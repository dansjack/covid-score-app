package com.nsc.covidscore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsc.covidscore.api.RequestSingleton;
import com.nsc.covidscore.api.Requests;
import com.nsc.covidscore.api.VolleyJsonCallback;
import com.nsc.covidscore.room.CovidSnapshot;
import com.nsc.covidscore.room.CovidSnapshotWithLocationViewModel;
import com.nsc.covidscore.room.EntityListener;
import com.nsc.covidscore.room.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Location currentLocation;
    private boolean currentLocationObserved = false;
    private CovidSnapshot currentSnapshot;
    private boolean currentCovidSnapshotObserved = false;

    private CovidSnapshotWithLocationViewModel vm;
    private RequestQueue queue;
    private RequestSingleton requestManager;

    private TextView tempDisplayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestManager = RequestSingleton.getInstance(this.getApplicationContext());
        queue = requestManager.getRequestQueue();
        tempDisplayTextView = findViewById(R.id.hello_world);

        // Access to Room Database
        vm = new ViewModelProvider(this).get(CovidSnapshotWithLocationViewModel.class);

        // Saves to DB when all the APIs have come in
        currentSnapshot = new CovidSnapshot();
        currentSnapshot.setListener(e -> {
            saveSnapshotToRoom();
            Log.e(TAG, "listener on currentSnapshot invoked!!");
        });

        // THESE DO NOT WORK (?)
//        currentSnapshot.setListener(evt -> {
//            saveSnapshotToRoom();
//            Log.e(TAG, "saveSnapshotToRoom invoked in currentSnapshot listener");
//        });

        // Set Observers (if Room has data)
        //setCovidSnapshotObserved();
        //setLocationObserved();

        // update textviews, etc with currentSnapshot & currentLocation, if present

        // Update Values - Maybe change this to only go when first submitted in dialog or refreshed
        // temp test data
        Location tempLocation = new Location("king", "washington");
        vm.insertLocation(tempLocation);
        currentLocation = tempLocation;
        tempLocation.setListener(e -> {
            saveLocationToRoom();
            Log.e(TAG, "listener on currentLocation invoked!!");
        });
//        currentLocation = new Location();
        makeApiCalls(tempLocation);

        Log.d(TAG,"onCreate invoked");
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            requestManager.getRequestQueue().cancelAll(TAG);
        }
        Log.d(TAG, "onStop invoked");
    }

    private void setCovidSnapshotObserved() {
        // Set Listener for Current Covid Snapshot - Add Else - Dialog
        if (!currentCovidSnapshotObserved && vm.getCurrentCovidSnapshot() != null) {
            vm.getCurrentCovidSnapshot().observe(this, new Observer<CovidSnapshot>() {
                @Override
                public void onChanged(@Nullable final CovidSnapshot covidSnapshotFromDb) {
                    // update cached version of snapshot
                    if (covidSnapshotFromDb != null && !currentSnapshot.equals(covidSnapshotFromDb)) {
                        currentSnapshot = covidSnapshotFromDb;
                        Log.e("currentSnapshot updated: ", currentSnapshot.toString());
                    }
                    else if (covidSnapshotFromDb == null) {
                        // run API call, if location is saved
                        // else, display some message about "Let's Get Started By Setting Location"
                    }
                }
            });
            currentCovidSnapshotObserved = true;
        }
    }

    private void setLocationObserved() {
        // Set Listener for Location
        if (!currentLocationObserved && vm.getCurrentLocation() != null) {
            vm.getCurrentLocation().observe(this, new Observer<Location>() {
                @Override
                public void onChanged(@Nullable final Location locationFromDb) {
                    // update cached version of location
                    if ((locationFromDb != null) && (!locationFromDb.equals(currentLocation))) {
                        currentLocation = locationFromDb;
                        Log.d(TAG, "new location set to :" + locationFromDb.toApiFormat());
                    } else {
                        // no location is saved
                        // pop up dialog
                    }
                }
            });
        }
        currentLocationObserved = true;
    }

    private void makeApiCalls(Location location) {
        if (currentSnapshot == null) {
            currentSnapshot = new CovidSnapshot();
        }
        Requests.getCounty(this, location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
                if (currentSnapshot == null) { currentSnapshot = new CovidSnapshot(); }
                // Add Location to Database (if not already present)
                Integer locationId = vm.insertLocation(location);
                currentSnapshot.setLocationId(locationId);
                JSONObject stats = (JSONObject) response.get("stats");
                Integer confirmed = (Integer) stats.get("confirmed");
                Integer deaths = (Integer) stats.get("deaths");
                // TODO: calculate better estimate of active cases
                Integer activeCounty = confirmed - deaths;
                currentSnapshot.setCountyActiveCount(activeCounty);
                Log.d(TAG, "getJsonData: county " + response);
            }

            @Override
            public void getJsonException(Exception exception) {
            }
        });
        Requests.getState(this, location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
                if (currentSnapshot == null) { currentSnapshot = new CovidSnapshot(); }
                Integer activeState = (Integer) response.get("active");
                currentSnapshot.setStateActiveCount(activeState);
                Log.d(TAG, "getJsonData: state " + response);
            }

            @Override
            public void getJsonException(Exception exception) {

            }
        });
        Requests.getCountyHistorical(this, location.toApiFormat(), "30", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {
                Log.d(TAG, "getJsonData: countyHistorical " + response);
            }

            @Override
            public void getJsonException(Exception exception) {
            }
        });
        Requests.getUSHistorical(this, "1", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException, IOException {
                if (currentSnapshot == null) { currentSnapshot = new CovidSnapshot(); }
                JSONObject timeline = response.getJSONObject("timeline");
                HashMap<String, Integer> totalMap = new ObjectMapper().readValue((timeline.get("cases")).toString(), HashMap.class);

                Integer totalCountry = 0;
                for (Object value : totalMap.values()) {
                    totalCountry = (Integer) value;
                }
                Integer deathCountry = 0;
                HashMap<String, Integer> deathMap = new ObjectMapper().readValue((timeline.get("deaths")).toString(), HashMap.class);
                for (Object value : deathMap.values()) {
                    deathCountry = (Integer) value;
                }
                Integer recoveredCountry = 0;
                HashMap<String, Integer> recoveredMap = new ObjectMapper().readValue((timeline.get("recovered")).toString(), HashMap.class);
                for (Object value : recoveredMap.values()) {
                    recoveredCountry = (Integer) value;
                }
                currentSnapshot.setCountryActiveCount(totalCountry - deathCountry - recoveredCountry);
                Log.d(TAG, "getJsonData: country " + response);
            }

            @Override
            public void getJsonException(Exception exception) {

            }
        });
    }

    public void saveSnapshotToRoom() {
        if (currentSnapshot.hasFieldsSet()) {
            // TODO: set textfields here!
            if (currentLocation == null) {
                tempDisplayTextView.setText("Most Recent Snapshot:\n" + currentSnapshot.toString());
            } else {
                tempDisplayTextView.setText("Most Recent Location: \n" + currentLocation.toApiFormat()
                        + "\nMost Recent Snapshot: \n" + currentSnapshot.toString());
            }
            Calendar calendar = Calendar.getInstance();
            currentSnapshot.setLastUpdated(calendar);
            vm.insertCovidSnapshot(currentSnapshot);
        } else {
            Log.e(TAG, "189: " + currentSnapshot.toString());
        }
//        setCovidSnapshotObserved();
        Log.d(TAG, "saveSnapshotToRoom invoked");
    }

    public void saveLocationToRoom() {
        if (currentLocation.hasFieldsSet()) {
            Calendar calendar = Calendar.getInstance();
            currentLocation.setLastUpdated(calendar);
            vm.insertLocation(currentLocation);
        } else {
            Log.e(TAG, "did not save to Room:  " + currentLocation.toApiFormat());
        }
//        setLocationObserved();
        Log.d(TAG, "saveLocationToRoom invoked");
    }
}

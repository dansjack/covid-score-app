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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Location currentLocation;
    private CovidSnapshot currentSnapshot;

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

        // Set Listener for Current Covid Snapshot - Add Else - Dialog
        if (vm.getCurrentCovidSnapshot() != null) {
            vm.getCurrentCovidSnapshot().observe(this, new Observer<CovidSnapshot>() {
                @Override
                public void onChanged(@Nullable final CovidSnapshot covidSnapshotFromDb) {
                    // update cached version of snapshot
                    // TODO: if !currentSnapshot.equals(covidSnapshotFromDb)
                    currentSnapshot = covidSnapshotFromDb;
                    if (currentSnapshot == null) {
                        // run API call, if location is saved
                        // else, display some message about "Let's Get Started By Setting Location"
                    } else {
                        Log.e("currentSnapshot updated: ", currentSnapshot.toString());
                    }
                }
            });
        }

        // Saves to DB when all the APIs have come in
        currentSnapshot.setListener(evt -> {
            saveSnapshotToRoom();
        });

        // Set Listener for Location
        if (vm.getCurrentLocation() != null) {
            vm.getCurrentLocation().observe(this, new Observer<Location>() {
                @Override
                public void onChanged(@Nullable final Location locationFromDb) {
                    // update cached version of location
                    currentLocation = locationFromDb;
                    Log.d(TAG, "new location set to :" + locationFromDb.toApiFormat());
                    if (currentLocation == null) {
                        // pop up dialog
                    } else {
                        Log.e("Main 71: ", currentLocation.toApiFormat());
                    }
                }
            });
        }

        // update textviews, etc with currentSnapshot & currentLocation, if present

        // Update Values - Maybe change this to only go when first submitted in dialog or refreshed
        // temp test data
        Location tempLocation = new Location("king", "washington");
        vm.insertLocation(tempLocation);
        currentLocation = tempLocation;
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

    private void makeApiCalls(Location location) {
        currentSnapshot = new CovidSnapshot();
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
                //saveSnapshotToRoom();
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
                //saveSnapshotToRoom();
                Log.d(TAG, "getJsonData: state " + response);
            }

            @Override
            public void getJsonException(Exception exception) {

            }
        });
        Requests.getCountyHistorical(this, location.toApiFormat(), "30", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {
                //saveSnapshotToRoom();
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
                //saveSnapshotToRoom();
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
        Log.d(TAG, "saveSnapshotToRoom invoked");
    }
}

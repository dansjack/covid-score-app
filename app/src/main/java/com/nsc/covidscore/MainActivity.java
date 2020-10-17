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
import com.nsc.covidscore.room.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Location currentLocation;
    // so that we only set one observer on Room Location
    private boolean roomLocationObserved = false;
    private CovidSnapshot currentSnapshot;
    // so that we only set one observer on Room CovidSnapshot
    private boolean roomCovidSnapshotObserved = false;

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

        // Set Room Data, if saved
        currentSnapshot = vm.getSavedCovidSnapshot();
        currentLocation = vm.getSavedLocation();

        // Attempts to save CovidSnapshot to DB whenever the local variable is changed
        // - if the fields aren't fully set, it will not insert
        if (currentSnapshot == null) {
            currentSnapshot = new CovidSnapshot();
        }
        currentSnapshot.setListener(e -> {
            if (currentSnapshot.hasFieldsSet()) {
                try {
                    saveSnapshotToRoom();
                } catch (InterruptedException err) {
                    Log.d(TAG, err.getMessage());
                }
            }            Log.d(TAG, "listener on currentSnapshot invoked!!");
        });

        // temp test data - remove
        Location tempLocation = new Location("king", "washington");
        currentLocation = tempLocation;

        if (currentLocation == null) {
            // TODO: pop up dialog here?
        }

        // Attempts to save Location to DB whenever the local variable is changed
        // - if the fields aren't fully set, it will not insert
        if (currentLocation.hasFieldsSet()) {
            try {
                saveLocationToRoom();
            } catch (InterruptedException e) {
                Log.d(TAG, e.getMessage());
            }
        }
        currentLocation.setListener(e -> {
            try {
                saveLocationToRoom();
            } catch (InterruptedException err) {
                Log.d(TAG, err.getMessage());
            }
            Log.d(TAG, "listener on currentLocation invoked!!");
        });

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

    /**
     * This sets an observer on the Room function that returns the most recent addition to the db
     * When the new CovidSnapshot comes through, save to local variable and display to user
     */
    private void setRoomCovidSnapshotObserved() {
        // Set Listener for Current Covid Snapshot - Add Else - Dialog
        if (!roomCovidSnapshotObserved && vm.getLatestCovidSnapshot().getValue() != null) {
            vm.getLatestCovidSnapshot().observe(this, new Observer<CovidSnapshot>() {
                @Override
                public void onChanged(@Nullable final CovidSnapshot covidSnapshotFromDb) {
                    // update cached version of snapshot
                    if (covidSnapshotFromDb != null && !currentSnapshot.equals(covidSnapshotFromDb)) {
                        currentSnapshot = covidSnapshotFromDb;
                        // TODO: set textfields here! - vv this is temporary vv
                        if (currentLocation == null) { // this shouldn't be hit because currentLocation shouldn't be null
                            tempDisplayTextView.setText("Most Recent Snapshot:\n" + currentSnapshot.toString());
                        } else {
                            tempDisplayTextView.setText("Most Recent Location: id: \n" + currentLocation.getLocationId() + ", " + currentLocation.toApiFormat()
                                    + "\nMost Recent Snapshot: \n" + covidSnapshotFromDb.toString());
                        }
                        Log.e(TAG, "CovidSnapshot Room listener invoked");
                    }
                    else if (covidSnapshotFromDb == null) {
                        // run API call, if location is saved
                        // else, display some message about "Let's Get Started By Setting Location"
                    }
                }
            });
            roomCovidSnapshotObserved = true;
        }
    }

    /**
     * This sets an observer on the Room function that returns the most recent addition to the db
     * When the new Location comes through, ?
     */
    private void setRoomLocationObserved() {
        // Set Listener for Location
        if (!roomLocationObserved && vm.getLatestLocation() != null) {
            vm.getLatestLocation().observe(this, new Observer<Location>() {
                @Override
                public void onChanged(@Nullable final Location locationFromDb) {
                    // update cached version of location
                    if ((locationFromDb != null) && (!locationFromDb.equals(currentLocation))) {
                        currentLocation = locationFromDb;
                        currentSnapshot.setLocationId(locationFromDb.getLocationId());
                        Log.d(TAG, "new location set to :" + locationFromDb.toApiFormat());
                    } else {
                        Log.e(TAG, "Error with location: " + locationFromDb.toApiFormat());
                        // no location is saved
                        // pop up dialog
                    }
                }
            });
        }
        roomLocationObserved = true;
    }

    private void makeApiCalls(Location location) {
        Requests.getCounty(this, location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
                if (currentSnapshot == null) { currentSnapshot = new CovidSnapshot(); }
                // Add Location to Database (if not already present)
                vm.insertLocation(location);
                JSONObject stats = (JSONObject) response.get("stats");
                Integer confirmed = (Integer) stats.get("confirmed");
                Integer deaths = (Integer) stats.get("deaths");
                // TODO: calculate better estimate of active cases
                Integer activeCounty = confirmed - deaths;
                currentSnapshot.setCountyActiveCount(activeCounty);
                if (currentSnapshot.hasFieldsSet()) {
                    try {
                        saveSnapshotToRoom();
                    } catch (InterruptedException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }                Log.d(TAG, "getJsonData: county " + response);
            }

            @Override
            public void getJsonException(Exception exception) {
            }
        });
        Requests.getState(this, location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
                if (currentSnapshot == null) { currentSnapshot = new CovidSnapshot(); }
                // Add Location to Database (if not already present)
                vm.insertLocation(location);
                Integer activeState = (Integer) response.get("active");
                currentSnapshot.setStateActiveCount(activeState);
                if (currentSnapshot.hasFieldsSet()) {
                    try {
                        saveSnapshotToRoom();
                    } catch (InterruptedException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
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
                // Add Location to Database (if not already present)
                vm.insertLocation(location);
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
                if (currentSnapshot.hasFieldsSet()) {
                    try {
                        saveSnapshotToRoom();
                    } catch (InterruptedException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }                Log.d(TAG, "getJsonData: country " + response);
            }

            @Override
            public void getJsonException(Exception exception) {

            }
        });
    }

    public void saveSnapshotToRoom() throws InterruptedException {
        if (currentSnapshot.hasFieldsSet()) {
            // make sure to set LocationIdFK on Snapshot to current LocationIdPK
            if (currentSnapshot.getLocationId() == null || currentSnapshot.getLocationId() == 0) {
                if (currentLocation.getLocationId() == null) {
                    saveLocationToRoom();
                    // timing issue here?
                    currentLocation = vm.getLatestLocation().getValue();
                }
                currentSnapshot.setLocationId(currentLocation.getLocationId());
            }
            Calendar calendar = Calendar.getInstance();
            currentSnapshot.setLastUpdated(calendar);
            vm.insertCovidSnapshot(currentSnapshot);
            setRoomCovidSnapshotObserved();
        } else {
            Log.e(TAG, "Did not save Snapshot to Room: " + currentSnapshot.toString());
        }
        Log.d(TAG, "saveSnapshotToRoom invoked");
    }

    public void saveLocationToRoom() throws InterruptedException {
        if (currentLocation.hasFieldsSet()) {
            Calendar calendar = Calendar.getInstance();
            currentLocation.setLastUpdated(calendar);
            vm.insertLocation(currentLocation);
            setRoomLocationObserved();
        } else {
            Log.e(TAG, "Did not save Location to Room:  " + currentLocation.toApiFormat());
        }
//        setLocationObserved();
        Log.d(TAG, "saveLocationToRoom invoked");
    }
}

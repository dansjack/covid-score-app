package com.nsc.covidscore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Location currentLocation;
    private LiveData<Location> liveLatestLocation;
    private List<Location> savedLocations = new ArrayList<>();
    private LiveData<CovidSnapshot> liveLatestCovidSnapshot;
    private CovidSnapshot currentSnapshot;

    private CovidSnapshotWithLocationViewModel vm;
    private RequestQueue queue;
    private RequestSingleton requestManager;

    private TextView tempLocationTextView;
    private TextView tempSnapshotTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestManager = RequestSingleton.getInstance(this.getApplicationContext());
        queue = requestManager.getRequestQueue();
        tempLocationTextView = findViewById(R.id.location);
        tempSnapshotTextView = findViewById(R.id.snapshot);

        // Access to Room Database
        vm = new ViewModelProvider(this).get(CovidSnapshotWithLocationViewModel.class);

        // Test to make sure Location Insertion works
        vm.getAllLocations().observe(this, allLocations -> {
            if (allLocations != null && allLocations.size() > 1) {
                Log.e(TAG, "first added Location: " + allLocations.get(0).toString());
                Log.e(TAG, "second added Location: " + allLocations.get(1).toString());
            }
        });

        // These variables will hold latest copies of Room rows
        liveLatestCovidSnapshot = vm.getLatestCovidSnapshot();
        liveLatestLocation = vm.getLatestLocation();

        // These functions will set observers on those, in case they change
        setRoomCovidSnapshotObserved();
        setRoomLocationObserved();

        // Attempts to save CovidSnapshot to DB whenever the local variable is changed
        // - if the fields aren't fully set, it will not insert
        if (currentSnapshot == null) {
            currentSnapshot = new CovidSnapshot();
        }
        currentSnapshot.setListener(e -> {
            if (currentSnapshot.hasFieldsSet()) {
                saveSnapshotToRoom();
            }
        });

        // temp test data - remove
//        Location tempLocation = new Location("king", "washington");
//        currentLocation = tempLocation;

        if (currentLocation == null) {
            Location temp = new Location("", "", "", "");
            currentLocation = temp;
            // there is no previously saved location
            // TODO: pop up dialog here?
        }

        currentLocation.setListener(e -> {
            Log.e(TAG, "currentLocation listener invoked");
        });

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
        if (liveLatestCovidSnapshot != null && !liveLatestCovidSnapshot.hasActiveObservers()) {
            liveLatestCovidSnapshot.observe(this, new Observer<CovidSnapshot>() {
                @Override
                public void onChanged(@Nullable final CovidSnapshot covidSnapshotFromDb) {
                    // update cached version of snapshot
                    currentSnapshot = liveLatestCovidSnapshot.getValue() != null ? liveLatestCovidSnapshot.getValue() : currentSnapshot;
                    if (covidSnapshotFromDb != null || (currentSnapshot != null && currentSnapshot.hasFieldsSet())) {
                        currentSnapshot = covidSnapshotFromDb == null ? covidSnapshotFromDb : currentSnapshot;
                        // TODO: set textfields here! - vv this is temporary vv
                        if (currentLocation == null || !currentLocation.hasFieldsSet()) {
                            currentLocation = liveLatestLocation.getValue();
                            tempSnapshotTextView.setText("Most Recent Snapshot:\n" + currentSnapshot.toString());
                        }
                        if (currentLocation != null) {
                            tempLocationTextView.setText("Most Recent Location: id: \n" + currentLocation.getLocationId() + ", " + currentLocation.toApiFormat());
                            tempSnapshotTextView.setText("Most Recent Snapshot: " + currentSnapshot.toString());
                        }
                        Log.e(TAG, "CovidSnapshot Room listener invoked");
                    }
                    else if (covidSnapshotFromDb == null) {
                        Log.e(TAG, "Observer returned null CovidSnapshot");
                        // run API call, if location is saved
                        if (currentLocation != null) {
                            makeApiCalls(currentLocation);
                        } else {
                            currentLocation = liveLatestLocation.getValue();
                        }
                    }
                }
            });
        }
    }

    /**
     * This sets an observer on the Room function that returns the most recently accessed Location in DB
     */
    private void setRoomLocationObserved() {
        // Set Listener for Location
        if (liveLatestLocation != null && !liveLatestLocation.hasActiveObservers()) {
            liveLatestLocation.observe(this, new Observer<Location>() {
                @Override
                public void onChanged(@Nullable final Location locationFromDb) {
                    // update cached version of location
                    currentLocation = liveLatestLocation.getValue() != null ? liveLatestLocation.getValue() : currentLocation;
                    if (locationFromDb != null) {
                        currentLocation = locationFromDb;
                        savedLocations.add(currentLocation);
                        if (!currentSnapshot.getLocationId().equals(currentLocation.getLocationId())) {
                            makeApiCalls(currentLocation);
                        }
                        Log.e(TAG, "Most recently updated Location : " + currentLocation.toApiFormat());
                        tempLocationTextView.setText("Most recent Location : id: " + currentLocation.getLocationId() + ", " + currentLocation.toApiFormat());
                    } else {
                        Log.d(TAG, "Location observer returned null");
                        if (currentSnapshot != null && currentLocation.getLocationId() != null) {
                            currentLocation = vm.getLocationById(currentSnapshot.getLocationId()).getValue();
                        } else {
                            // no location is saved
                            // location selection fragment
                        }
                    }
                }
            });
        }
    }

    private void makeApiCalls(Location location) {
        Requests.getCounty(this, location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
                if (currentSnapshot == null) { currentSnapshot = new CovidSnapshot(); }
                JSONObject stats = (JSONObject) response.get("stats");
                Integer confirmed = (Integer) stats.get("confirmed");
                Integer deaths = (Integer) stats.get("deaths");
                // TODO: calculate better estimate of active cases
                Integer activeCounty = confirmed - deaths;
                currentSnapshot.setCountyActiveCount(activeCounty);
                if (currentSnapshot.hasFieldsSet()) {
                        saveSnapshotToRoom();
                }
                Log.d(TAG, "getJsonData: county " + response);
            }

            @Override
            public void getJsonException(Exception exception) {}

            @Override
            public void getString(String response) {

            }
        });
        Requests.getState(this, location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
                if (currentSnapshot == null) { currentSnapshot = new CovidSnapshot(); }
                Integer activeState = (Integer) response.get("active");
                currentSnapshot.setStateActiveCount(activeState);
                if (currentSnapshot.hasFieldsSet()) {
                    saveSnapshotToRoom();
                }
                Log.d(TAG, "getJsonData: state " + response);
            }

            @Override
            public void getJsonException(Exception exception) {}

            @Override
            public void getString(String response) {}
        });
        Requests.getCountyHistorical(this, location.toApiFormat(), "30", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {
                Log.d(TAG, "getJsonData: countyHistorical " + response);
            }

            @Override
            public void getJsonException(Exception exception) {}

            @Override
            public void getString(String response) {}
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
                if (currentSnapshot.hasFieldsSet()) {
                    saveSnapshotToRoom();
                }
                Log.d(TAG, "getJsonData: country " + response);
            }

            @Override
            public void getJsonException(Exception exception) {}

            @Override
            public void getString(String response) {}
        });
        Requests.getCountyPopulation(this, location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {}

            @Override
            public void getJsonException(Exception exception) {}

            @Override
            public void getString(String response) {
                if (currentSnapshot == null) { currentSnapshot = new CovidSnapshot(); }
                currentSnapshot.setCountyTotalPopulation(Integer.parseInt(response));
                if (currentSnapshot.hasFieldsSet()) {
                    saveSnapshotToRoom();
                }
                Log.d(TAG, "getStringData: County " + response);
            }
        });
        Requests.getStatePopulation(this, location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {}

            @Override
            public void getJsonException(Exception exception) {}

            @Override
            public void getString(String response) {
                if (currentSnapshot == null) { currentSnapshot = new CovidSnapshot(); }
                currentSnapshot.setStateTotalPopulation(Integer.parseInt(response));
                if (currentSnapshot.hasFieldsSet()) {
                    saveSnapshotToRoom();
                }
                Log.d(TAG, "getStringData: State  " + response);
            }
        });

        Requests.getCountryPopulation(this, new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {}

            @Override
            public void getJsonException(Exception exception) {}

            @Override
            public void getString(String response) {
                if (currentSnapshot == null) { currentSnapshot = new CovidSnapshot(); }
                currentSnapshot.setCountryTotalPopulation(Integer.parseInt(response));
                if (currentSnapshot.hasFieldsSet()) {
                    saveSnapshotToRoom();
                }
                Log.d(TAG, "getStringData: Country " + response);
            }
        });
    }

    public void saveSnapshotToRoom() {
        CovidSnapshot mostRecent = liveLatestCovidSnapshot.getValue();
        if (currentSnapshot != null && currentSnapshot.hasFieldsSet()) {
            // make sure to set LocationIdFK on Snapshot to current LocationIdPK
            if (currentSnapshot.getLocationId() == null || currentSnapshot.getLocationId() == 0) {
                if (currentLocation.getLocationId() == null) {
                    currentLocation = liveLatestLocation.getValue();
                }
                currentSnapshot.setLocationId(currentLocation != null ? currentLocation.getLocationId() : -1);
            }
            if (!currentSnapshot.hasSameData(mostRecent)) {
                Calendar calendar = Calendar.getInstance();
                currentSnapshot.setLastUpdated(calendar);
                vm.insertCovidSnapshot(currentSnapshot);
            } else {
                Log.e(TAG, "Skipped inserting Snapshot ID: " + currentSnapshot.getCovidSnapshotId());
            }
        } else {
            Log.e(TAG, "Incomplete Snapshot: " + currentSnapshot.toString());
        }
        Log.d(TAG, "saveSnapshotToRoom invoked");
    }

}

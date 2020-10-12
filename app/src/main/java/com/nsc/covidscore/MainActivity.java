package com.nsc.covidscore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.nsc.covidscore.api.RequestSingleton;
import com.nsc.covidscore.api.Requests;
import com.nsc.covidscore.api.VolleyJsonCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Location currentLocation;
    private CovidSnapshot currentSnapshot;

    private CovidSnapshotWithLocationViewModel vm;
    private RequestQueue queue;
    private RequestSingleton requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestManager = RequestSingleton.getInstance(this.getApplicationContext());
        queue = requestManager.getRequestQueue();

        // Access to Room Database
        vm = new ViewModelProvider(this).get(CovidSnapshotWithLocationViewModel.class);

        // Set Listener for Current Covid Snapshot - Add Else - Dialog
        if (vm.getCurrentCovidSnapshot() != null) {
            vm.getCurrentCovidSnapshot().observe(this, new Observer<CovidSnapshot>() {
                @Override
                public void onChanged(@Nullable final CovidSnapshot covidSnapshotFromDb) {
                    // update cached version of snapshot
                    currentSnapshot = covidSnapshotFromDb;
                    if (currentSnapshot == null) {
                        // run API call, if location is saved
                        // else, display some message about "Let's Get Started By Setting Location"
                    }
                }
            });
        }

        // Set Listener for Location
        if (vm.getCurrentLocation() != null) {
            vm.getCurrentLocation().observe(this, new Observer<Location>() {
                @Override
                public void onChanged(@Nullable final Location locationFromDb) {
                    // update cached version of location
                    currentLocation = locationFromDb;
                    if (currentLocation == null) {
                        // pop up dialog
                    }
                }
            });
        }

        // update textviews, etc with currentSnapshot & currentLocation

        // Update Values - Maybe change this to only go when first submitted in dialog or refreshed
        // temp test data
        Location tempLocation = new Location("king", "washington");
        makeApiCalls(tempLocation);



        Log.d(TAG,"onCreate invoked");
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            requestManager.getRequestQueue().cancelAll(TAG);
        }
    }

    private void makeApiCalls(Location location) {
        CovidSnapshot currentSnapshot = new CovidSnapshot();
        Requests.getCounty(this, location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
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
            public void getJsonData(JSONObject response) throws JSONException {

                JSONObject timeline = response.getJSONObject("timeline");
                Integer totalCountry = 0;
                Map activeMap = (Map) timeline.get("cases");
                for (Object value : ((Map) activeMap).values()) {
                    totalCountry = (Integer) value;
                }
                Integer deathCountry = 0;
                Map deathMap = (Map) timeline.get("deaths");
                for (Object value : ((Map) deathMap).values()) {
                    deathCountry = (Integer) value;
                }
                Integer recoveredCountry = 0;
                Map recoveredMap = (Map) timeline.get("recovered");
                for (Object value : ((Map) recoveredMap).values()) {
                    recoveredCountry = (Integer) value;
                }
                currentSnapshot.setCountryActiveCount(totalCountry - deathCountry - recoveredCountry);
                Log.d(TAG, "getJsonData: country " + response);
            }

            @Override
            public void getJsonException(Exception exception) {

            }
        });
        Calendar calendar = Calendar.getInstance();
        currentSnapshot.setLastUpdatedApi(calendar);
        currentSnapshot.setLastUpdatedRoom(calendar);
        vm.insertCovidSnapshot(currentSnapshot);
    }
}

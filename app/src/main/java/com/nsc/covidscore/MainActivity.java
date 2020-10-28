package com.nsc.covidscore;

import android.app.FragmentManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.nsc.covidscore.api.RequestSingleton;
//import com.nsc.covidscore.room.CovidSnapshot;
//import com.nsc.covidscore.room.CovidSnapshotWithLocationViewModel;
//import com.nsc.covidscore.room.Location;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private HashMap<String, List<Location>> mapOfLocations = new HashMap<>();

    private Location lastLocation;
    private List<Location> savedLocations = new ArrayList<>();
    private LiveData<CovidSnapshot> liveCovidSnapshot;
    private CovidSnapshot lastSnapshot;

    private CovidSnapshotWithLocationViewModel vm;
    private RequestQueue queue;
    private RequestSingleton requestManager;

    private FragmentAdapter mFragmentAdapter;
    private Fragment mFragment;
    private ViewPager mViewPager;
    private FragmentAdapter pagerAdapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        fillLocationsMap();

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragContainer) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            boolean hasSavedCovidSnapshot = false;

            if (!hasSavedCovidSnapshot) {

                // Create a new Location Selection Fragment to be placed in the activity layout
                LocationManualSelectionFragment locationManualSelectionFragment = new LocationManualSelectionFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("allLocationsMap", mapOfLocations);

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                locationManualSelectionFragment.setArguments(bundle);

                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragContainer, locationManualSelectionFragment, "lmsf").commit();
            } else {
                // Create a new Risk Detail Fragment to be placed in the activity layout
                RiskDetailPageFragment riskDetailPageFragment = new RiskDetailPageFragment();
                Bundle bundle = new Bundle();

                // Save current CovidSnapshot and Location to this bundle

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                riskDetailPageFragment.setArguments(bundle);

                // Add the fragment to the "Fragment_container" FrameLayout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragContainer, riskDetailPageFragment, "rdpf").commit();
            }
        }

        requestManager = RequestSingleton.getInstance(this.getApplicationContext());
        queue = requestManager.getRequestQueue();

        // Access to Room Database
        vm = new ViewModelProvider(this).get(CovidSnapshotWithLocationViewModel.class);

        // This variable will hold latest copies of Room rows
        lastCovidSnapshot = vm.getLatestCovidSnapshot();

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
        Location tempLocation = new Location("king", "washington");
        currentLocation = tempLocation;

        if (currentLocation == null) {
            // there is no previously saved location
            // TODO: pop up dialog here?
        }

        makeApiCalls(tempLocation);

        Log.d(TAG,"onCreate invoked");
    }

    public void setViewPager(int fragmentNumber){
        mViewPager.setCurrentItem(fragmentNumber);
    }

    /**
     * This sets an observer on the Room function that returns the most recent addition to the db
     * When the new CovidSnapshot comes through, save to local variable and display to user
     */
    private void setRoomCovidSnapshotObserved() {
        // Set Listener for Current Covid Snapshot - Add Else - Dialog
        if (!roomCovidSnapshotObserved && liveCovidSnapshot != null) {
            liveCovidSnapshot.observe(this, new Observer<CovidSnapshot>() {
                @Override
                public void onChanged(@Nullable final CovidSnapshot covidSnapshotFromDb) {
                    // update cached version of snapshot
                    currentSnapshot = liveCovidSnapshot.getValue() != null ? liveCovidSnapshot.getValue() : currentSnapshot;
                    if (covidSnapshotFromDb != null || (currentSnapshot != null && currentSnapshot.hasFieldsSet())) {
                        currentSnapshot = covidSnapshotFromDb == null ? covidSnapshotFromDb : currentSnapshot;
                        // TODO: set textfields here! - vv this is temporary vv
                        if (currentLocation == null) { // this shouldn't be hit because currentLocation shouldn't be null
                            currentLocation = liveLocation.getValue();
                            tempDisplayTextView.setText("Most Recent Snapshot:\n" + currentSnapshot.toString());
                        } else {
                            tempDisplayTextView.setText("Most Recent Location: id: \n" + currentLocation.getLocationId() + ", " + currentLocation.toApiFormat()
                                    + "\nMost Recent Snapshot: \n" + currentSnapshot.toString());
                        }
                        Log.e(TAG, "CovidSnapshot Room listener invoked");
                    }
                    else if (covidSnapshotFromDb == null) {
                        Log.e(TAG, "Observer returned null CovidSnapshot");
                        // run API call, if location is saved
                    }
                }
            });
            roomCovidSnapshotObserved = true;
        }
    }

    private void makeApiCalls(Location location) {
        Requests.getCounty(this, location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
                if (currentSnapshot == null) { currentSnapshot = new CovidSnapshot(); }
                // Add Location to Database (if not already present)
                if (!currentLocation.hasSameData(location)) {
                    vm.insertLocation(location);
                }
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
                // Add Location to Database (if not already present)
                if (!currentLocation.hasSameData(location)) {
                    vm.insertLocation(location);
                }                Integer activeState = (Integer) response.get("active");
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
                // Add Location to Database (if not already present)
                if (!currentLocation.hasSameData(location)) {
                    vm.insertLocation(location);
                }
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
                vm.insertLocation(location);
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
                vm.insertLocation(location);
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
                vm.insertLocation(location);
                currentSnapshot.setCountryTotalPopulation(Integer.parseInt(response));
                if (currentSnapshot.hasFieldsSet()) {
                    saveSnapshotToRoom();
                }
                Log.d(TAG, "getStringData: Country " + response);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LocationManualSelectionFragment tLmsf = (LocationManualSelectionFragment) getSupportFragmentManager().findFragmentByTag("lmsf");
        if (tLmsf != null && tLmsf.isVisible()) {
            LocationManualSelectionFragment locationManualSelectionFragment = new LocationManualSelectionFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("allLocationsMap", mapOfLocations);

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            locationManualSelectionFragment.setArguments(bundle);

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragContainer, locationManualSelectionFragment, "lmsf").commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            requestManager.getRequestQueue().cancelAll(TAG);
        }
        Log.d(TAG, "onStop invoked");
    }

    private void fillLocationsMap() {
        String jsonString;
        JSONArray jsonArray;
        AssetManager assetManager = this.context.getAssets();
        try {
            InputStream inputStream = assetManager.open("county_fips.json");
            byte[] buffer = new byte[inputStream.available()];
            int read = inputStream.read(buffer);
            if (read == -1) {
                inputStream.close();
            }
            jsonString = new String(buffer, StandardCharsets.UTF_8);
            jsonArray = new JSONArray(jsonString);
            for (int i = 1; i < jsonArray.length(); i++) {
                JSONArray currentArray = jsonArray.getJSONArray(i);
                // split county and state names
                String[] nameArray = currentArray.getString(0).split(",");
                String countyName = nameArray[0].trim();
                String stateName = nameArray[1].trim();
                String stateFips = currentArray.getString(1);
                String countyFips = currentArray.getString(2);
                Location countyInState = new Location(countyName, stateName, stateFips, countyFips);

                if (mapOfLocations.get(stateName) == null) {
                    Log.i(TAG, "fillLocationsMap: " + stateName);
                    mapOfLocations.put(stateName, new ArrayList<>());
                    mapOfLocations.get(stateName).add(countyInState);
                } else {
                    mapOfLocations.get(stateName).add(countyInState);
                }
            }

        } catch (IOException | JSONException exception) {
            exception.printStackTrace();
        }
    }
}

    public void saveSnapshotToRoom() {
        if (currentSnapshot != null && currentSnapshot.hasFieldsSet()) {
            // make sure to set LocationIdFK on Snapshot to current LocationIdPK
            if (currentSnapshot.getLocationId() == null || currentSnapshot.getLocationId() == 0) {
                if (currentLocation.getLocationId() == null) {
                    saveLocationToRoom();
                    currentLocation = liveLocation.getValue();
                }
                currentSnapshot.setLocationId(currentLocation != null ? currentLocation.getLocationId() : -1);

            }
            Calendar calendar = Calendar.getInstance();
            currentSnapshot.setLastUpdated(calendar);
            vm.insertCovidSnapshot(currentSnapshot);
        } else {
            Log.e(TAG, "Incomplete Snapshot: " + currentSnapshot.toString());
        }
        Log.d(TAG, "saveSnapshotToRoom invoked");
    }

}

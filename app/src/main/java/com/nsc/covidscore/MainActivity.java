package com.nsc.covidscore;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsc.covidscore.api.RequestSingleton;
import com.nsc.covidscore.api.Requests;
import com.nsc.covidscore.api.VolleyJsonCallback;
//import com.nsc.covidscore.room.CovidSnapshot;
//import com.nsc.covidscore.room.CovidSnapshotWithLocationViewModel;
//import com.nsc.covidscore.room.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Location currentLocation;
    private HashMap<String, List<Location>> mapOfLocations = new HashMap<>();
    private LiveData<Location> liveLocation;
    private List<Location> savedLocations = new ArrayList<>();
    // so that we only set one observer on Room Location
    private boolean roomLocationObserved = false;
    private MutableLiveData<String> mutableSelectedCountyMain = new MutableLiveData<>();

    //    private LiveData<CovidSnapshot> liveCovidSnapshot;
//    private CovidSnapshot currentSnapshot;
    // so that we only set one observer on Room CovidSnapshot
    private boolean roomCovidSnapshotObserved = false;

//    private CovidSnapshotWithLocationViewModel vm;
    private RequestQueue queue;
    private RequestSingleton requestManager;

//    private TextView tempDisplayTextView;

    private FragmentAdapter mFragmentAdapter;
    private Fragment mFragment;
    private ViewPager mViewPager;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        fillLocationsMap();

        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.frag_placeholder);
        setupViewPager(mViewPager);

        requestManager = RequestSingleton.getInstance(this.getApplicationContext());
        queue = requestManager.getRequestQueue();

        mutableSelectedCountyMain.observe(this, selectedCounty -> {
            if (selectedCounty != null) {
                Log.i(TAG, "onCreate: NOT NULL");
            }
        });

        // Access to Room Database
//        vm = new ViewModelProvider(this).get(CovidSnapshotWithLocationViewModel.class);

        // Set Room Data to local variables, if saved
//        currentSnapshot = vm.getSavedCovidSnapshot();
//        currentLocation = vm.getSavedLocation();

        // These variables will hold latest copies of Room rows
//        liveCovidSnapshot = vm.getLatestCovidSnapshot();
//        liveLocation = vm.getLatestLocation();

        // These functions will set observers on those, in case they change
//        setRoomCovidSnapshotObserved();
//        setRoomLocationObserved();

        // Attempts to save CovidSnapshot to DB whenever the local variable is changed
        // - if the fields aren't fully set, it will not insert
//        if (currentSnapshot == null) {
//            currentSnapshot = new CovidSnapshot();
//        }
//        currentSnapshot.setListener(e -> {
//            if (currentSnapshot.hasFieldsSet()) {
////                saveSnapshotToRoom();
//            }
//        });


        // temp test data - remove
        Location tempLocation = new Location("washington", "king", "99", "99");
        currentLocation = tempLocation;

        if (currentLocation == null) {
            // there is no previously saved location
            // TODO: pop up dialog here?
        }

        // Attempts to save Location to DB whenever the local variable is changed
        // - if the fields aren't fully set, it will not insert
//        if (currentLocation.hasFieldsSet()) {
//            saveLocationToRoom();
//        }
//        currentLocation.setListener(e -> {
//            saveLocationToRoom();
//        });

//        makeApiCalls(currentLocation);

        Log.d(TAG,"onCreate invoked");
    }

    private void setupViewPager(ViewPager viewPager){
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        // adapter.addFragment(new WelcomePageFragment(), "WelcomePageFragment");
        LocationManualSelectionFragment locationManualSelectionFragment = new LocationManualSelectionFragment();
        Bundle bundle = new Bundle();
        Log.i(TAG, "MAP: " + mapOfLocations.toString());
        bundle.putSerializable("allLocationsMap", mapOfLocations);
        locationManualSelectionFragment.setArguments(bundle);
        adapter.addFragment(locationManualSelectionFragment, "LocationManualSelectionFragment");
        adapter.addFragment(new RiskDetailPageFragment(), "RiskDetailPageFragment");
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber){
        mViewPager.setCurrentItem(fragmentNumber);
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

    /**
     * This sets an observer on the Room function that returns the most recent addition to the db
     * When the new CovidSnapshot comes through, save to local variable and display to user
     */
//    private void setRoomCovidSnapshotObserved() {
//        // Set Listener for Current Covid Snapshot - Add Else - Dialog
//        if (!roomCovidSnapshotObserved && liveCovidSnapshot != null) {
//            liveCovidSnapshot.observe(this, new Observer<CovidSnapshot>() {
//                @Override
//                public void onChanged(@Nullable final CovidSnapshot covidSnapshotFromDb) {
//                    // update cached version of snapshot
//                    currentSnapshot = liveCovidSnapshot.getValue() != null ? liveCovidSnapshot.getValue() : currentSnapshot;
//                    if (covidSnapshotFromDb != null || (currentSnapshot != null && currentSnapshot.hasFieldsSet())) {
//                        currentSnapshot = covidSnapshotFromDb == null ? covidSnapshotFromDb : currentSnapshot;
//                        // TODO: set textfields here! - vv this is temporary vv
//                        if (currentLocation == null) { // this shouldn't be hit because currentLocation shouldn't be null
//                            currentLocation = liveLocation.getValue();
////                            tempDisplayTextView.setText("Most Recent Snapshot:\n" + currentSnapshot.toString());
//                            Toast.makeText
//                                    (context, "Most Recent Snapshot:\n" + currentSnapshot.toString(), Toast.LENGTH_SHORT).show();
//                        } else {
////                            tempDisplayTextView.setText("Most Recent Location: id: \n" + currentLocation.getLocationId() + ", " + currentLocation.toApiFormat()
////                                    + "\nMost Recent Snapshot: \n" + currentSnapshot.toString());
//                            Toast.makeText
//                                    (context, "Most Recent Location: id: \n" + currentLocation.getLocationId() + ", " + currentLocation.toApiFormat()
//                                    + "\nMost Recent Snapshot: \n" + currentSnapshot.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                        Log.e(TAG, "CovidSnapshot Room listener invoked");
//                    }
//                    else if (covidSnapshotFromDb == null) {
//                        Log.e(TAG, "Observer returned null CovidSnapshot");
//                        // run API call, if location is saved
//                    }
//                }
//            });
//            roomCovidSnapshotObserved = true;
//        }
//    }

    /**
     * This sets an observer on the Room function that returns the most recent addition to the db
     * When the new Location comes through, ?
     */
//    private void setRoomLocationObserved() {
//        // Set Listener for Location
//        if (!roomLocationObserved && liveLocation != null) {
//            liveLocation.observe(this, new Observer<Location>() {
//                @Override
//                public void onChanged(@Nullable final Location locationFromDb) {
//                    // update cached version of location
//                    currentLocation = liveLocation.getValue() != null ? liveLocation.getValue() : currentLocation;
//                    if ((locationFromDb != null && !Location.alreadyInRoom(locationFromDb, savedLocations))
//                        || (currentLocation != null && currentLocation.hasFieldsSet())) {
//                        currentLocation = locationFromDb != null ? locationFromDb : currentLocation;
//                        savedLocations.add(currentLocation);
//                        currentSnapshot.setLocationId(currentLocation.getLocationId());
//                        Log.e(TAG, "Locally saved location set to :" + currentLocation.toApiFormat());
//                    } else {
//                        Log.d(TAG, "Location not saved locally: " + locationFromDb.toApiFormat());
//                        // no location is saved
//                        // pop up dialog
//                    }
//                }
//            });
//        }
//        roomLocationObserved = true;
//    }



//    public void saveSnapshotToRoom() {
//        if (currentSnapshot != null && currentSnapshot.hasFieldsSet()) {
//            // make sure to set LocationIdFK on Snapshot to current LocationIdPK
//            if (currentSnapshot.getLocationId() == null || currentSnapshot.getLocationId() == 0) {
//                if (currentLocation.getLocationId() == null) {
////                    saveLocationToRoom();
//                    currentLocation = liveLocation.getValue();
//                }
//                currentSnapshot.setLocationId(currentLocation != null ? currentLocation.getLocationId() : -1);
//
//            }
//            Calendar calendar = Calendar.getInstance();
//            currentSnapshot.setLastUpdated(calendar);
//            vm.insertCovidSnapshot(currentSnapshot);
//        } else {
//            Log.e(TAG, "Incomplete Snapshot: " + currentSnapshot.toString());
//        }
//        Log.d(TAG, "saveSnapshotToRoom invoked");
//    }

//    public void saveLocationToRoom() {
//        if (currentLocation != null && currentLocation.hasFieldsSet()) {
//            Calendar calendar = Calendar.getInstance();
//            currentLocation.setLastUpdated(calendar);
//            vm.insertLocation(currentLocation);
//        } else {
//            Log.e(TAG, "Incomplete Location:  " + currentLocation.toApiFormat());
//        }
//        Log.d(TAG, "saveLocationToRoom invoked");
//    }

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

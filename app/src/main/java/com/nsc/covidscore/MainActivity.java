package com.nsc.covidscore;

import android.app.FragmentManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.nsc.covidscore.api.RequestSingleton;
import com.nsc.covidscore.room.CovidSnapshot;
import com.nsc.covidscore.room.CovidSnapshotWithLocationViewModel;
import com.nsc.covidscore.room.Location;

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

    private LiveData<CovidSnapshot> liveCovidSnapshot;
    private CovidSnapshot lastSavedCovidSnapshot = new CovidSnapshot();

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

        // Access to Room Database
        vm = new ViewModelProvider(this).get(CovidSnapshotWithLocationViewModel.class);

        // This variable will hold latest copy of Covid Snapshot
        liveCovidSnapshot = vm.getLatestCovidSnapshot();

        // This sets an observer on that Snapshot, for when it is updated
        setRoomCovidSnapshotObserved();

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragContainer) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            if (!lastSavedCovidSnapshot.hasFieldsSet()) {
                Log.e(TAG, "no saved CovidSnapshot");
                openLocationSelectionFragment();
            } else {
                //openRiskDetailPageFragment();
                // TODO: fix this method to work
                Log.e(TAG, "saved CovidSnapshot exists");
                openLocationSelectionFragment();
            }
        }

        requestManager = RequestSingleton.getInstance(this.getApplicationContext());
        queue = requestManager.getRequestQueue();

        Log.d(TAG,"onCreate invoked");
    }

    public void openRiskDetailPageFragment() {
        // Create a new Risk Detail Fragment to be placed in the activity layout
        RiskDetailPageFragment riskDetailPageFragment = new RiskDetailPageFragment();
        Bundle bundle = new Bundle();

        // TODO: Save current CovidSnapshot and Location to this bundle

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        riskDetailPageFragment.setArguments(bundle);

        // Add the fragment to the "Fragment_container" FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragContainer, riskDetailPageFragment, "rdpf").commit();
    }

    public void openLocationSelectionFragment() {
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
        if (liveCovidSnapshot != null && !liveCovidSnapshot.hasActiveObservers()) {
            liveCovidSnapshot.observe(this, new Observer<CovidSnapshot>() {
                @Override
                public void onChanged(@Nullable final CovidSnapshot covidSnapshotFromDb) {
                    if (covidSnapshotFromDb != null) {
                        lastSavedCovidSnapshot = covidSnapshotFromDb;
                        Log.d(TAG, "Most recently saved Snapshot: " + covidSnapshotFromDb.toString());

                    } else {
                        Log.d(TAG, "Observer returned null CovidSnapshot");
                    }
                }
            });
        }
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
                Integer locationId = currentArray.getInt(0);
                // split county and state names
                String[] nameArray = currentArray.getString(1).split(",");
                String countyName = nameArray[0].trim();
                String stateName = nameArray[1].trim();
                String stateFips = currentArray.getString(2);
                String countyFips = currentArray.getString(3);
                Location countyInState = new Location(locationId, countyName, stateName, stateFips, countyFips);

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

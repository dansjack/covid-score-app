package com.nsc.covidscore;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

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
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity implements RiskDetailPageFragment.OnSelectLocationButtonListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private HashMap<String, List<Location>> mapOfLocationsByState = new HashMap<>();
    private HashMap<Integer, Location> mapOfLocationsById = new HashMap<>();

    private Location lastSavedLocation;
    private CovidSnapshot lastSavedCovidSnapshot = new CovidSnapshot();

    private CovidSnapshotWithLocationViewModel vm;
    private RequestQueue queue;
    private RequestSingleton requestManager;

    private Context context;

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof  RiskDetailPageFragment) {
            RiskDetailPageFragment riskDetailPageFragment = (RiskDetailPageFragment) fragment;
            riskDetailPageFragment.setOnSelectLocationButtonListener(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        // Access to Room Database
        vm = new ViewModelProvider(this).get(CovidSnapshotWithLocationViewModel.class);

        // This variable will hold latest copy of Covid Snapshot
        vm.getLatestCovidSnapshot().observe(this, covidSnapshotFromDb -> {
            if (covidSnapshotFromDb != null) {
                lastSavedCovidSnapshot = covidSnapshotFromDb;
                lastSavedLocation = mapOfLocationsById.get(covidSnapshotFromDb.getLocationId());
                Log.e(TAG, "Most recently saved Snapshot: " + covidSnapshotFromDb.toString());

            } else {
                Log.d(TAG, "Observer returned null CovidSnapshot");
            }
            loadFragments(savedInstanceState);
        });

        fillLocationsMap();

        requestManager = RequestSingleton.getInstance(this.getApplicationContext());
        queue = requestManager.getRequestQueue();

        Log.d(TAG,"onCreate invoked");
    }

    private void loadFragments(Bundle savedInstanceState) {
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
                Log.e(TAG, "saved CovidSnapshot exists");
                openRiskDetailPageFragment();
            }
        }

    }

    public void openRiskDetailPageFragment() {
        // Create a new Risk Detail Fragment to be placed in the activity layout
        RiskDetailPageFragment riskDetailPageFragment = new RiskDetailPageFragment();

        HashMap<Integer, Double> riskMap = RiskCalculation.getRiskCalculationsMap(
                lastSavedCovidSnapshot.getCountyActiveCount(),
                lastSavedCovidSnapshot.getCountyTotalPopulation(),
                Constants.GROUP_SIZES);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.CURRENT_LOCATION, lastSavedLocation.getCounty() + ", " + lastSavedLocation.getState());
        bundle.putString(Constants.ACTIVE_COUNTY, lastSavedCovidSnapshot.getCountyActiveCount().toString());
        bundle.putString(Constants.ACTIVE_STATE, lastSavedCovidSnapshot.getStateActiveCount().toString());
        bundle.putString(Constants.ACTIVE_COUNTRY, lastSavedCovidSnapshot.getCountryActiveCount().toString());
        bundle.putString(Constants.TOTAL_COUNTY, lastSavedCovidSnapshot.getCountyTotalPopulation().toString());
        bundle.putString(Constants.TOTAL_STATE, lastSavedCovidSnapshot.getStateTotalPopulation().toString());
        bundle.putString(Constants.TOTAL_COUNTRY, lastSavedCovidSnapshot.getCountryTotalPopulation().toString());
        bundle.putSerializable(Constants.RISK_MAP,riskMap);
        bundle.putSerializable(Constants.LOCATIONS_MAP_BY_STATE, mapOfLocationsByState);
        bundle.putSerializable(Constants.LOCATIONS_MAP_BY_ID, mapOfLocationsById);

        // TODO: Save current CovidSnapshot and Location to this bundle

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        riskDetailPageFragment.setArguments(bundle);

        // Add the fragment to the "Fragment_container" FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragContainer, riskDetailPageFragment, Constants.FRAGMENT_RDPF).commit();
    }

    public void openLocationSelectionFragment() {
        // Create a new Location Selection Fragment to be placed in the activity layout
        LocationManualSelectionFragment locationManualSelectionFragment = new LocationManualSelectionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.LOCATIONS_MAP_BY_STATE, mapOfLocationsByState);
        bundle.putSerializable(Constants.LOCATIONS_MAP_BY_ID, mapOfLocationsById);

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        locationManualSelectionFragment.setArguments(bundle);

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragContainer, locationManualSelectionFragment, Constants.FRAGMENT_LMSF)
                .addToBackStack(null).commit();
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
        LocationManualSelectionFragment tLmsf = (LocationManualSelectionFragment)
                getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_LMSF);
        if (tLmsf != null && tLmsf.isVisible()) {
            LocationManualSelectionFragment locationManualSelectionFragment = new LocationManualSelectionFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.LOCATIONS_MAP_BY_STATE, mapOfLocationsByState);
            bundle.putSerializable(Constants.LOCATIONS_MAP_BY_ID, mapOfLocationsById);

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            locationManualSelectionFragment.setArguments(bundle);

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragContainer, locationManualSelectionFragment, Constants.FRAGMENT_LMSF).commit();
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
            InputStream inputStream = assetManager.open(Constants.LOCATION_FILENAME);
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
                String[] nameArray = currentArray.getString(1).split(Constants.COMMA);
                String countyName = nameArray[0].trim();
                String stateName = nameArray[1].trim();
                String stateFips = currentArray.getString(2);
                String countyFips = currentArray.getString(3);
                Location countyInState = new Location(locationId, countyName, stateName, countyFips, stateFips);

                mapOfLocationsById.put(locationId, countyInState);
                if (mapOfLocationsByState.get(stateName) == null) {
//                    Log.i(TAG, "fillLocationsMap: " + stateName);
                    mapOfLocationsByState.put(stateName, new ArrayList<>());
                }
                mapOfLocationsByState.get(stateName).add(countyInState);
            }

        } catch (IOException | JSONException exception) {
            exception.printStackTrace();
        }
    }


    @Override
    public void onLocationButtonClicked() {
        openLocationSelectionFragment();
    }
}

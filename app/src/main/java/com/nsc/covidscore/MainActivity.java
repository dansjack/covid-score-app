package com.nsc.covidscore;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.RequestQueue;
import com.nsc.covidscore.api.RequestSingleton;
import com.nsc.covidscore.room.CovidSnapshot;
import com.nsc.covidscore.room.CovidSnapshotWithLocationViewModel;
import com.nsc.covidscore.room.Location;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class MainActivity extends FragmentActivity implements RiskDetailPageFragment.OnSelectLocationButtonListener, LocationManualSelectionFragment.OnSubmitButtonListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Location lastSavedLocation;
    private CovidSnapshot lastSavedCovidSnapshot = new CovidSnapshot();

    private CovidSnapshotWithLocationViewModel vm;
    private RequestQueue queue;
    private RequestSingleton requestManager;

    private Context context;

    private ConnectivityManager cm;

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof  RiskDetailPageFragment) {
            RiskDetailPageFragment rdpFragment = (RiskDetailPageFragment) fragment;
            rdpFragment.setOnSelectLocationButtonListener(this);
        }
        if (fragment instanceof LocationManualSelectionFragment) {
            LocationManualSelectionFragment lmsFragment = (LocationManualSelectionFragment) fragment;
            lmsFragment.setOnSubmitButtonListener(this);
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
                lastSavedLocation = vm.getMapOfLocationsById().get(covidSnapshotFromDb.getLocationId());
                Log.e(TAG, "Most recently saved Snapshot: " + covidSnapshotFromDb.toString());

            } else {
                Log.d(TAG, "Observer returned null CovidSnapshot");
            }
            loadFragments(savedInstanceState);
        });

        // Check Internet Connectivity
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                vm.setConnectionStatus(true);
            }
            @Override
            public void onLost(Network network) {
                vm.setConnectionStatus(false);
                Toast.makeText(context, "No Internet Connection Available", Toast.LENGTH_LONG);
            }
        });

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

            if (!lastSavedCovidSnapshot.hasFieldsSet()) { // No saved CovidSnapshot
                Log.e(TAG, "no saved CovidSnapshot");
                openLocationSelectionFragment();
            } else if (vm.getConnectionStatus() == true) { // CovidSnapshot saved, with Internet
                Log.e(TAG, "saved CovidSnapshot exists, update w/ internet");
            } else { // CovidSnapshot saved, no internet
                Log.e(TAG, "saved CovidSnapshot exists, no internet");
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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        bundle.putString(Constants.LAST_UPDATED, sdf.format(lastSavedCovidSnapshot.getLastUpdated().getTime()));

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        riskDetailPageFragment.setArguments(bundle);

        // Add the fragment to the "Fragment_container" FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragContainer, riskDetailPageFragment, Constants.FRAGMENT_RDPF).commit();
    }

    public void openNewRiskDetailPageFragment(MutableLiveData<CovidSnapshot> mcs, Location selectedLocation) {
        Log.i(TAG, "onViewCreated - btnNavRiskDetail - selectedLocation filled: " + selectedLocation.toString());

        LocationManualSelectionFragment lmsFragment = (LocationManualSelectionFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_LMSF);
        if (lmsFragment != null) { // User has manually selected location already
            lmsFragment.saveSnapshotToRoom(mcs.getValue(), selectedLocation);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        RiskDetailPageFragment riskDetailPageFragment = new RiskDetailPageFragment();
        CovidSnapshot snapshot = mcs.getValue();

        HashMap<Integer, Double> riskMap = RiskCalculation.getRiskCalculationsMap(
                snapshot.getCountyActiveCount(),
                snapshot.getCountyTotalPopulation(),
                Constants.GROUP_SIZES);
        Log.i(TAG, "onViewCreated: riskMap" + riskMap.toString());

        Bundle bundle = new Bundle();
        StringBuilder currentLocationSB = new StringBuilder(selectedLocation.getCounty())
                .append(Constants.COMMA_SPACE).append(selectedLocation.getState());
        bundle.putString(Constants.CURRENT_LOCATION, String.valueOf(currentLocationSB));
        bundle.putString(Constants.ACTIVE_COUNTY, snapshot.getCountyActiveCount().toString());
        bundle.putString(Constants.ACTIVE_STATE, snapshot.getStateActiveCount().toString());
        bundle.putString(Constants.ACTIVE_COUNTRY, snapshot.getCountryActiveCount().toString());
        bundle.putString(Constants.TOTAL_COUNTY, snapshot.getCountyTotalPopulation().toString());
        bundle.putString(Constants.TOTAL_STATE, snapshot.getStateTotalPopulation().toString());
        bundle.putString(Constants.TOTAL_COUNTRY, snapshot.getCountryTotalPopulation().toString());
        bundle.putSerializable(Constants.RISK_MAP,riskMap);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        bundle.putString(Constants.LAST_UPDATED, sdf.format(lastSavedCovidSnapshot.getLastUpdated().getTime()));
        riskDetailPageFragment.setArguments(bundle);
        transaction.replace(R.id.fragContainer, riskDetailPageFragment, Constants.FRAGMENT_RDPF);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
        mcs.setValue(new CovidSnapshot());
        //  selectedLocation = new Location();
    }

    public void openLocationSelectionFragment() {
        // Create a new Location Selection Fragment to be placed in the activity layout
        LocationManualSelectionFragment locationManualSelectionFragment = new LocationManualSelectionFragment();

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
//            bundle.putSerializable(Constants.LOCATIONS_MAP_BY_STATE, mapOfLocationsByState);

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

    @Override
    public void onLocationButtonClicked() {
        openLocationSelectionFragment();
    }

    @Override
    public void onSubmitButtonClicked(MutableLiveData<CovidSnapshot> mcs, Location selectedLocation) {
        openNewRiskDetailPageFragment(mcs, selectedLocation);
    }
}

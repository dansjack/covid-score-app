package com.nsc.covidscore;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends FragmentActivity implements RiskDetailPageFragment.OnSelectLocationButtonListener, LocationManualSelectionFragment.OnSubmitButtonListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Location lastSavedLocation;
    private CovidSnapshot lastSavedCovidSnapshot = new CovidSnapshot();
    private boolean firstOpen = true;
    public boolean isConnected = false;

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
            if (firstOpen) { // Don't do this every time Room is updated
                loadFragments(savedInstanceState);
                firstOpen = false;
            }
        });

        // Check Internet Connectivity
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                vm.setConnectionStatus(true);
                isConnected = true;
            }
            @Override
            public void onLost(Network network) {
                vm.setConnectionStatus(false);
                isConnected = false;
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
            } else if (vm.getConnectionStatus() && !hasBeenUpdatedThisHour()) { // CovidSnapshot saved, with Internet
                Log.e(TAG, "saved CovidSnapshot exists, update w/ internet");
                Location savedLocation = vm.getMapOfLocationsById().get(lastSavedCovidSnapshot.getLocationId());
                if (savedLocation != null) {
                    Log.i(TAG, "loadFragments: + " + savedLocation.toString());
                    openNewRiskDetailPageFragment(lastSavedCovidSnapshot, savedLocation);
                }
            } else { // CovidSnapshot saved, no internet
                Log.e(TAG, "saved CovidSnapshot exists, no internet or saved this hour");
                Toast.makeText(context, "No Internet Connection Available", Toast.LENGTH_LONG);
                locationSelectToRiskFragment();
            }
        }
    }

    public void openAboutFragment() {
        AboutFragment aboutFragment = new AboutFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragContainer, aboutFragment, Constants.FRAGMENT_ABOUT).commit();
    }

    public void locationSelectToRiskFragment() {
        // Used when navigating from LocationManualSelectFragment to RiskDetailPageFragment
        RiskDetailPageFragment riskDetailPageFragment = new RiskDetailPageFragment();

        Bundle bundle = makeRiskDetailPageBundle(lastSavedCovidSnapshot, lastSavedLocation);
        riskDetailPageFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragContainer, riskDetailPageFragment, Constants.FRAGMENT_RDPF).commit();
    }

    public void openNewRiskDetailPageFragment(CovidSnapshot cs, Location selectedLocation) {
        // Used when opening the app with an existing CovidSnapshot

        if (cs.hasFieldsSet() && cs.getLocationId() != null) {
            Log.i(TAG, "openNewRiskDetailPageFragment2: ++ Inserting CS" + cs.toString());
            vm.makeApiCalls(selectedLocation);
            Calendar calendar = Calendar.getInstance();
            cs.setLastUpdated(calendar);
            vm.insertCovidSnapshot(cs);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            RiskDetailPageFragment riskDetailPageFragment = new RiskDetailPageFragment();

            Bundle bundle = makeRiskDetailPageBundle(cs, selectedLocation);
            riskDetailPageFragment.setArguments(bundle);

            transaction.replace(R.id.fragContainer, riskDetailPageFragment, Constants.FRAGMENT_RDPF);
            transaction.addToBackStack(null);
            transaction.commit();
            vm.setMutableCovidSnapshot(cs);
        }
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
        Log.i(TAG, "onSubmitButtonClicked - cs: " + Objects.requireNonNull(mcs.getValue()).toString());
        openNewRiskDetailPageFragment(mcs.getValue(), selectedLocation);
    }

    private Bundle makeRiskDetailPageBundle(CovidSnapshot snapshot, Location location) {
        Bundle bundle = new Bundle();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HashMap<Integer, Double> countyRiskMap = RiskCalculation.getRiskCalculationsMap(
                snapshot.getCountyActiveCount(),
                snapshot.getCountyTotalPopulation(),
                Constants.GROUP_SIZES);
        HashMap<Integer, Double> stateRiskMap = RiskCalculation.getRiskCalculationsMap(
                snapshot.getStateActiveCount(),
                snapshot.getStateTotalPopulation(),
                Constants.GROUP_SIZES);
        HashMap<Integer, Double> countryRiskMap = RiskCalculation.getRiskCalculationsMap(
                snapshot.getCountryActiveCount(),
                snapshot.getCountryTotalPopulation(),
                Constants.GROUP_SIZES);
        StringBuilder locationSb = new StringBuilder(location.getCounty())
                .append(Constants.COMMA_SPACE).append(location.getState());

        bundle.putString(Constants.CURRENT_LOCATION, locationSb.toString());
        bundle.putString(Constants.ACTIVE_COUNTY, snapshot.getCountyActiveCount().toString());
        bundle.putString(Constants.ACTIVE_STATE, snapshot.getStateActiveCount().toString());
        bundle.putString(Constants.ACTIVE_COUNTRY, snapshot.getCountryActiveCount().toString());
        bundle.putString(Constants.TOTAL_COUNTY, snapshot.getCountyTotalPopulation().toString());
        bundle.putString(Constants.TOTAL_STATE, snapshot.getStateTotalPopulation().toString());
        bundle.putString(Constants.TOTAL_COUNTRY, snapshot.getCountryTotalPopulation().toString());
        bundle.putSerializable(Constants.COUNTY_RISK_MAP, countyRiskMap);
        bundle.putSerializable(Constants.STATE_RISK_MAP, stateRiskMap);
        bundle.putSerializable(Constants.COUNTRY_RISK_MAP, countryRiskMap);
        bundle.putString(Constants.LAST_UPDATED, sdf.format(snapshot.getLastUpdated().getTime()));

        return bundle;
    }

    private boolean hasBeenUpdatedThisHour() {
        Calendar lastSaved = lastSavedCovidSnapshot.getLastUpdated();
        Calendar lastSavedHour = Calendar.getInstance();
        lastSavedHour.clear();
        lastSavedHour.set(lastSaved.get(Calendar.YEAR), lastSaved.get(Calendar.MONTH), lastSaved.get(Calendar.DAY_OF_MONTH));
        lastSavedHour.set(Calendar.HOUR_OF_DAY, lastSaved.get(Calendar.HOUR_OF_DAY));
        Calendar now = Calendar.getInstance();
        Calendar nowHour = Calendar.getInstance();
        nowHour.clear();
        nowHour.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        nowHour.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
        return nowHour.equals(lastSavedHour);
    }
}

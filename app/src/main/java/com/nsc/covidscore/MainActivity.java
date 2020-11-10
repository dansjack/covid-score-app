package com.nsc.covidscore;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.RequestQueue;
import com.google.android.material.navigation.NavigationView;
import com.nsc.covidscore.api.RequestSingleton;
import com.nsc.covidscore.room.CovidSnapshot;
import com.nsc.covidscore.room.CovidSnapshotWithLocationViewModel;
import com.nsc.covidscore.room.Location;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity implements RiskDetailPageFragment.OnSelectLocationButtonListener, LocationManualSelectionFragment.OnSubmitButtonListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Location lastSavedLocation;
    private CovidSnapshot lastSavedCovidSnapshot = new CovidSnapshot();

    private CovidSnapshotWithLocationViewModel vm;
    private RequestQueue queue;
    private RequestSingleton requestManager;

    private Context context;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;

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

        // Toolbar to replace actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find drawer view
        mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
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

    public void openNewRiskDetailPageFragment(MutableLiveData<CovidSnapshot> mcs, Location selectedLocation) {
        Log.i(TAG, "onViewCreated - btnNavRiskDetail - selectedLocation filled: " + selectedLocation.toString());

        LocationManualSelectionFragment lmsFragment = (LocationManualSelectionFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_LMSF);
        if (lmsFragment != null) {
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

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        riskDetailPageFragment.setArguments(bundle);
        transaction.replace(R.id.fragContainer, riskDetailPageFragment, Constants.FRAGMENT_RDPF);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
        mcs.setValue(new CovidSnapshot());
        //  selectedLocation = new Location();
    }

    public void openNewRiskDetailPageFragment(MutableLiveData<CovidSnapshot> mcs, Location selectedLocation) {
        Log.i(TAG, "onViewCreated - btnNavRiskDetail - selectedLocation filled: " + selectedLocation.toString());

        LocationManualSelectionFragment lmsFragment = (LocationManualSelectionFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_LMSF);
        if (lmsFragment != null) {
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















//TODO: write method which takes getLatestLocations and prints them as drawerItems
//TODO: write method which takes a getLatestLocations drawerItem selection and
// spins up new RiskDetailFragment given location using vm.getLatestCovidSnapshotByLocation from repository
    public void updateDrawerItems() {
        LiveData<List<CovidSnapshot>> locationsList = vm.getLatestLocations();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationView.getMenu().findItem(R.id.nav_location_fragment_1).setTitle("savedlocation1");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }
//TODO: RiskDetail fragment should open with location state
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_location_fragment_1:
                fragmentClass = RiskDetailPageFragment.class;
                break;
            case R.id.nav_location_fragment_2:
                fragmentClass = RiskDetailPageFragment.class;
                break;
            case R.id.nav_location_fragment_3:
                fragmentClass = RiskDetailPageFragment.class;
                break;
            case R.id.nav_location_settings_fragment:
                fragmentClass = LocationSettingsPageFragment.class;
                break;
            default:
                fragmentClass = RiskDetailPageFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragContainer, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }
    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not
        // require it but won't render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.drawer_open,  R.string.drawer_close);
    }



















    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    // Synchronize state when screen is restored or rotated
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
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

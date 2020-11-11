package com.nsc.covidscore;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.RequestQueue;
import com.google.android.material.navigation.NavigationView;
import com.nsc.covidscore.api.RequestSingleton;
import com.nsc.covidscore.room.CovidSnapshot;
import com.nsc.covidscore.room.CovidSnapshotWithLocationViewModel;
import com.nsc.covidscore.room.Location;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RiskDetailPageFragment.OnSelectLocationButtonListener, LocationManualSelectionFragment.OnSubmitButtonListener {
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

    private MutableLiveData<List<CovidSnapshot>> lastSavedCovidLocationsListSnapshot = new MutableLiveData<List<CovidSnapshot>>();
    private HashMap<Integer, Location> mapOfLocationsById = new HashMap<Integer, Location>();
    private List<Location> locationIdsList;
    private Location locationDrawerItem;
    private int locationIdDrawerItem;

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

        // Access to Room Database/ Get the ViewModel.
        vm = new ViewModelProvider(this).get(CovidSnapshotWithLocationViewModel.class);

        //TODO: get map of Locations -- done
        mapOfLocationsById = vm.getMapOfLocationsById();

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

        //TODO: Set Observer; access location list via vm; store in lastSavedCovidLocationsListSnapshot --done
            vm.getLatestLocationsList().observe(this, covidLocationListSnapshotFromDb -> {
                if (covidLocationListSnapshotFromDb != null) {
                    lastSavedCovidLocationsListSnapshot =
                            (MutableLiveData<List<CovidSnapshot>>) covidLocationListSnapshotFromDb;
                    //TODO: get list of (up to) 3 last saved locationIds; taken from covidLocationListSnapshotFromDb

                    Log.e(TAG, "Most recently saved locations list snapshot: " +
                            lastSavedCovidLocationsListSnapshot.toString());

                } else {
                    Log.d(TAG, "Observer returned null CovidSnapshot location list");
                }
            }
        );



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





//TODO: write method that takes list of locationIds and locationList snapshot and produces a Location and a Snapshot









//TODO: write method which takes getLatestLocations and prints them as drawerItems
//TODO: write method which takes a getLatestLocations drawerItem selection and
// spins up new RiskDetailFragment given location using vm.getLatestCovidSnapshotByLocation from repository
//    public void updateDrawerItems() {
//        locationsList = vm.getLatestLocations();
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
//        navigationView.getMenu().findItem(R.id.nav_location_fragment_1).setTitle("savedlocation1");
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // The action bar home/up action should open or close the drawer.
        if (drawerToggle.onOptionsItemSelected(menuItem)) {
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
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

    //
//TODO: RiskDetail fragment should open with location state
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_location_fragment_1:
                //TODO: if(list of
                fragmentClass = LocationManualSelectionFragment.class;
                break;
            case R.id.nav_location_fragment_2:
                fragmentClass = LocationManualSelectionFragment.class;
                break;
            case R.id.nav_location_fragment_3:
                fragmentClass = LocationManualSelectionFragment.class;
                break;
            case R.id.nav_location_settings_fragment:
                fragmentClass = LocationSettingsPageFragment.class;
                break;
            default:
                fragmentClass = LocationManualSelectionFragment.class;
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

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private HashMap<String, List<Location>> mapOfLocationsByState = new HashMap<>();
    private HashMap<Integer, Location> mapOfLocationsById = new HashMap<>();

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
        vm.getLatestCovidSnapshot().observe(this, new Observer<CovidSnapshot>() {
            @Override
            public void onChanged(@Nullable final CovidSnapshot covidSnapshotFromDb) {
                if (covidSnapshotFromDb != null) {
                    lastSavedCovidSnapshot = covidSnapshotFromDb;
                    lastSavedLocation = mapOfLocationsById.get(covidSnapshotFromDb.getLocationId());
                    Log.e(TAG, "Most recently saved Snapshot: " + covidSnapshotFromDb.toString());

                } else {
                    Log.d(TAG, "Observer returned null CovidSnapshot");
                }
                loadFragments(savedInstanceState);
            }
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
        bundle.putString("currentLocation", lastSavedLocation.getCounty() + ", " + lastSavedLocation.getState());
        bundle.putString("activeCounty", lastSavedCovidSnapshot.getCountyActiveCount().toString());
        bundle.putString("activeState", lastSavedCovidSnapshot.getStateActiveCount().toString());
        bundle.putString("activeCountry", lastSavedCovidSnapshot.getCountryActiveCount().toString());
        bundle.putString("totalCounty", lastSavedCovidSnapshot.getCountyTotalPopulation().toString());
        bundle.putString("totalState", lastSavedCovidSnapshot.getStateTotalPopulation().toString());
        bundle.putString("totalCountry", lastSavedCovidSnapshot.getCountyTotalPopulation().toString());
        bundle.putSerializable("riskMap",riskMap);
        bundle.putSerializable("allLocationsMapByState", mapOfLocationsByState);
        bundle.putSerializable("allLocationsMapById", mapOfLocationsById);

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
        bundle.putSerializable("allLocationsMapByState", mapOfLocationsByState);
        bundle.putSerializable("allLocationsMapById", mapOfLocationsById);

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        locationManualSelectionFragment.setArguments(bundle);

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragContainer, locationManualSelectionFragment, "lmsf").commit();
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
        LocationManualSelectionFragment tLmsf = (LocationManualSelectionFragment) getSupportFragmentManager().findFragmentByTag("lmsf");
        if (tLmsf != null && tLmsf.isVisible()) {
            LocationManualSelectionFragment locationManualSelectionFragment = new LocationManualSelectionFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("allLocationsMap", mapOfLocationsByState);

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

                mapOfLocationsById.put(locationId, countyInState);
                if (mapOfLocationsByState.get(stateName) == null) {
                    Log.i(TAG, "fillLocationsMap: " + stateName);
                    mapOfLocationsByState.put(stateName, new ArrayList<>());
                    mapOfLocationsByState.get(stateName).add(countyInState);
                } else {
                    mapOfLocationsByState.get(stateName).add(countyInState);
                }
            }

        } catch (IOException | JSONException exception) {
            exception.printStackTrace();
        }
    }

}

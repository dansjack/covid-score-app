package com.nsc.covidscore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements
        RiskDetailPageFragment.OnSelectLocationButtonListener,
        LocationManualSelectionFragment.OnSubmitButtonListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Location lastSavedLocation;
    private CovidSnapshot lastSavedCovidSnapshot = new CovidSnapshot();
    private boolean firstOpen = true;
    public boolean isConnected = false;

    private CovidSnapshotWithLocationViewModel vm;
    private RequestQueue queue;
    private RequestSingleton requestManager;

    private Context context;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;

    private final List<Location> locationsNavList = new ArrayList<>();
    private final List<CovidSnapshot> covidSnapshotNavList = new ArrayList<>();

    private MenuItem menuItem;

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

        // This will hold latest copy of Covid Snapshot
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

        // This will hold latest copy of last 3 locationIds
        vm.getLatestLocationsLatestCovidSnapshots().observe(this, snapshotListFromDb -> {
            if (snapshotListFromDb != null && snapshotListFromDb.size() <= 3) {
                locationsNavList.clear();
                covidSnapshotNavList.clear();
                for (int i = 0; i < snapshotListFromDb.size(); i++) {
                    Location recentLocation = vm.getMapOfLocationsById().get(snapshotListFromDb.get(i).getLocationId());
                    locationsNavList.add(recentLocation);

                    covidSnapshotNavList.add(snapshotListFromDb.get(i));

                    nvDrawer.getMenu().getItem(i).setVisible(true);
                    nvDrawer.getMenu().getItem(i).setTitle(recentLocation.toApiFormat());
                }
                nvDrawer.getMenu().getItem(0).setChecked(true);
            }
        });

        // Check Internet Connectivity
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                vm.setConnectionStatus(true);
                isConnected = true;
            }
            @Override
            public void onLost(@NonNull Network network) {
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

    public void locationSelectToRiskFragment() {
        // Used when navigating from LocationManualSelectFragment to RiskDetailPageFragment
        RiskDetailPageFragment riskDetailPageFragment = new RiskDetailPageFragment();

        Bundle bundle = makeRiskDetailPageBundle(lastSavedCovidSnapshot, lastSavedLocation);
        riskDetailPageFragment.setArguments(bundle);

        // Select first drawer item
        nvDrawer.setCheckedItem(R.id.nav_location_fragment_1);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragContainer, riskDetailPageFragment, Constants.FRAGMENT_RDPF).commit();
    }

    public void openNewRiskDetailPageFragment(CovidSnapshot cs, Location selectedLocation) {
        // Used when opening the app with an existing CovidSnapshot
        if (cs.hasFieldsSet() && cs.getLocationId() != null) {
            Log.i(TAG, "openNewRiskDetailPageFragment2: ++ Inserting CS" + cs.toString());

            if (cs.getLastUpdated() == null) { // new Snapshot needs to be added to DB
                Calendar calendar = Calendar.getInstance();
                cs.setLastUpdated(calendar);
                vm.insertCovidSnapshot(cs);
            } else if (!hasBeenUpdatedThisHour(cs)) {
                vm.makeApiCalls(selectedLocation);
            }

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
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        // The action bar home/up action should open or close the drawer.
        if (drawerToggle.onOptionsItemSelected(menuItem)) {
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    @SuppressLint("NonConstantResourceId")
    public void selectDrawerItem(@NonNull MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment;
        Class fragmentClass = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_location_fragment_1:
                if (locationsNavList.size() >= 1 && covidSnapshotNavList.size() >= 1) {
                    openNewRiskDetailPageFragment(covidSnapshotNavList.get(0), locationsNavList.get(0));
                }
                break;
            case R.id.nav_location_fragment_2:
                if (locationsNavList.size() >= 2 && covidSnapshotNavList.size() >= 2) {
                    openNewRiskDetailPageFragment(covidSnapshotNavList.get(1), locationsNavList.get(1));
                }
                break;
            case R.id.nav_location_fragment_3:
                if (locationsNavList.size() >= 3 && covidSnapshotNavList.size() >= 3) {
                    openNewRiskDetailPageFragment(covidSnapshotNavList.get(2), locationsNavList.get(2));
                }
                break;
            case R.id.nav_about_fragment:
                fragmentClass = AboutFragment.class;
                break;
            default:
                fragmentClass = LocationManualSelectionFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();

            // Insert the fragment by replacing any existing fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragContainer, fragment).commit();
        } catch(NullPointerException e) {
            // already handled new fragment
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);

        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not
        // require it but won't render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.drawer_open,  R.string.drawer_close);
    }

    /**
     * onPostCreate called when activity start-up is complete after onStart()
     * NOTE 1: Make sure to override the method with only a single Bundle argument
     * Note 2: Make sure you implement the correct onPostCreate(Bundle savedInstanceState) method.
     * There are 2 signatures and only onPostCreate(Bundle state) shows the hamburger icon.
     * @param savedInstanceState state from current session
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    // Synchronize state when screen is restored or rotated
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                java.util.Locale.getDefault());
        HashMap<Integer, Double> riskMap = RiskCalculation.getRiskCalculationsMap(
                snapshot.getCountyActiveCount(),
                snapshot.getCountyTotalPopulation(),
                Constants.GROUP_SIZES);

        String locationSb = location.getCounty() +
                Constants.COMMA_SPACE + location.getState();
        bundle.putString(Constants.CURRENT_LOCATION, locationSb);
        bundle.putString(Constants.ACTIVE_COUNTY, snapshot.getCountyActiveCount().toString());
        bundle.putString(Constants.ACTIVE_STATE, snapshot.getStateActiveCount().toString());
        bundle.putString(Constants.ACTIVE_COUNTRY, snapshot.getCountryActiveCount().toString());
        bundle.putString(Constants.TOTAL_COUNTY, snapshot.getCountyTotalPopulation().toString());
        bundle.putString(Constants.TOTAL_STATE, snapshot.getStateTotalPopulation().toString());
        bundle.putString(Constants.TOTAL_COUNTRY, snapshot.getCountryTotalPopulation().toString());
        bundle.putSerializable(Constants.RISK_MAP, riskMap);
        bundle.putString(Constants.LAST_UPDATED, sdf.format(snapshot.getLastUpdated().getTime()));

        return bundle;
    }

    /**
     * Compare most recently saved CovidSnapshot to current time - if the snapshot is more than an hour old, and
     * the phone has connectivity, we change behavior
     * @return true if the CovidSnapshot is recent within the hour, false otherwise
     */
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

    private boolean hasBeenUpdatedThisHour(CovidSnapshot cs) {
        Calendar lastSaved = cs.getLastUpdated();
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

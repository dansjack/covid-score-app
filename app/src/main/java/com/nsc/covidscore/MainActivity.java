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
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private HashMap<String, List<Location>> mapOfLocations = new HashMap<>();

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

        Log.d(TAG,"onCreate invoked");
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

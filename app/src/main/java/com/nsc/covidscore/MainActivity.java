package com.nsc.covidscore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.nsc.covidscore.api.RequestSingleton;
import com.nsc.covidscore.api.Requests;
import com.nsc.covidscore.api.VolleyJsonCallback;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Location currentLocation;
    private CovidSnapshot currentSnapshot;

    private CovidSnapshotWithLocationViewModel vm;
    private RequestQueue queue;
    private RequestSingleton requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestManager = RequestSingleton.getInstance(this.getApplicationContext());
        queue = requestManager.getRequestQueue();
        makeApiCalls();
        vm = new ViewModelProvider(this).get(CovidSnapshotWithLocationViewModel.class);

        if (vm.getCurrentCovidSnapshot() != null) {
            vm.getCurrentCovidSnapshot().observe(this, new Observer<CovidSnapshot>() {
                @Override
                public void onChanged(@Nullable final CovidSnapshot covidSnapshotFromDb) {
                    // update cached version of snapshot
                    currentSnapshot = covidSnapshotFromDb;
                    if (currentSnapshot == null) {
                        // run API call?
                    }
                }
            });
        }

        if (vm.getCurrentLocation() != null) {
            vm.getCurrentLocation().observe(this, new Observer<Location>() {
                @Override
                public void onChanged(@Nullable final Location locationFromDb) {
                    // update cached version of location
                    currentLocation = locationFromDb;
                    if (currentLocation == null) {
                        // pop up dialog
                    }
                }
            });
        }

        // update textviews, etc with currentSnapshot & currentLocation



        Log.d(TAG,"onCreate invoked");
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            requestManager.getRequestQueue().cancelAll(TAG);
        }
    }

    private void makeApiCalls() {
        Requests.getCounty(this, "king,washington", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {
                Log.d(TAG, "getJsonData: county " + response);
            }

            @Override
            public void getJsonException(Exception exception) {
            }
        });
        Requests.getCountyHistorical(this, "whatcom,washington", "30", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {
                Log.d(TAG, "getJsonData: countyHistorical " + response);
            }

            @Override
            public void getJsonException(Exception exception) {
            }
        });
        Requests.getUSHistorical(this, "1", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {
                Log.d(TAG, "getJsonData: country " + response);
            }

            @Override
            public void getJsonException(Exception exception) {

            }
        });
    }
}

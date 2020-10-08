package com.nsc.covidscore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Location currentLocation;
    private CovidSnapshot currentSnapshot;

    private CovidSnapshotWithLocationViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vm = new ViewModelProvider(this).get(CovidSnapshotWithLocationViewModel.class);
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

        // update textviews, etc with currentSnapshot & currentLocation



        Log.d(TAG,"onCreate invoked");
    }
}
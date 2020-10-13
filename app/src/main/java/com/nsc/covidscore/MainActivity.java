package com.nsc.covidscore;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate invoked");
    }

    public void onLocationNavBtnClick(View view) {
        setContentView(R.layout.fragment_location_selection);
    }


    // button on LocationSelectionFragment displays RiskDetailFragment onClick
    public void onLocationSubmitClick(View view) {
        setContentView(R.layout.fragment_risk_detail);
    }


}
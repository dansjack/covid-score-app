package com.nsc.covidscore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.List;

/**
 * A full-screen fragment.
 */
public class RiskDetailPageFragment extends Fragment {
    private static final String TAG = RiskDetailPageFragment.class.getSimpleName();
    private String currentLocation;
    private String activeCounty;
    private String activeState;
    private String activeCountry;
    private String totalCounty;
    private String totalState;
    private String totalCountry;

    private TextView currentLocationV;
    private TextView activeCountyV;
    private TextView activeStateV;
    private TextView activeCountryV;
    private TextView totalCountyV;
    private TextView totalStateV;
    private TextView totalCountryV;

    public RiskDetailPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: invoked");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_risk_detail, container, false);
        super.onCreate(savedInstanceState);


        Log.d(TAG, "onCreateView invoked");
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        currentLocationV = v.findViewById(R.id.currentLocation);
        activeCountyV = v.findViewById(R.id.activeCounty);
        activeStateV = v.findViewById(R.id.activeState);
        activeCountryV = v.findViewById(R.id.activeUS);
        totalCountyV = v.findViewById(R.id.totalCounty);
        totalStateV = v.findViewById(R.id.totalState);
        totalCountryV = v.findViewById(R.id.totalCountry);

        Bundle bundle = getArguments();

        if (bundle != null) {
            currentLocation = bundle.getString("currentLocation");
            activeCounty = bundle.getString("activeCounty");
            activeState = bundle.getString("activeState");
            activeCountry = bundle.getString("activeCountry");
            totalCounty = bundle.getString("totalCounty");
            totalState = bundle.getString("totalState");
            totalCountry = bundle.getString("totalCountry");

            currentLocationV.setText(currentLocation);
            activeCountyV.setText(activeCounty);
            activeStateV.setText(activeState);
            activeCountryV.setText(activeCountry);
            totalCountyV.setText(totalCounty);
            totalStateV.setText(totalState);
            totalCountryV.setText(totalCountry);

            Log.i(TAG, "onCreateView: Bundle received from LocationManualSelectionFragment");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, String.valueOf(outState));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
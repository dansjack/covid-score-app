package com.nsc.covidscore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.nsc.covidscore.room.Location;

import java.util.HashMap;
import java.util.List;

/**
 * A full-screen fragment.
 */
public class RiskDetailPageFragment extends Fragment {
    private static final String TAG = RiskDetailPageFragment.class.getSimpleName();
    private HashMap<String, List<Location>> mapOfLocationsByState = new HashMap<>();
    private HashMap<Integer, List<Location>> mapOfLocationsById = new HashMap<>();
    private String currentLocation;
    private String activeCounty;
    private String activeState;
    private String activeCountry;
    private String totalCounty;
    private String totalState;
    private String totalCountry;
    private HashMap<Integer, Double> riskMap;

    private TextView currentLocationV;
    private TextView activeCountyV;
    private TextView activeStateV;
    private TextView activeCountryV;
    private TextView totalCountyV;
    private TextView totalStateV;
    private TextView totalCountryV;

    private TextView labelRiskGroup1;
    private TextView labelRiskGroup2;
    private TextView labelRiskGroup3;
    private TextView labelRiskGroup4;
    private TextView labelRiskGroup5;
    private TextView riskGroup1;
    private TextView riskGroup2;
    private TextView riskGroup3;
    private TextView riskGroup4;
    private TextView riskGroup5;

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
        Bundle bundle = getArguments();

        if (bundle != null) {
            // noinspection unchecked
            mapOfLocationsByState = (HashMap<String, List<Location>>) bundle.getSerializable("allLocationsMapByState");
            mapOfLocationsById = (HashMap<Integer, List<Location>>) bundle.getSerializable("allLocationsMapById");
            Log.i(TAG, "onCreateView: Bundle received from MainActivity");
        }

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

        labelRiskGroup1 = v.findViewById(R.id.labelFirstGroup);
        labelRiskGroup2 = v.findViewById(R.id.labelSecondGroup);
        labelRiskGroup3 = v.findViewById(R.id.labelThirdGroup);
        labelRiskGroup4 = v.findViewById(R.id.labelFourthGroup);
        labelRiskGroup5 = v.findViewById(R.id.labelFifthGroup);

        riskGroup1 = v.findViewById(R.id.firstGroup);
        riskGroup2 = v.findViewById(R.id.secondGroup);
        riskGroup3 = v.findViewById(R.id.thirdGroup);
        riskGroup4 = v.findViewById(R.id.fourthGroup);
        riskGroup5 = v.findViewById(R.id.fifthGroup);

        Button btnSelectNewLocation = v.findViewById(R.id.select_location_btn);
        btnSelectNewLocation.setOnClickListener(v1 -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            LocationManualSelectionFragment locationManualSelectionFragment = new LocationManualSelectionFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("allLocationsMapByState", mapOfLocationsByState);
            bundle.putSerializable("allLocationsMapById", mapOfLocationsById);

            locationManualSelectionFragment.setArguments(bundle);

            transaction.replace(R.id.fragContainer, locationManualSelectionFragment, "lmsf");
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
                });

        Bundle bundle = getArguments();

        if (bundle != null) {
            currentLocation = bundle.getString("currentLocation");

            activeCounty = bundle.getString("activeCounty");
            activeState = bundle.getString("activeState");
            activeCountry = bundle.getString("activeCountry");

            totalCounty = bundle.getString("totalCounty");
            totalState = bundle.getString("totalState");
            totalCountry = bundle.getString("totalCountry");

            riskMap = (HashMap<Integer, Double>) bundle.getSerializable("riskMap");

            currentLocationV.setText(currentLocation);

            activeCountyV.setText(activeCounty);
            activeStateV.setText(activeState);
            activeCountryV.setText(activeCountry);

            totalCountyV.setText(totalCounty);
            totalStateV.setText(totalState);
            totalCountryV.setText(totalCountry);

            labelRiskGroup1.setText("- " + Constants.GROUP_SIZES[0]);
            labelRiskGroup2.setText("- " + Constants.GROUP_SIZES[1]);
            labelRiskGroup3.setText("- " + Constants.GROUP_SIZES[2]);
            labelRiskGroup4.setText("- " + Constants.GROUP_SIZES[3]);
            labelRiskGroup5.setText("- " + Constants.GROUP_SIZES[4]);

            riskGroup1.setText(riskMap.get(Constants.GROUP_SIZES[0]).toString() + "%");
            riskGroup2.setText(riskMap.get(Constants.GROUP_SIZES[1]).toString() + "%");
            riskGroup3.setText(riskMap.get(Constants.GROUP_SIZES[2]).toString() + "%");
            riskGroup4.setText(riskMap.get(Constants.GROUP_SIZES[3]).toString() + "%");
            riskGroup5.setText(riskMap.get(Constants.GROUP_SIZES[4]).toString() + "%");

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
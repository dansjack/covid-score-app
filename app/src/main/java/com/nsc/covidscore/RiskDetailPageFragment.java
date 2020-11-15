package com.nsc.covidscore;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Objects;


public class RiskDetailPageFragment extends Fragment {
    private static final String TAG = RiskDetailPageFragment.class.getSimpleName();
    private String currentLocation;
    private String activeCounty;
    private String activeState;
    private String activeCountry;
    private String totalCounty;
    private String totalState;
    private String totalCountry;
    private HashMap<Integer, Double> riskMap;
    private String lastUpdated;

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

    private TextView lastUpdatedV;

    private String[] groupSizesArray;
    Resources res;

    OnSelectLocationButtonListener callback;

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

        lastUpdatedV = v.findViewById(R.id.lastUpdatedTextView);

        res = Objects.requireNonNull(getActivity()).getResources();
        groupSizesArray = res.getStringArray(R.array.group_sizes);


        ImageButton btnSelectNewLocation = v.findViewById(R.id.select_location_btn);
        btnSelectNewLocation.setOnClickListener(v1 -> callback.onLocationButtonClicked());

        Bundle bundle = getArguments();

        if (bundle != null) {
            currentLocation = bundle.getString(Constants.CURRENT_LOCATION);

            activeCounty = bundle.getString(Constants.ACTIVE_COUNTY);
            activeState = bundle.getString(Constants.ACTIVE_STATE);
            activeCountry = bundle.getString(Constants.ACTIVE_COUNTRY);

            totalCounty = bundle.getString(Constants.TOTAL_COUNTY);
            totalState = bundle.getString(Constants.TOTAL_STATE);
            totalCountry = bundle.getString(Constants.TOTAL_COUNTRY);

            riskMap = (HashMap<Integer, Double>) bundle.getSerializable(Constants.RISK_MAP);

            StringBuilder lastUpdatedSB = new StringBuilder(Constants.UPDATED)
                    .append(bundle.getString(Constants.LAST_UPDATED));
            lastUpdated = lastUpdatedSB.toString();

            currentLocationV.setText(currentLocation);

            activeCountyV.setText(activeCounty);
            activeStateV.setText(activeState);
            activeCountryV.setText(activeCountry);

            totalCountyV.setText(totalCounty);
            totalStateV.setText(totalState);
            totalCountryV.setText(totalCountry);

            labelRiskGroup1.setText(String.format(res.getString(R.string.group), groupSizesArray[0]));
            labelRiskGroup2.setText(String.format(res.getString(R.string.group), groupSizesArray[1]));
            labelRiskGroup3.setText(String.format(res.getString(R.string.group), groupSizesArray[2]));
            labelRiskGroup4.setText(String.format(res.getString(R.string.group), groupSizesArray[3]));
            labelRiskGroup5.setText(String.format(res.getString(R.string.group), groupSizesArray[4]));

            riskGroup1.setText(String.format(res.getString(R.string.risk), riskMap.get(Constants.GROUP_SIZES[0])));
            riskGroup2.setText(String.format(res.getString(R.string.risk), riskMap.get(Constants.GROUP_SIZES[1])));
            riskGroup3.setText(String.format(res.getString(R.string.risk), riskMap.get(Constants.GROUP_SIZES[2])));
            riskGroup4.setText(String.format(res.getString(R.string.risk), riskMap.get(Constants.GROUP_SIZES[3])));
            riskGroup5.setText(String.format(res.getString(R.string.risk), riskMap.get(Constants.GROUP_SIZES[4])));

            lastUpdatedV.setText(lastUpdated);

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
//        Log.d(TAG, String.valueOf(outState));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setOnSelectLocationButtonListener(OnSelectLocationButtonListener callback) {
        this.callback = callback;
    }

    public interface OnSelectLocationButtonListener {
        public void onLocationButtonClicked();
    }
}
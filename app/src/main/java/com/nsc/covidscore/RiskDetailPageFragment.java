package com.nsc.covidscore;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import androidx.core.content.ContextCompat;
import android.graphics.Color;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class RiskDetailPageFragment extends Fragment {
    private static final String TAG = RiskDetailPageFragment.class.getSimpleName();
    private String currentLocation;
    private String activeCounty;
    private String activeState;
    private String activeCountry;
    private String totalCounty;
    private String totalState;
    private String totalCountry;
    private HashMap<Integer, Double> countyRiskMap;
    private HashMap<Integer, Double> stateRiskMap;
    private HashMap<Integer, Double> countryRiskMap;
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
    private LineChart riskTrendChart;

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

        currentLocationV = v.findViewById(R.id.labelCurrentLocation);

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

            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(currentLocation);

            activeCounty = bundle.getString(Constants.ACTIVE_COUNTY);
            activeState = bundle.getString(Constants.ACTIVE_STATE);
            activeCountry = bundle.getString(Constants.ACTIVE_COUNTRY);

            totalCounty = bundle.getString(Constants.TOTAL_COUNTY);
            totalState = bundle.getString(Constants.TOTAL_STATE);
            totalCountry = bundle.getString(Constants.TOTAL_COUNTRY);

            countyRiskMap = (HashMap<Integer, Double>) bundle.getSerializable(Constants.COUNTY_RISK_MAP);
            stateRiskMap = (HashMap<Integer, Double>) bundle.getSerializable(Constants.STATE_RISK_MAP);
            countryRiskMap = (HashMap<Integer, Double>) bundle.getSerializable(Constants.COUNTRY_RISK_MAP);

            StringBuilder lastUpdatedSB = new StringBuilder(Constants.UPDATED)
                    .append(bundle.getString(Constants.LAST_UPDATED));
            lastUpdated = lastUpdatedSB.toString();
            String locationString = getResources().getString(R.string.current_location) + currentLocation;
            SpannableString locationStringSpannable = new SpannableString(locationString);
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            locationStringSpannable.setSpan(boldSpan, 0, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            currentLocationV.setText(locationStringSpannable);

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

            riskGroup1.setText(String.format(res.getString(R.string.risk), countyRiskMap.get(Constants.GROUP_SIZES[0])));
            riskGroup2.setText(String.format(res.getString(R.string.risk), countyRiskMap.get(Constants.GROUP_SIZES[1])));
            riskGroup3.setText(String.format(res.getString(R.string.risk), countyRiskMap.get(Constants.GROUP_SIZES[2])));
            riskGroup4.setText(String.format(res.getString(R.string.risk), countyRiskMap.get(Constants.GROUP_SIZES[3])));
            riskGroup5.setText(String.format(res.getString(R.string.risk), countyRiskMap.get(Constants.GROUP_SIZES[4])));

            lastUpdatedV.setText(lastUpdated);

            Log.i(TAG, "onViewCreated: Bundle received from LocationManualSelectionFragment");
        }

        riskTrendChart = v.findViewById(R.id.lineGraph);
        setRiskChart();

    }

    /**
     * Set all data into lines for group size vs. risk relationship chart
     */
    private void setRiskChart() {
        LineDataSet countyRiskDataSet = new LineDataSet(getEntryList(countyRiskMap),"County");
        LineDataSet stateRiskDataSet = new LineDataSet(getEntryList(stateRiskMap), "State");
        LineDataSet countryRiskDataSet = new LineDataSet(getEntryList(countryRiskMap), "Country");

        countyRiskDataSet.setCircleColor(R.color.black_overlay);
        countyRiskDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        countyRiskDataSet.setLineWidth(5);
        countyRiskDataSet.setColor(Color.rgb(93, 211, 158));
        stateRiskDataSet.setCircleColor(R.color.black_overlay);
        stateRiskDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        stateRiskDataSet.setLineWidth(5);
        stateRiskDataSet.setColor(Color.rgb(52, 138, 167));
        countryRiskDataSet.setCircleColor(R.color.black_overlay);
        countryRiskDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        countryRiskDataSet.setLineWidth(5);
        countryRiskDataSet.setColor(Color.rgb(188, 231, 132));

        ArrayList<ILineDataSet> riskLineSet = new ArrayList<>();
        riskLineSet.add(countyRiskDataSet);
        riskLineSet.add(stateRiskDataSet);
        riskLineSet.add(countryRiskDataSet);

        Description description = new Description();
        description.setText(currentLocation);

        setAxes();
        riskTrendChart.setData(new LineData(riskLineSet));
        riskTrendChart.getDescription().setEnabled(true);
        riskTrendChart.setPinchZoom(true);
        riskTrendChart.setDoubleTapToZoomEnabled(false);
        riskTrendChart.setDescription(description);
        riskTrendChart.invalidate();
    }

    /**
     * Creates a List of Entries to be plotted on the Chart
     * @param riskMap   map of (group size, risk %) for a location
     * @return          List of Entries for Chart
     */
    private static ArrayList<Entry> getEntryList(HashMap<Integer, Double> riskMap) {
        ArrayList<Entry> riskVals = new ArrayList<>();
        riskVals.add(new Entry(0f, 0f));
        for (int i = 0; i < riskMap.size(); i++) {
            riskVals.add(new Entry((float) Constants.GROUP_SIZES[i], riskMap.get(Constants.GROUP_SIZES[i]).floatValue()));
        }
        return riskVals;
    };

    /**
     * sets basic data for Chart
     */
    private void setAxes() {
        XAxis xAxis = riskTrendChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(Constants.GROUP_SIZES[Constants.GROUP_SIZES.length - 1] + 10);
        xAxis.setTextSize(18);
        YAxis yAxis = riskTrendChart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(100);
        yAxis.setTextSize(18);
        YAxis rightAxis = riskTrendChart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setOnSelectLocationButtonListener(OnSelectLocationButtonListener callback) {
        this.callback = callback;
    }

    public interface OnSelectLocationButtonListener {
        void onLocationButtonClicked();
    }
}
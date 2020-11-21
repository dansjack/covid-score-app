package com.nsc.covidscore;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CompareFragment extends Fragment {
    private static final String TAG = CompareFragment.class.getSimpleName();

    private List<HashMap<Integer, Double>> countyRiskMaps;
    private List<String> locationStrings;
    private List<TextView> locationsTextViews = new ArrayList<>();

    private LineChart riskComparisonChart;
    Resources res;

    public CompareFragment() {
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

        View v = inflater.inflate(R.layout.fragment_compare, container, false);
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreateView invoked");
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        res = Objects.requireNonNull(getActivity()).getResources();
        riskComparisonChart = v.findViewById(R.id.comparisonGraph);

        Bundle bundle = getArguments();

        if (bundle != null) {
            if (bundle.containsKey(Constants.COMPARE_MAP_LIST) && bundle.containsKey(Constants.LOCATION_LIST)) {
                countyRiskMaps = (List<HashMap<Integer, Double>>) bundle.get(Constants.COMPARE_MAP_LIST);
                locationStrings = (List<String>) bundle.get(Constants.LOCATION_LIST);

                setTextViewsFromBundle(countyRiskMaps, locationStrings, v);
                setRiskChart();
            }
        }
    }

    private void setTextViewsFromBundle(List<HashMap<Integer, Double>> countyRiskMaps, List<String> locationStrings, View v) {
        TextView comparing = v.findViewById(R.id.comparing_tv);
        if (countyRiskMaps != null && locationStrings != null && countyRiskMaps.size() == locationStrings.size()) {
            int count = countyRiskMaps.size();
            if (count == 0) {
                comparing.setText(res.getString(R.string.nothing_to_compare));
                return;
            }

            TextView location1 = v.findViewById(R.id.compare_location_1);
            location1.setVisibility(View.VISIBLE);
            location1.setText(locationStrings.get(0));
            locationsTextViews.add(location1);

            if (count == 1) {
                comparing.setText(res.getString(R.string.need_more_locations));
                return;
            }

            TextView location2 = v.findViewById(R.id.compare_location_2);
            location2.setVisibility(View.VISIBLE);
            location2.setText(locationStrings.get(1));
            locationsTextViews.add(location2);

            if (count == 3) {
                TextView location3 = v.findViewById(R.id.compare_location_3);
                location3.setVisibility(View.VISIBLE);
                location3.setText(locationStrings.get(2));
                locationsTextViews.add(location3);
            }
        }
    }

    private void setRiskChart() {
        int lineCount = locationStrings.size();

        ArrayList<ILineDataSet> riskLineSet = new ArrayList<>();

        if (lineCount >= 2) {
            LineDataSet countyRiskDataSet1 =
                    new LineDataSet(getEntryList(countyRiskMaps.get(0)),locationStrings.get(0));
            countyRiskDataSet1.setCircleColor(R.color.black_overlay);
            countyRiskDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
            countyRiskDataSet1.setLineWidth(5);
            countyRiskDataSet1.setColor(Color.rgb(93, 211, 158));
            riskLineSet.add(countyRiskDataSet1);

            LineDataSet countyRiskDataSet2 =
                    new LineDataSet(getEntryList(countyRiskMaps.get(1)),locationStrings.get(1));
            countyRiskDataSet2.setCircleColor(R.color.black_overlay);
            countyRiskDataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);
            countyRiskDataSet2.setLineWidth(5);
            countyRiskDataSet2.setColor(Color.rgb(52, 138, 167));
            riskLineSet.add(countyRiskDataSet2);
        }
        if (lineCount == 3) {
            LineDataSet countyRiskDataSet3 =
                    new LineDataSet(getEntryList(countyRiskMaps.get(0)),locationStrings.get(2));
            countyRiskDataSet3.setCircleColor(R.color.black_overlay);
            countyRiskDataSet3.setAxisDependency(YAxis.AxisDependency.LEFT);
            countyRiskDataSet3.setLineWidth(5);
            countyRiskDataSet3.setColor(Color.rgb(188, 231, 132));
            riskLineSet.add(countyRiskDataSet3);
        }

        Description description = new Description();
        description.setText(res.getString(R.string.comparison_description));

        setAxes();
        riskComparisonChart.setData(new LineData(riskLineSet));
        riskComparisonChart.getDescription().setEnabled(true);
        riskComparisonChart.setPinchZoom(true);
        riskComparisonChart.setDoubleTapToZoomEnabled(false);
        riskComparisonChart.setDescription(description);
        riskComparisonChart.invalidate();
    }

    private List<Entry> getEntryList(HashMap<Integer, Double> riskMap) {
        ArrayList<Entry> riskVals = new ArrayList<>();
        riskVals.add(new Entry(0f, 0f));
        for (int i = 0; i < Constants.GROUP_SIZES.length; i++) {
            riskVals.add(new Entry((float) Constants.GROUP_SIZES[i], riskMap.get(Constants.GROUP_SIZES[i]).floatValue()));
        }
        return riskVals;
    }

    private void setAxes() {
        XAxis xAxis = riskComparisonChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(Constants.GROUP_SIZES[Constants.GROUP_SIZES.length - 1] + 10);
        xAxis.setTextSize(18);
        YAxis yAxis = riskComparisonChart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(100);
        yAxis.setTextSize(18);
        YAxis rightAxis = riskComparisonChart.getAxisRight();
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


}

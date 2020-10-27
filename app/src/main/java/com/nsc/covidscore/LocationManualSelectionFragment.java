package com.nsc.covidscore;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationManualSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationManualSelectionFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = LocationManualSelectionFragment.class.getSimpleName();

    private FragmentActivity listener;

    public LocationManualSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment LocationManualSelectionFragment.
     */
    public static LocationManualSelectionFragment newInstance() {
        LocationManualSelectionFragment fragment = new LocationManualSelectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        Log.d(TAG, "newInstance: invoked");
        return fragment;
    }

    // onAttach method fires 1st, before creation of fragment or any views
    // It is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }
        Log.d(TAG, "onAttach invoked");
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate invoked");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_selection, container, false);

        // State spinner
        Spinner state_spinner = v.findViewById(R.id.state_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> state_adapter = ArrayAdapter.createFromResource(listener,
                R.array.states_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        state_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        state_spinner.setAdapter(state_adapter);
        state_spinner.setOnItemSelectedListener(this);

        // County spinner
        Spinner county_spinner = v.findViewById(R.id.county_spinner);
        ArrayAdapter<CharSequence> county_adapter = ArrayAdapter.createFromResource(listener,
                R.array.counties_array, android.R.layout.simple_spinner_item);
        county_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        county_spinner.setAdapter(county_adapter);
        county_spinner.setOnItemSelectedListener(this);

        Log.d(TAG, "onCreateView invoked");

        return v;
    }

//     This event is triggered soon after onCreateView().
//     onViewCreated() is only called if the view returned from onCreateView() is non-null.
//     Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        Button btnNavRiskDetail = v.findViewById(R.id.submit_btn);
        btnNavRiskDetail.setOnClickListener(v1 -> (
                (MainActivity) Objects.requireNonNull(getActivity())).setViewPager(1));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated invoked");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.state_spinner)
        {
            String stateSelected = (String) parent.getItemAtPosition(position);
            //TODO: remove toast
            Toast.makeText
                    (listener.getApplicationContext(), "Selected : " + stateSelected, Toast.LENGTH_SHORT).show();

        }
        else if (parent.getId() == R.id.county_spinner)
        {
            String countySelected = (String) parent.getItemAtPosition(position);
            //TODO: remove toast
            Toast.makeText
                    (listener.getApplicationContext(), "Selected : " + countySelected, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Required method
    }

}
package com.nsc.covidscore;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.nsc.covidscore.room.CovidSnapshot;
import com.nsc.covidscore.room.CovidSnapshotWithLocationViewModel;
import com.nsc.covidscore.room.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class LocationManualSelectionFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = LocationManualSelectionFragment.class.getSimpleName();
    private Location selectedLocation = new Location();
    private MutableLiveData<CovidSnapshot> mutableCovidSnapshot;
    private boolean justForApiCalls = false;

    private CovidSnapshotWithLocationViewModel vm;
    private TextView loadingTextView;

    private List<Location> countyLocations = new ArrayList<>();
    private Spinner state_spinner;
    private Spinner county_spinner;

    LocationManualSelectionFragment.OnSubmitButtonListener callback;

    public LocationManualSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * onAttach fires 1st, before creation of fragment or any views
     * It is called when the Fragment instance is associated with an Activity.
     * This does not mean the Activity is fully initialized.
     * @param context from activity; attach to fragment
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach invoked");
    }

    /**
     * onCreate fires 2nd, before views are created for the fragment
     * The onCreate method is called when the Fragment instance is being created, or re-created.
     * Use onCreate for any standard setup that does not require the activity to be fully created
     * @param savedInstanceState state from current session
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Access to Room Database
        vm = new ViewModelProvider(this).get(CovidSnapshotWithLocationViewModel.class);
        mutableCovidSnapshot = vm.getMutableCovidSnapshot();
        Log.d(TAG, "onCreate invoked");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_selection, container, false);
        setInitialSpinners(v);

        Log.d(TAG, "onCreateView invoked");
        return v;
    }

    /**
     *  This event is triggered soon after onCreateView(). onViewCreated() is only called
     *  if the view returned from onCreateView() is non-null. Any view setup should occur here.
     *  E.g., view lookups and attaching view listeners.
     */

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");

        MainActivity main = (MainActivity) getActivity();

        loadingTextView = v.findViewById(R.id.loadingTextView);

        Button btnNavRiskDetail = v.findViewById(R.id.submit_btn);
        btnNavRiskDetail.setOnClickListener(v1 -> {
            if (main.isConnected == true) {
                // user selected a state and county, call APIs
                if (selectedLocation.getCounty() != null) {
                    // API data retrieved for selected state and county
                    if (mutableCovidSnapshot.getValue() != null && mutableCovidSnapshot.getValue().hasFieldsSet()) {
                        callback.onSubmitButtonClicked(mutableCovidSnapshot, selectedLocation);
                    } else {
                        // wait for API data to be retrieved before proceeding
                        loadingTextView.setText(R.string.loading_data);
                        vm.makeApiCalls(selectedLocation);
                        mutableCovidSnapshot.observe(getViewLifecycleOwner(), covidSnapshot -> {
                            if (!justForApiCalls && (covidSnapshot != null && covidSnapshot.hasFieldsSet())) {
                                CovidSnapshot mcsValue = mutableCovidSnapshot.getValue();
                                mcsValue.setLastUpdated(null);
                                mutableCovidSnapshot.setValue(mcsValue);
                                callback.onSubmitButtonClicked(mutableCovidSnapshot, selectedLocation);
                                mutableCovidSnapshot.removeObservers(getViewLifecycleOwner());
                            }
                        });
                    }
                } else {
                    loadingTextView.setText(R.string.pick_state_county);
                    Log.i(TAG, "onViewCreated - btnNavRiskDetail - selectedLocation not filled: " + selectedLocation.toString());
                }
            } else { // No Internet Access
                loadingTextView.setText(R.string.no_internet);
            }
        });

        resetWelcomeText(v, vm);

        Log.d(TAG, "onViewCreated invoked");
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
    }

    /**
     * Removes welcome text value if there is a stored snapshot
     * @param v the current view to change;
     * @param vm the current vm to check
     */
    private void resetWelcomeText(View v, CovidSnapshotWithLocationViewModel vm){
        final TextView welcome_tv = v.findViewById(R.id.fullscreen_content);

        vm.getLatestCovidSnapshot().observe(getViewLifecycleOwner(), covidSnapshotFromDb -> {
            if(covidSnapshotFromDb!=null){
                welcome_tv.setText("");
            } else {
                welcome_tv.setText(R.string.app_welcome);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String stateSelected;
        if (position > 0 && view != null) {
            if (parent.getId()==R.id.state_spinner) {
                // state spinner selected
                stateSelected = (String) parent.getItemAtPosition(position);
                Log.i(TAG, "onCreateView - mutableSelectedState: STATE SELECTED " + stateSelected);

                // get counties
                countyLocations = vm.getMapOfLocationsByState().get(stateSelected);
                List<String> countyNamesInner = countyLocations.stream()
                        .map(Location::getCounty).sorted().collect(Collectors.toList());
                countyNamesInner.add(0, Constants.SELECT_COUNTY);

                // set county spinner
                ArrayAdapter<String> countyAdapterInner = new ArrayAdapter<>(
                        getActivity(), android.R.layout.simple_spinner_item, countyNamesInner);
                countyAdapterInner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                county_spinner.setSelection(0, false);
                county_spinner.setAdapter(countyAdapterInner);
                county_spinner.setOnItemSelectedListener(this);
            } else if (parent.getId() == R.id.county_spinner) {
                // county spinner selected
                String countySelected = (String) parent.getItemAtPosition(position);
                Log.i(TAG, "onItemSelected: in county... " + countySelected);
                vm.setMutableCovidSnapshot(new CovidSnapshot());
                Log.i(TAG, "onCreateView - mutableSelectedCounty: COUNTY SELECTED " + countySelected);
                for (int i = 0; i < countyLocations.size(); i++) {
                    Location location = countyLocations.get(i);
                    if (location.getCounty().equals(countySelected)) {
                        Log.i(TAG, "onCreateView - mutableSelectedCounty: COUNTY FOUND" + countySelected);
                        selectedLocation = location;
                        mutableCovidSnapshot.getValue().setLocationId(selectedLocation.getLocationId());
                    }
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Required method
    }

    public void setInitialSpinners(View v) {
        List<String> stateNames = new ArrayList<>(vm.getMapOfLocationsByState().keySet());
        Collections.sort(stateNames);
        stateNames.add(0, Constants.SELECT_STATE);

        // State spinner
        state_spinner = v.findViewById(R.id.state_spinner);
        ArrayAdapter<String> state_adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, stateNames);
        state_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state_spinner.setSelection(0, false);
        state_spinner.setAdapter(state_adapter);
        state_spinner.setOnItemSelectedListener(this);

        // County spinner placeholder
        county_spinner = v.findViewById(R.id.county_spinner);
        List<String> countyNames = new ArrayList<>();
        countyNames.add(0, Constants.SELECT_COUNTY);
        ArrayAdapter<String> county_adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, countyNames);
        county_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        county_spinner.setAdapter(county_adapter);
        county_spinner.setOnItemSelectedListener(this);
    }

    public void setOnSubmitButtonListener(LocationManualSelectionFragment.OnSubmitButtonListener callback) {
        this.callback = callback;
    }

    public interface OnSubmitButtonListener {
        void onSubmitButtonClicked(MutableLiveData<CovidSnapshot> mcs, Location selectedLocation);
    }

    public void clearCovidSnapshot() {
        CovidSnapshot clear = new CovidSnapshot();
        vm.setMutableCovidSnapshot(clear);
    }
}

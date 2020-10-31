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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsc.covidscore.api.Requests;
import com.nsc.covidscore.api.VolleyJsonCallback;
import com.nsc.covidscore.room.CovidSnapshot;
import com.nsc.covidscore.room.CovidSnapshotWithLocationViewModel;
import com.nsc.covidscore.room.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class LocationManualSelectionFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = LocationManualSelectionFragment.class.getSimpleName();
    private MutableLiveData<String> mutableSelectedState = new MutableLiveData<>();
    private MutableLiveData<String> mutableSelectedCounty = new MutableLiveData<>();
    private Location selectedLocation = new Location();
    private CovidSnapshot selectedCovidSnapshot = new CovidSnapshot();
    private MutableLiveData<CovidSnapshot> mutableCovidSnapshot = new MutableLiveData<>(new CovidSnapshot());

    private CovidSnapshotWithLocationViewModel vm;
    private TextView loadingTextView;

    private FragmentActivity listener;
    private HashMap<String, List<Location>> mapOfLocationsByState = new HashMap<>();
    private HashMap<Integer, List<Location>> mapOfLocationsById = new HashMap<>();
    private List<Location> countyLocations = new ArrayList<>();

    public LocationManualSelectionFragment() {
        // Required empty public constructor
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
        // Access to Room Database
        vm = new ViewModelProvider(this).get(CovidSnapshotWithLocationViewModel.class);
        Log.d(TAG, "onCreate invoked");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_selection, container, false);
        Bundle bundle = getArguments();

        if (bundle != null) {
            // noinspection unchecked
            mapOfLocationsByState = (HashMap<String, List<Location>>) bundle.getSerializable(Constants.LOCATIONS_MAP_BY_STATE);
            mapOfLocationsById = (HashMap<Integer, List<Location>>) bundle.getSerializable(Constants.LOCATIONS_MAP_BY_ID);
            Log.i(TAG, "onCreateView: Bundle received from MainActivity");
        }

        handleSpinners(v);

        Log.d(TAG, "onCreateView invoked");
        return v;
    }

//     This event is triggered soon after onCreateView().
//     onViewCreated() is only called if the view returned from onCreateView() is non-null.
//     Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        loadingTextView = v.findViewById(R.id.loadingTextView);

        Button btnNavRiskDetail = v.findViewById(R.id.submit_btn);
        btnNavRiskDetail.setOnClickListener(v1 -> {
            mutableCovidSnapshot.observe(getViewLifecycleOwner(), covidSnapshot -> {

                if (covidSnapshot.hasFieldsSet()) {
                    // TODO: Save to Room, set Location ID on snapshot
                    saveSnapshotToRoom(selectedCovidSnapshot, selectedLocation);

                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    RiskDetailPageFragment riskDetailPageFragment = new RiskDetailPageFragment();

                    HashMap<Integer, Double> riskMap = RiskCalculation.getRiskCalculationsMap(
                            selectedCovidSnapshot.getCountyActiveCount(),
                            selectedCovidSnapshot.getCountyTotalPopulation(),
                            Constants.GROUP_SIZES);
                    Log.i(TAG, "onViewCreated: riskMap" + riskMap.toString());

                    Bundle bundle = new Bundle();
                    StringBuilder currentLocationSB = new StringBuilder(selectedLocation.getCounty())
                            .append(Constants.COMMA_SPACE).append(selectedLocation.getState());
                    bundle.putString(Constants.CURRENT_LOCATION, String.valueOf(currentLocationSB));
                    bundle.putString(Constants.ACTIVE_COUNTY, selectedCovidSnapshot.getCountyActiveCount().toString());
                    bundle.putString(Constants.ACTIVE_STATE, selectedCovidSnapshot.getStateActiveCount().toString());
                    bundle.putString(Constants.ACTIVE_COUNTRY, selectedCovidSnapshot.getCountryActiveCount().toString());
                    bundle.putString(Constants.TOTAL_COUNTY, selectedCovidSnapshot.getCountyTotalPopulation().toString());
                    bundle.putString(Constants.TOTAL_STATE, selectedCovidSnapshot.getStateTotalPopulation().toString());
                    bundle.putString(Constants.TOTAL_COUNTRY, selectedCovidSnapshot.getCountryTotalPopulation().toString());
                    bundle.putSerializable(Constants.RISK_MAP,riskMap);
                    riskDetailPageFragment.setArguments(bundle);
                    transaction.replace(R.id.fragContainer, riskDetailPageFragment, Constants.FRAGMENT_RDPF);
                    transaction.addToBackStack(null);

                    // Commit the transaction
                    transaction.commit();
                    mutableCovidSnapshot.setValue(new CovidSnapshot());
//                    selectedLocation = new Location();
//                    selectedCovidSnapshot = new CovidSnapshot();
                } else if (mutableSelectedState.getValue() == null || mutableSelectedCounty.getValue() == null) {
                    loadingTextView.setText(R.string.pick_state_county);

                } else {
                    loadingTextView.setText(R.string.loading_data);
                }

            });

        });
    }

    private void handleSpinners(View v) {
        List<String> stateNames = new ArrayList<>(mapOfLocationsByState.keySet());
        Collections.sort(stateNames);
        stateNames.add(0, Constants.SELECT_STATE);


        // State spinner
        Spinner state_spinner = v.findViewById(R.id.state_spinner);
        ArrayAdapter<String> state_adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, stateNames);
        state_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state_spinner.setSelection(0, false);
        state_spinner.setAdapter(state_adapter);
        state_spinner.setOnItemSelectedListener(this);

        // County spinner
        Spinner county_spinner = v.findViewById(R.id.county_spinner);
        List<String> countyNames = new ArrayList<>();
        countyNames.add(0, Constants.SELECT_COUNTY);
        ArrayAdapter<String> county_adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, countyNames);
        county_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        county_spinner.setAdapter(county_adapter);
        county_spinner.setOnItemSelectedListener(this);

        // observe state spinner selection
        mutableSelectedState.observe(getActivity(), selectedState -> {
            if (selectedState != null) {
                mutableCovidSnapshot.setValue(new CovidSnapshot());
                Log.i(TAG, "onCreateView - mutableSelectedState: STATE SELECTED " + selectedState);
                countyLocations= mapOfLocationsByState.get(selectedState);
                List<String> countyNamesInner = countyLocations.stream().map(Location::getCounty).sorted().collect(Collectors.toList());
                countyNamesInner.add(0, Constants.SELECT_COUNTY);
                ArrayAdapter<String> countyAdapterInner = new ArrayAdapter<>(
                        getActivity(), android.R.layout.simple_spinner_item, countyNamesInner);
                countyAdapterInner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                county_spinner.setSelection(0, false);
                county_spinner.setAdapter(countyAdapterInner);
                county_spinner.setOnItemSelectedListener(this);
            }
        });

        // observe county spinner selection
        mutableSelectedCounty.observe(getActivity(), selectedCounty -> {
            if (selectedCounty != null) {
                mutableCovidSnapshot.setValue(new CovidSnapshot());
                Log.i(TAG, "onCreateView - mutableSelectedCounty: COUNTY SELECTED " + selectedCounty);
                for (int i = 0; i < countyLocations.size(); i++) {
                    Location location = countyLocations.get(i);
                    if (location.getCounty().equals(selectedCounty)) {
                        Log.i(TAG, "onCreateView - mutableSelectedCounty: COUNTY FOUND, MAKING API CALLS" + selectedCounty);
                        selectedLocation = location;
                        selectedCovidSnapshot.setLocationId(selectedLocation.getLocationId());
                        makeApiCalls(selectedLocation);
                    }
                }
            }
        });
    }

    public void saveSnapshotToRoom(CovidSnapshot currentCovidSnapshot, Location currentLocation) {
        if (currentCovidSnapshot != null && currentCovidSnapshot.hasFieldsSet()) {
            // make sure to set LocationIdFK on Snapshot to current LocationIdPK
            if (currentCovidSnapshot.getLocationId() == null || currentCovidSnapshot.getLocationId() == 0) {
                if (currentCovidSnapshot.getLocationId() == null) {
                    // TODO: set boolean?
                }
                currentCovidSnapshot.setLocationId(currentLocation != null ? currentLocation.getLocationId() : -1);
            }
            Calendar calendar = Calendar.getInstance();
            currentCovidSnapshot.setLastUpdated(calendar);
            vm.insertCovidSnapshot(currentCovidSnapshot);
        } else {
            Log.e(TAG, "Incomplete Snapshot: " + currentCovidSnapshot.toString());
        }
        Log.d(TAG, "saveSnapshotToRoom invoked");
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
        if (position > 0) {
            if (parent.getId()==R.id.state_spinner) {
                String stateSelected = (String) parent.getItemAtPosition(position);
                mutableSelectedState.setValue(stateSelected);
            } else if (parent.getId() == R.id.county_spinner) {
                String countySelected = (String) parent.getItemAtPosition(position);
                Log.i(TAG, "onItemSelected: in county... " + countySelected);
                mutableSelectedCounty.setValue(countySelected);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Required method
    }

    private void makeApiCalls(Location location) {
        Log.i(TAG, "makeApiCalls: CALLED " + location.toString());
        CovidSnapshot covidSnapshot = new CovidSnapshot();
        covidSnapshot.setLocationId(location.getLocationId());
        mutableCovidSnapshot.getValue().setLocationId(location.getLocationId());
        Requests.getCounty(getActivity(), location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
                JSONObject stats = (JSONObject) response.get(Constants.RESPONSE_STATS);
                Integer confirmed = (Integer) stats.get(Constants.RESPONSE_CONFIRMED);
                Integer deaths = (Integer) stats.get(Constants.RESPONSE_DEATHS);
                // TODO: calculate better estimate of active cases
                Integer activeCounty = confirmed - deaths;
                covidSnapshot.setCountyActiveCount(activeCounty);
                mutableCovidSnapshot.getValue().setCountyActiveCount(activeCounty);
                if (covidSnapshot.hasFieldsSet()) {
                    selectedCovidSnapshot = covidSnapshot;
                    mutableCovidSnapshot.setValue(covidSnapshot);
                }
                Log.d(TAG, "getJsonData: county " + activeCounty);
            }

            @Override
            public void getJsonException(Exception exception) {
                Log.e(TAG, exception.getMessage());}

            @Override
            public void getString(String response) {}
        });
        Requests.getState(getContext(), location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
                Integer activeState = (Integer) response.get(Constants.RESPONSE_ACTIVE);
                covidSnapshot.setStateActiveCount(activeState);
                mutableCovidSnapshot.getValue().setStateActiveCount(activeState);
                if (covidSnapshot.hasFieldsSet()) {
//                    selectedCovidSnapshot = covidSnapshot;
                    mutableCovidSnapshot.setValue(covidSnapshot);
                }
            }

            @Override
            public void getJsonException(Exception exception) {
                Log.e(TAG, exception.getMessage());
            }

            @Override
            public void getString(String response) {}
        });
        Requests.getCountyHistorical(getContext(), location.toApiFormat(), Constants.DAYS_30, new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {
                Log.d(TAG, "getJsonData: countyHistorical " + response);
            }

            @Override
            public void getJsonException(Exception exception) {
                Log.e(TAG, exception.getMessage());}

            @Override
            public void getString(String response) {}
        });
        Requests.getUSHistorical(getContext(), Constants.DAYS_01, new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException, IOException {
                JSONObject timeline = response.getJSONObject(Constants.RESPONSE_TIMELINE);
                HashMap<String, Integer> totalMap = new ObjectMapper().readValue((timeline.get(Constants.RESPONSE_CASES)).toString(), HashMap.class);

                Integer totalCountry = 0;
                for (Object value : totalMap.values()) {
                    totalCountry = (Integer) value;
                }
                Integer deathCountry = 0;
                HashMap<String, Integer> deathMap = new ObjectMapper().readValue((timeline.get(Constants.RESPONSE_DEATHS)).toString(), HashMap.class);
                for (Object value : deathMap.values()) {
                    deathCountry = (Integer) value;
                }
                Integer recoveredCountry = 0;
                HashMap<String, Integer> recoveredMap = new ObjectMapper().readValue((timeline.get(Constants.RESPONSE_RECOVERED)).toString(), HashMap.class);
                for (Object value : recoveredMap.values()) {
                    recoveredCountry = (Integer) value;
                }

                Integer countryActiveCount = totalCountry - deathCountry - recoveredCountry;
                covidSnapshot.setCountryActiveCount(countryActiveCount);
                if (covidSnapshot.hasFieldsSet()) {
                    selectedCovidSnapshot = covidSnapshot;
                }
                Log.i(TAG, "getJsonData: country " + response);
            }

            @Override
            public void getJsonException(Exception exception) {
                Log.e(TAG, exception.getMessage());
            }

            @Override
            public void getString(String response) {}
        });
        Requests.getCountyPopulation(getActivity(), location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {}

            @Override
            public void getJsonException(Exception exception) {}

            @Override
            public void getString(String response) {
                covidSnapshot.setCountyTotalPopulation(Integer.parseInt(response));
                mutableCovidSnapshot.getValue().setCountyTotalPopulation(Integer.parseInt(response));
                if (covidSnapshot.hasFieldsSet()) {
                    selectedCovidSnapshot = covidSnapshot;
                    mutableCovidSnapshot.setValue(covidSnapshot);
                }
                //  Log.d(TAG, "getStringData: County  " + response);
            }
        });
        Requests.getStatePopulation(getActivity(), location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {}

            @Override
            public void getJsonException(Exception exception) {}

            @Override
            public void getString(String response) {
                covidSnapshot.setStateTotalPopulation(Integer.parseInt(response));
                mutableCovidSnapshot.getValue().setStateTotalPopulation(Integer.parseInt(response));
                if (covidSnapshot.hasFieldsSet()) {
                    selectedCovidSnapshot = covidSnapshot;
                    mutableCovidSnapshot.setValue(covidSnapshot);
                }
                //  Log.d(TAG, "getStringData: State  " + response);
            }
        });
        Requests.getCountryPopulation(getActivity(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {}

            @Override
            public void getJsonException(Exception exception) {}

            @Override
            public void getString(String response) {
                covidSnapshot.setCountryTotalPopulation(Integer.parseInt(response));
                mutableCovidSnapshot.getValue().setCountryTotalPopulation(Integer.parseInt(response));
                if (covidSnapshot.hasFieldsSet()) {
                    selectedCovidSnapshot = covidSnapshot;
                    mutableCovidSnapshot.setValue(covidSnapshot);
                }
                //  Log.d(TAG, "getStringData: Country " + response);
            }
        });
    }

}
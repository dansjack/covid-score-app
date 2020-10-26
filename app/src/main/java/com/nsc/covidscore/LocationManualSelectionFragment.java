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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsc.covidscore.api.Requests;
import com.nsc.covidscore.api.VolleyJsonCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationManualSelectionFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = LocationManualSelectionFragment.class.getSimpleName();
    private MutableLiveData<String> mutableSelectedState = new MutableLiveData<>();
    private MutableLiveData<String> mutableSelectedCounty = new MutableLiveData<>();
    private Location selectedLocation = new Location();
    private  MutableLiveData<CovidSnapshot> mutableCovidSnapshot = new MutableLiveData<>(new CovidSnapshot());

    private FragmentActivity listener;
    private HashMap<String, List<Location>> mapOfLocations = new HashMap<>();
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
            mapOfLocations = (HashMap<String, List<Location>>) bundle.getSerializable("allLocationsMap");
            Log.i(TAG, "onCreateView: " + mapOfLocations.toString());
        }

        List<String> stateNames = new ArrayList<>(mapOfLocations.keySet());
        Collections.sort(stateNames);

        // State spinner
        Spinner state_spinner = v.findViewById(R.id.state_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> state_adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, stateNames);
        // Specify the layout to use when the list of choices appears
        state_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        state_spinner.setAdapter(state_adapter);
        state_spinner.setOnItemSelectedListener(this);

        Spinner county_spinner = v.findViewById(R.id.county_spinner);
        // County spinner
        mutableSelectedState.observe(getActivity(), selectedState -> {
            if (selectedState != null) {
                countyLocations= mapOfLocations.get(selectedState);
                Log.i(TAG, "onCreateView: countyLocations" + Arrays.toString(countyLocations.toArray()));
                List<String> countyNames = countyLocations.stream().map(Location::getCounty).sorted().collect(Collectors.toList());
                Log.i(TAG, "onCreateView: countyNames" + Arrays.toString(countyNames.toArray()));
                ArrayAdapter<String> county_adapter = new ArrayAdapter<>(
                        getActivity(), android.R.layout.simple_spinner_item, countyNames);
                county_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                county_spinner.setAdapter(county_adapter);
                county_spinner.setOnItemSelectedListener(this);
            }
        });

        mutableSelectedCounty.observe(getActivity(), selectedCounty -> {
            if (selectedCounty != null) {
                Log.i(TAG, "onCreateView: COUNTY SELECTED " + selectedCounty);
                for (int i = 0; i < countyLocations.size(); i++) {
                    Location location = countyLocations.get(i);
                    if (location.getCounty().equals(selectedCounty)) {
                        Log.i(TAG, "onCreateView: FOUND COUNTY " + selectedCounty);
                        selectedLocation = location;
                        makeApiCalls(selectedLocation);
                    }
                }
            }
        });

        mutableCovidSnapshot.observe(getActivity(), covidSnapshot -> {
            Log.i(TAG, "onCreateView: mutableSnapshot Set: " + covidSnapshot.toString());
        });

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
        btnNavRiskDetail.setOnClickListener(v1 -> {
            Log.i(TAG, "BUTTON: CLICKED");
            Log.i(TAG, "BUTTON: LOCATION " + selectedLocation.toString());
            Log.i(TAG, "BUTTON: SNAPSHOT " + mutableCovidSnapshot.getValue().toString());
        });
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
            mutableSelectedState.setValue(stateSelected);
            //TODO: remove toast
            Toast.makeText
                    (listener.getApplicationContext(), "Selected : " + stateSelected, Toast.LENGTH_SHORT).show();

        }
        else if (parent.getId() == R.id.county_spinner)
        {
            String countySelected = (String) parent.getItemAtPosition(position);
            Log.i(TAG, "onItemSelected: in county.. " + mutableSelectedState);
            mutableSelectedCounty.setValue(countySelected);
            //TODO: remove toast
            Toast.makeText
                    (listener.getApplicationContext(), "Selected : " + countySelected, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Required method
    }

    private void makeApiCalls(Location location) {
        Log.i(TAG, "makeApiCalls: location " + location);
        Log.i(TAG, "makeApiCalls: locationApi " + location.toApiFormat());
        CovidSnapshot covidSnapshot = new CovidSnapshot();
        Requests.getCounty(getActivity(), location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
//                if (currentSnapshot == null) { currentSnapshot = new CovidSnapshot(); }
//                // Add Location to Database (if not already present)
//                if (!currentLocation.hasSameData(location)) {
//                    vm.insertLocation(location);
//                }
                JSONObject stats = (JSONObject) response.get("stats");
                Integer confirmed = (Integer) stats.get("confirmed");
                Integer deaths = (Integer) stats.get("deaths");
                // TODO: calculate better estimate of active cases
                Integer activeCounty = confirmed - deaths;
                covidSnapshot.setCountyActiveCount(activeCounty);
                if (covidSnapshot.hasFieldsSet()) {
                    Log.i(TAG, "getString: MUTABLE SET");
                    mutableCovidSnapshot.setValue(covidSnapshot);
                }
                Log.i(TAG, "snapShot settings: " + covidSnapshot.toString());
                Log.d(TAG, "getJsonData: county " + activeCounty);
            }

            @Override
            public void getJsonException(Exception exception) {
            }

            @Override
            public void getString(String response) {}
        });
        Requests.getState(getContext(), location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
//                if (currentSnapshot == null) { currentSnapshot = new CovidSnapshot(); }
//                // Add Location to Database (if not already present)
//                if (!currentLocation.hasSameData(location)) {
//                    vm.insertLocation(location);
//            }
                Integer activeState = (Integer) response.get("active");
                covidSnapshot.setStateActiveCount(activeState);
                if (covidSnapshot.hasFieldsSet()) {
                    Log.i(TAG, "getString: MUTABLE SET");
                    mutableCovidSnapshot.setValue(covidSnapshot);
                }
                Log.i(TAG, "snapShot settings: " + covidSnapshot.toString());
                Log.d(TAG, "getJsonData: state " + response);
            }

            @Override
            public void getJsonException(Exception exception) {

            }

            @Override
            public void getString(String response) {}
        });
        Requests.getCountyHistorical(getContext(), location.toApiFormat(), "30", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {
                Log.d(TAG, "getJsonData: countyHistorical " + response);
            }

            @Override
            public void getJsonException(Exception exception) {
            }

            @Override
            public void getString(String response) {}
        });
        Requests.getUSHistorical(getContext(), "1", new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException, IOException {
//                if (currentSnapshot == null) { currentSnapshot = new CovidSnapshot(); }
//                // Add Location to Database (if not already present)
//                if (!currentLocation.hasSameData(location)) {
//                    vm.insertLocation(location);
//                }
                JSONObject timeline = response.getJSONObject("timeline");
                HashMap<String, Integer> totalMap = new ObjectMapper().readValue((timeline.get("cases")).toString(), HashMap.class);

                Integer totalCountry = 0;
                for (Object value : totalMap.values()) {
                    totalCountry = (Integer) value;
                }
                Integer deathCountry = 0;
                HashMap<String, Integer> deathMap = new ObjectMapper().readValue((timeline.get("deaths")).toString(), HashMap.class);
                for (Object value : deathMap.values()) {
                    deathCountry = (Integer) value;
                }
                Integer recoveredCountry = 0;
                HashMap<String, Integer> recoveredMap = new ObjectMapper().readValue((timeline.get("recovered")).toString(), HashMap.class);
                for (Object value : recoveredMap.values()) {
                    recoveredCountry = (Integer) value;
                }

                Integer countryActiveCount = totalCountry - deathCountry - recoveredCountry;
                covidSnapshot.setCountryActiveCount(countryActiveCount);
                if (covidSnapshot.hasFieldsSet()) {
                    Log.i(TAG, "getString: MUTABLE SET");
                    mutableCovidSnapshot.setValue(covidSnapshot);
                }
                Log.i(TAG, "snapShot settings: " + covidSnapshot.toString());
                Log.d(TAG, "getJsonData: country " + response);
            }

            @Override
            public void getJsonException(Exception exception) {}

            @Override
            public void getString(String response) {}
        });
        Requests.getCountyPopulation(getActivity(), location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException, IOException {}

            @Override
            public void getJsonException(Exception exception) {}

            @Override
            public void getString(String response) {
                covidSnapshot.setCountyTotalPopulation(Integer.parseInt(response));
                if (covidSnapshot.hasFieldsSet()) {
                    Log.i(TAG, "getString: MUTABLE SET");
                    mutableCovidSnapshot.setValue(covidSnapshot);
                }
                Log.i(TAG, "snapShot settings: " + covidSnapshot.toString());
                Log.d(TAG, "getStringData: State  " + response);
            }
        });
        Requests.getStatePopulation(getActivity(), location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {}

            @Override
            public void getJsonException(Exception exception) {}

            @Override
            public void getString(String response) {
//                if (currentSnapshot == null) { currentSnapshot = new CovidSnapshot(); }
//                vm.insertLocation(location);
                covidSnapshot.setStateTotalPopulation(Integer.parseInt(response));
                if (covidSnapshot.hasFieldsSet()) {
                    Log.i(TAG, "getString: MUTABLE SET");
                    mutableCovidSnapshot.setValue(covidSnapshot);
                }
                Log.i(TAG, "snapShot settings: " + covidSnapshot.toString());
                Log.d(TAG, "getStringData: State  " + response);
            }
        });
        Requests.getCountryPopulation(getActivity(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) {}

            @Override
            public void getJsonException(Exception exception) {}

            @Override
            public void getString(String response) {
//                if (currentSnapshot == null) { currentSnapshot = new CovidSnapshot(); }
//                vm.insertLocation(location);
                covidSnapshot.setCountryTotalPopulation(Integer.parseInt(response));
                if (covidSnapshot.hasFieldsSet()) {
                    Log.i(TAG, "getString: MUTABLE SET");
                    mutableCovidSnapshot.setValue(covidSnapshot);
                }
                Log.i(TAG, "snapShot settings: " + covidSnapshot.toString());
                Log.d(TAG, "getStringData: Country " + response);
            }
        });
    }

}
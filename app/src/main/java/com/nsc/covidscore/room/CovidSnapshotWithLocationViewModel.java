package com.nsc.covidscore.room;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsc.covidscore.Constants;
import com.nsc.covidscore.api.Requests;
import com.nsc.covidscore.api.VolleyJsonCallback;
import com.nsc.covidscore.room.CovidSnapshot;
import com.nsc.covidscore.room.CovidSnapshotWithLocationRepository;
import com.nsc.covidscore.room.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CovidSnapshotWithLocationViewModel extends AndroidViewModel {

    private CovidSnapshotWithLocationRepository repo;
    private Context context;
    private final String TAG = "CovidSnapshotWithLocationViewModel";
    private boolean isConnected;
    private MutableLiveData<CovidSnapshot> mutableCovidSnapshot = new MutableLiveData<>(new CovidSnapshot());

    private HashMap<String, List<Location>> mapOfLocationsByState = new HashMap<>();
    private HashMap<Integer, Location> mapOfLocationsById = new HashMap<>();

    public CovidSnapshotWithLocationViewModel(Application application) {
        super(application);
        repo = new CovidSnapshotWithLocationRepository(application);
        context = application.getApplicationContext();
        isConnected = false;
        fillLocationsMaps();
    }

    public HashMap<String, List<Location>> getMapOfLocationsByState() {
        return mapOfLocationsByState;
    }

    public HashMap<Integer, Location> getMapOfLocationsById() {
        return mapOfLocationsById;
    }

    public void insertCovidSnapshot(CovidSnapshot covidSnapshot) {
        if (covidSnapshot.getLocationId() != null) {
            repo.insertCovidSnapshot(covidSnapshot);
        }
    }

    public LiveData<CovidSnapshot> getLatestCovidSnapshot() { return repo.getLatestSnapshot(); }

    // Returns a list of up to 3 LiveData objects (CovidSnapshots)
    // directly from AppDatabase via CovidSnapshotDao.
    public LiveData<List<CovidSnapshot>> getLatestLocationsLatestCovidSnapshots() {
        return repo.getLatestLocationsLatestSnapshots();
    }

    /**
     * <p>
     *     Fills local variables, mapOfLocationsById and mapOfLocationsByState from assets/county_fips.json.
     * </p>
     */
    private void fillLocationsMaps() {
        String jsonString;
        JSONArray jsonArray;
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(Constants.LOCATION_FILENAME);
            byte[] buffer = new byte[inputStream.available()];
            int read = inputStream.read(buffer);
            if (read == -1) {
                inputStream.close();
            }
            jsonString = new String(buffer, StandardCharsets.UTF_8);
            jsonArray = new JSONArray(jsonString);
            for (int i = 1; i < jsonArray.length(); i++) {
                JSONArray currentArray = jsonArray.getJSONArray(i);
                Integer locationId = currentArray.getInt(0);
                // split county and state names
                String[] nameArray = currentArray.getString(1).split(Constants.COMMA);
                String countyName = nameArray[0].trim();
                String stateName = nameArray[1].trim();
                String stateFips = currentArray.getString(2);
                String countyFips = currentArray.getString(3);
                Location countyInState = new Location(locationId, countyName, stateName, countyFips, stateFips);

                mapOfLocationsById.put(locationId, countyInState);
                if (mapOfLocationsByState.get(stateName) == null) {
                    mapOfLocationsByState.put(stateName, new ArrayList<>());
                }
                mapOfLocationsByState.get(stateName).add(countyInState);
            }

        } catch (IOException | JSONException exception) {
            exception.printStackTrace();
        }
    }

    public void setConnectionStatus(boolean connected) {
        isConnected = connected;
    }

    public boolean getConnectionStatus() {
        return isConnected;
    }

    public MutableLiveData<CovidSnapshot> getMutableCovidSnapshot() {
        return mutableCovidSnapshot;
    }

    public void setMutableCovidSnapshot(CovidSnapshot covidSnapshot) {
        this.mutableCovidSnapshot.setValue(covidSnapshot);
    }

    /**
     * Fills a CovidSnapshot instance with data from two APIs, disease.sh and U.S. Census Bureau,
     * once all data has been retrieved.
     * The following endpoints are called:
     *  disease.sh
     *      - county COVID-19 data (Johns Hopkins University)
     *      - state COVID-19 data (Worldometer)
     *      - country COVID-19 data (Johns Hopkins University)
     *      - country historical COVID-19 data (Johns Hopkins University)
     *  U.S. Census Bureau
     *      - county population data
     *      - state population data
     *      - country population data
     * @param location the Location instance to retrieve COVID-19 and Population stats for
     */
    public void makeApiCalls(Location location) {
        Log.i(TAG, "makeApiCalls: CALLED " + location.toString());
        CovidSnapshot covidSnapshot = new CovidSnapshot();
        covidSnapshot.setLocationId(location.getLocationId());
        mutableCovidSnapshot.getValue().setLocationId(location.getLocationId());

        Requests.getCounty(context, location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
                JSONObject stats = (JSONObject) response.get(Constants.RESPONSE_STATS);
                Integer confirmed = (Integer) stats.get(Constants.RESPONSE_CONFIRMED);
                Integer deaths = (Integer) stats.get(Constants.RESPONSE_DEATHS);
                // TODO: calculate better estimate of active cases
                Integer activeCounty = confirmed - deaths;
                covidSnapshot.setCountyActiveCount(activeCounty);
                mutableCovidSnapshot.getValue().setCountyActiveCount(activeCounty);
                RoomHelpers.setSnapshot(mutableCovidSnapshot, covidSnapshot);
                Log.d(TAG, "req: getActiveCounty " + activeCounty);
            }

            @Override
            public void getJsonException(Exception exception) {
                Log.e(TAG, exception.getMessage());}
        });
        Requests.getState(context, location.toApiFormat(), new VolleyJsonCallback() {
            @Override
            public void getJsonData(JSONObject response) throws JSONException {
                Integer activeState = (Integer) response.get(Constants.RESPONSE_CASES);
                covidSnapshot.setStateActiveCount(activeState);
                mutableCovidSnapshot.getValue().setStateActiveCount(activeState);
                RoomHelpers.setSnapshot(mutableCovidSnapshot, covidSnapshot);
                Log.d(TAG, "req: getActiveState " + activeState);
            }

            @Override
            public void getJsonException(Exception exception) {
                Log.e(TAG, exception.getMessage());
            }
        });
        Requests.getUSHistorical(context, Constants.DAYS_01, new VolleyJsonCallback() {
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
                RoomHelpers.setSnapshot(mutableCovidSnapshot, covidSnapshot);
                Log.i(TAG, "req: getCountryHistorical " + response);
            }

            @Override
            public void getJsonException(Exception exception) {
                Log.e(TAG, exception.getMessage());
            }
        });
        Requests.getCountyPopulation(context, location, response -> {
            covidSnapshot.setCountyTotalPopulation(Integer.parseInt(response));
            mutableCovidSnapshot.getValue().setCountyTotalPopulation(Integer.parseInt(response));
            RoomHelpers.setSnapshot(mutableCovidSnapshot, covidSnapshot);
            Log.d(TAG, "req: getCountyPopulation  " + response);
        });
        Requests.getStatePopulation(context, location, response -> {
            covidSnapshot.setStateTotalPopulation(Integer.parseInt(response));
            mutableCovidSnapshot.getValue().setStateTotalPopulation(Integer.parseInt(response));
            RoomHelpers.setSnapshot(mutableCovidSnapshot, covidSnapshot);
            Log.d(TAG, "req: getStatePopulation  " + response);
        });
        Requests.getCountryPopulation(context, response -> {
            covidSnapshot.setCountryTotalPopulation(Integer.parseInt(response));
            mutableCovidSnapshot.getValue().setCountryTotalPopulation(Integer.parseInt(response));
            RoomHelpers.setSnapshot(mutableCovidSnapshot, covidSnapshot);
            Log.d(TAG, "req: getCountryPopulation " + response);
        });
    }
}

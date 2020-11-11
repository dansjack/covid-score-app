package com.nsc.covidscore.room;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nsc.covidscore.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CovidSnapshotWithLocationViewModel extends AndroidViewModel {

    private CovidSnapshotWithLocationRepository repo;
    private Context context;


    private HashMap<String, List<Location>> mapOfLocationsByState = new HashMap<>();
    private HashMap<Integer, Location> mapOfLocationsById = new HashMap<>();

    public CovidSnapshotWithLocationViewModel(Application application) {
        super(application);
        repo = new CovidSnapshotWithLocationRepository(application);
        context = application.getApplicationContext();
        fillLocationsMaps();
    }

    public HashMap<String, List<Location>> getMapOfLocationsByState() {
        return mapOfLocationsByState;
    }

    public HashMap<Integer, Location> getMapOfLocationsById() {
        return mapOfLocationsById;
    }

    public void insertCovidSnapshot(CovidSnapshot covidSnapshot) {
        // TODO: fix the timing issue in Main so we can add this back
        if (covidSnapshot.getLocationId() != null) {
            repo.insertCovidSnapshot(covidSnapshot);
        }
    }

//    public LiveData<CovidSnapshot> getLatestCovidSnapshotByLocation(Location location) { return repo.getLatestCovidSnapshotByLocation(location); }

    public LiveData<CovidSnapshot> getLatestCovidSnapshot() { return repo.getLatestSnapshot(); }

    // Returns a list of up to 3 LiveData objects (locationIDs)
    // directly from AppDatabase via CovidSnapshotDao.
    public LiveData<List<CovidSnapshot>> getLatestLocationsList() {
        return repo.getLatestLocationsList();
    }

    public LiveData<CovidSnapshot> getLatestCovidSnapshotByLocationId(int locationId) {
        return repo.getLatestCovidSnapshotByLocationId(locationId);
    }


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
}


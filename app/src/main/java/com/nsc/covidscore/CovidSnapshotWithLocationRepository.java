package com.nsc.covidscore;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class CovidSnapshotWithLocationRepository {

    private static final String TAG = CovidSnapshotWithLocationRepository.class.getSimpleName();

    private LocationDao locationDao;
    private CovidSnapshotDao covidSnapshotDao;
    private LiveData<List<Location>> allLocations;
    private LiveData<Location> currentLocation;
    private LiveData<CovidSnapshot> currentSnapshot;

    CovidSnapshotWithLocationRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        locationDao = db.locationDao();
        allLocations = locationDao.getAll();

        covidSnapshotDao = db.covidSnapshotDao();
        currentSnapshot = covidSnapshotDao.findLatest();

        if (currentSnapshot != null && currentSnapshot.getValue() != null) {
            currentLocation = locationDao.findByLocationId(currentSnapshot.getValue().getLocationId());
        }
    }

    Integer insertLocation(Location location) {
        final Location[] lastAdded = new Location[1];
        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (locationDao.findByCountyAndState(location.getCounty(), location.getState()).getValue() == null) {
                locationDao.insert(location);
                Log.e(TAG, "insertLocation: " + location.toApiFormat());
                lastAdded[0] = locationDao.getMostRecent().getValue();
            }
        });
        if (lastAdded[0] != null) {
            Log.e(TAG, "lastAdded location: " + lastAdded[0].toApiFormat());
            return lastAdded[0].getLocationId();
        } else {
            Log.e(TAG, "location table is empty");
            return 0;
        }
    }

    void insertCovidSnapshot(CovidSnapshot covidSnapshot) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            covidSnapshotDao.insert(covidSnapshot);
        });
        Log.e(TAG, "Inserted: " + covidSnapshot.toString());
    }

    LiveData<CovidSnapshot> getLatestCovidSnapshotByLocation(Location location) {
        return covidSnapshotDao.findLatestByLocationId(location.getLocationId());
    }

    LiveData<CovidSnapshot> getCurrentSnapshot() { return currentSnapshot; }

    LiveData<Location> getCurrentLocation() { return currentLocation; }

    LiveData<List<Location>> getAllLocations() { return allLocations; }
}

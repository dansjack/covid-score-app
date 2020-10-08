package com.nsc.covidscore;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class CovidSnapshotWithLocationRepository {

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

        currentLocation = locationDao.findByLocationId(currentSnapshot.getValue().getLocationId());
    }

    void insertLocation(Location location) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (locationDao.findByCountyAndState(location.getCounty(), location.getState()).getValue() != null) {
                locationDao.insert(location);
            }
        });
    }

    LiveData<CovidSnapshot> getLatestCovidSnapshotByLocation(Location location) {
        return covidSnapshotDao.findLatestByLocationId(location.getLocationId());
    }

    LiveData<CovidSnapshot> getCurrentSnapshot() { return currentSnapshot; }

    LiveData<Location> getCurrentLocation() { return currentLocation; }

    LiveData<List<Location>> getAllLocations() { return allLocations; }
}

package com.nsc.covidscore.room;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
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
        currentSnapshot = covidSnapshotDao.getLatest();

        if (currentSnapshot != null && currentSnapshot.getValue() != null) {
            currentLocation = locationDao.findByLocationId(currentSnapshot.getValue().getLocationId());
        } else {
            currentLocation = locationDao.getLatest();
        }
    }

    void insertLocation(Location location) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            if ((currentLocation.getValue() == null || !currentLocation.getValue().equals(location))
                    && (locationDao.findByCountyAndState(location.getCounty(), location.getState()).getValue() == null)
                    && (allLocations.getValue() == null || !Location.alreadyInRoom(location, allLocations.getValue()))) {
                // TODO: fix duplicate additions
                int newId = (int) locationDao.insert(location);

                if (newId != 0 && newId != -1) {
                    Log.e(TAG, "Inserted Location: id: " + newId + ", " + location.toApiFormat());
                } else {
                    Log.e(TAG, "Failed to insert location: " + location.toApiFormat());
                }
            } else {
                Log.e(TAG, "Room already contains location: " + location.toApiFormat());
            }
        });
        currentLocation = locationDao.getLatest();
        allLocations = locationDao.getAll();
    }

    void insertCovidSnapshot(CovidSnapshot covidSnapshot) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Location savedLocation = currentLocation.getValue();
            if ((currentSnapshot.getValue() == null || !currentSnapshot.getValue().hasSameData(covidSnapshot)) && covidSnapshot.getLocationId() != null) {
                Calendar calendar = Calendar.getInstance();
                covidSnapshot.setLastUpdated(calendar);
                covidSnapshotDao.insert(covidSnapshot);
                Log.e(TAG, "Inserted: " + covidSnapshot.toString());
            } else {
                Log.e(TAG, "Did not insert: " + covidSnapshot.toString());
            }
        });
        currentSnapshot = covidSnapshotDao.getLatest();
    }

    LiveData<CovidSnapshot> getLatestCovidSnapshotByLocation(Location location) {
        return covidSnapshotDao.findLatestByLocationId(location.getLocationId());
    }

    LiveData<CovidSnapshot> getLatestSnapshot() {
        return covidSnapshotDao.getLatest();
    }

    LiveData<CovidSnapshot> getSavedCovidSnapshot() {
        return currentSnapshot;
    }

    LiveData<Location> getLatestLocation() {
        return locationDao.getLatest();
    }

    LiveData<Location> getSavedLocation() {
        return currentLocation;
    }

    LiveData<List<Location>> getAllLocations() { return allLocations; }
}

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
        }
    }

    Integer insertLocation(Location location) {
        final Location[] lastAdded = new Location[1];
        AppDatabase.databaseWriteExecutor.execute(() -> {
            if ((currentLocation.getValue() == null || !currentLocation.getValue().equals(location)) && locationDao.findByCountyAndState(location.getCounty(), location.getState()).getValue() == null) {
                locationDao.insert(location);
                Log.e(TAG, "Inserted Location: " + location.toApiFormat());
                currentLocation = locationDao.getLatest();
                lastAdded[0] = currentLocation.getValue();
                if (lastAdded[0] != null) {
                    Log.e(TAG, "lastAdded location: " + lastAdded[0].toApiFormat());
                    return lastAdded[0].getLocationId();
                } else {
                    Log.e(TAG, "location table is empty");
                    return 0;
                }
            }
        });

    }

    void insertCovidSnapshot(CovidSnapshot covidSnapshot) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (currentSnapshot.getValue() == null || !currentSnapshot.getValue().hasSameData(covidSnapshot)) {
                Calendar calendar = Calendar.getInstance();
                covidSnapshot.setLastUpdated(calendar);
                covidSnapshotDao.insert(covidSnapshot);
                Log.e(TAG, "Inserted: " + covidSnapshot.toString());
            } else {
                Log.e(TAG, "Did not insert: " + covidSnapshot.toString());
            }
            currentSnapshot = covidSnapshotDao.getLatest();
        });
    }

    LiveData<CovidSnapshot> getLatestCovidSnapshotByLocation(Location location) {
        return covidSnapshotDao.findLatestByLocationId(location.getLocationId());
    }

    LiveData<CovidSnapshot> getLatestSnapshot() {
        //not sure this is necessary, since I'm saving the local variable on every insertion...
        LiveData<CovidSnapshot> latestSnapshot = covidSnapshotDao.getLatest();
        this.currentSnapshot = latestSnapshot;
        return currentSnapshot;
    }

    LiveData<Location> getLatestLocation() {
        //not sure this is necessary, since I'm saving the local variable on every insertion...
        LiveData<Location> latestLocation = locationDao.getLatest();
        this.currentLocation = latestLocation;
        return currentLocation;
    }

    LiveData<List<Location>> getAllLocations() { return allLocations; }
}

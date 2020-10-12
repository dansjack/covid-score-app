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

    Integer getMostRecentId() {
        Location location = locationDao.getMostRecent().getValue();
        return location != null ? location.getLocationId() : null;
    }

    Integer insertLocation(Location location) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (locationDao.findByCountyAndState(location.getCounty(), location.getState()).getValue() != null) {
                locationDao.insert(location);
                Log.e(TAG, "insertLocation: " + location.toApiFormat());
            }
        });
        Location lastAdded = locationDao.getMostRecent().getValue();
        return lastAdded != null ? lastAdded.getLocationId() : 0;
    }

    void insertCovidSnapshot(CovidSnapshot covidSnapshot) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            covidSnapshotDao.insert(covidSnapshot);
        });
        Log.e("Repo 53: ", covidSnapshot.toString());
    }

    LiveData<CovidSnapshot> getLatestCovidSnapshotByLocation(Location location) {
        return covidSnapshotDao.findLatestByLocationId(location.getLocationId());
    }

    LiveData<CovidSnapshot> getCurrentSnapshot() { return currentSnapshot; }

    LiveData<Location> getCurrentLocation() { return currentLocation; }

    LiveData<List<Location>> getAllLocations() { return allLocations; }
}

package com.nsc.covidscore.room;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.List;

public class CovidSnapshotWithLocationRepository {

    private static final String TAG = CovidSnapshotWithLocationRepository.class.getSimpleName();

    private CovidSnapshotDao covidSnapshotDao;
    private LiveData<CovidSnapshot> currentSnapshot;

    CovidSnapshotWithLocationRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);

        covidSnapshotDao = db.covidSnapshotDao();
        currentSnapshot = covidSnapshotDao.getLatest();
    }

    void insertCovidSnapshot(CovidSnapshot covidSnapshot) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
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

    // Returns a list of up to 3 LiveData objects (locationIDs)
    // directly from AppDatabase via CovidSnapshotDao.
    LiveData<List<CovidSnapshot>> getLatestLocations() {
        return covidSnapshotDao.getLatestLocations();
    }

    LiveData<CovidSnapshot> getLatestCovidSnapshotByLocationId(int locationId) {
        return covidSnapshotDao.findLatestByLocationId(locationId);
    }

}
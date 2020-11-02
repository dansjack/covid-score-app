package com.nsc.covidscore.room;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nsc.covidscore.room.CovidSnapshot;
import com.nsc.covidscore.room.CovidSnapshotWithLocationRepository;
import com.nsc.covidscore.room.Location;

import java.util.List;

public class CovidSnapshotWithLocationViewModel extends AndroidViewModel {

    private CovidSnapshotWithLocationRepository repo;

    public CovidSnapshotWithLocationViewModel(Application application) {
        super(application);
        repo = new CovidSnapshotWithLocationRepository(application);
    }

    public void insertCovidSnapshot(CovidSnapshot covidSnapshot) {
        // TODO: fix the timing issue in Main so we can add this back
        if (covidSnapshot.getLocationId() != null) {
            repo.insertCovidSnapshot(covidSnapshot);
        }
    }

    public LiveData<CovidSnapshot> getLatestCovidSnapshotByLocation(Location location) { return repo.getLatestCovidSnapshotByLocation(location); }

    public LiveData<CovidSnapshot> getLatestCovidSnapshot() { return repo.getLatestSnapshot(); }
}


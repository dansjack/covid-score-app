package com.nsc.covidscore;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CovidSnapshotWithLocationViewModel extends AndroidViewModel {

    private CovidSnapshotWithLocationRepository repo;

    public CovidSnapshotWithLocationViewModel(Application application) {
        super(application);
        repo = new CovidSnapshotWithLocationRepository(application);
    }

    public Integer insertLocation(Location location) { return repo.insertLocation(location); }

    public void insertCovidSnapshot(CovidSnapshot covidSnapshot) { repo.insertCovidSnapshot(covidSnapshot); }

    public LiveData<CovidSnapshot> getLatestCovidSnapshotByLocation(Location location) { return repo.getLatestCovidSnapshotByLocation(location); }

    public LiveData<CovidSnapshot> getCurrentCovidSnapshot() { return repo.getCurrentSnapshot(); }

    public LiveData<Location> getCurrentLocation() { return repo.getCurrentLocation(); }

    public LiveData<List<Location>> getAllLocations() { return repo.getAllLocations(); }

}


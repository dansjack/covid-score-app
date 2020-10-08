package com.nsc.covidscore;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface CovidSnapshotWithLocationDao {

    @Transaction
    @Query("SELECT * FROM location")
    LiveData<List<CovidSnapshotWithLocation>> getCovidSnapshotsWithLocations();

    @Transaction
    @Query("SELECT * FROM location WHERE county LIKE :county AND state LIKE :state LIMIT 1")
    LiveData<CovidSnapshotWithLocation> getCovidSnapshotsWithLocationByCountyAndState(String county, String state);

    @Transaction
    @Query("SELECT * FROM location WHERE location_id = :locationId LIMIT 1")
    LiveData<CovidSnapshotWithLocation> getCovidSnapshotsWithLocationByLocationId(Integer locationId);

}

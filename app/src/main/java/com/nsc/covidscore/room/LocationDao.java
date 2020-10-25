package com.nsc.covidscore.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.nsc.covidscore.room.Location;

import java.util.Calendar;
import java.util.List;

@Dao
public interface LocationDao {

    @Query("SELECT * FROM location")
    LiveData<List<Location>> getAll();

    @Query("SELECT * FROM location WHERE county LIKE :county AND state LIKE :state LIMIT 1")
    LiveData<Location> findByCountyAndState(String county, String state);

    @Query("SELECT * FROM location WHERE location_id = :locationId LIMIT 1")
    LiveData<Location> findByLocationId(Integer locationId);

    @Query("SELECT * FROM location ORDER BY last_updated DESC LIMIT 1")
    LiveData<Location> getLatest();

    @Transaction
    @Query("SELECT * FROM location")
    LiveData<List<CovidSnapshotWithLocation>> getCovidSnapshotsWithLocations();

    @Transaction
    @Query("SELECT * FROM location WHERE county LIKE :county AND state LIKE :state LIMIT 1")
    LiveData<CovidSnapshotWithLocation> getCovidSnapshotsWithLocationByCountyAndState(String county, String state);

    @Transaction
    @Query("SELECT * FROM location WHERE location_id = :locationId LIMIT 1")
    LiveData<CovidSnapshotWithLocation> getCovidSnapshotsWithLocationByLocationId(Integer locationId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Location location);

    @Query("UPDATE location SET last_updated = :lastUpdated WHERE location_id = :id")
    void updateLocation(Integer id, Calendar lastUpdated);

    @Query("UPDATE location SET last_updated = :lastUpdated WHERE county = :county AND state = :state")
    void updateLocation(String county, String state, Calendar lastUpdated);
}

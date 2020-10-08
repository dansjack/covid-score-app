package com.nsc.covidscore;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationDao {

    @Query("SELECT * FROM location")
    LiveData<List<Location>> getAll();

    @Query("SELECT * FROM location WHERE county LIKE :county AND state LIKE :state LIMIT 1")
    LiveData<Location> findByCountyAndState(String county, String state);

    @Query("SELECT * FROM location WHERE location_id = :locationId LIMIT 1")
    LiveData<Location> findByLocationId(Integer locationId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Location location);

}

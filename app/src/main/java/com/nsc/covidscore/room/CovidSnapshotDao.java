package com.nsc.covidscore.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CovidSnapshotDao {

    @Query("SELECT * FROM covid_snapshot")
    LiveData<List<CovidSnapshot>> getAll();

    @Query("SELECT * FROM covid_snapshot WHERE covid_snapshot_id = :id LIMIT 1")
    LiveData<CovidSnapshot> findById(Integer id);

    @Query("SELECT * FROM covid_snapshot WHERE location_id = :locationId ORDER BY last_updated LIMIT 1")
    LiveData<CovidSnapshot> findLatestByLocationId(Integer locationId);

    @Query("SELECT * FROM covid_snapshot ORDER BY last_updated DESC LIMIT 1")
    LiveData<CovidSnapshot> getLatest();

    @Query("SELECT DISTINCT location_id FROM covid_snapshot ORDER BY last_updated DESC LIMIT 3")
    LiveData<List<Integer>> getLastThreeLocationIds();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CovidSnapshot covidSnapshot);

    @Query("SELECT DISTINCT location_id, covid_snapshot_id FROM covid_snapshot ORDER BY last_updated DESC LIMIT 3")
    LiveData<List<CovidSnapshot>> getLatestLocationsList();

}

package com.nsc.covidscore;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "covid_snapshot")
public class CovidSnapshot {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "covid_snapshot_id")
    private Integer covidSnapshotId;
    private Integer getCovidSnapshotId() { return covidSnapshotId; }

    @NonNull
    @ColumnInfo(name = "location_id")
    private Integer locationId;
    private Integer getLocationId() { return locationId; }

    @ColumnInfo(name = "county_active_count")
    private Integer countyActiveCount;
    private Integer getCountyActiveCount() { return countyActiveCount; }

    @ColumnInfo(name = "state_active_count")
    private Integer stateActiveCount;
    private Integer getStateActiveCount() { return stateActiveCount; }

    @ColumnInfo(name = "country_active_count")
    private Integer countryActiveCount;
    private Integer getCountryActiveCount() { return countryActiveCount; }

    @ColumnInfo(name = "last_updated_api")
    private Calendar lastUpdatedApi;
    private Calendar getLastUpdatedApi() { return lastUpdatedApi; }

    @ColumnInfo(name = "last_updated_room")
    private Calendar lastUpdatedRoom;
    private Calendar getLastUpdatedRoom() { return lastUpdatedRoom; }

    // CONSTRUCTOR

    public CovidSnapshot(Integer covidSnapshotId,
                         Integer locationId,
                         Integer countyActiveCount,
                         Integer stateActiveCount,
                         Integer countryActiveCount,
                         Calendar lastUpdatedApi,
                         Calendar lastUpdatedRoom) {
        this.covidSnapshotId = covidSnapshotId;
        this.locationId = locationId;
        this.countyActiveCount = countyActiveCount;
        this.stateActiveCount = stateActiveCount;
        this.countryActiveCount = countryActiveCount;
        this.lastUpdatedApi = lastUpdatedApi;
        this.lastUpdatedRoom = lastUpdatedRoom;
    }

}

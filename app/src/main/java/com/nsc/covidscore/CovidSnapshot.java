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
    public Integer getCovidSnapshotId() { return covidSnapshotId; }

    @NonNull
    @ColumnInfo(name = "location_id")
    private Integer locationId;
    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }

    @ColumnInfo(name = "county_active_count")
    private Integer countyActiveCount;
    public Integer getCountyActiveCount() { return countyActiveCount; }
    public void setCountyActiveCount(Integer countyActiveCount) { this.countyActiveCount = countyActiveCount; }

    @ColumnInfo(name = "state_active_count")
    private Integer stateActiveCount;
    public Integer getStateActiveCount() { return stateActiveCount; }
    public void setStateActiveCount(Integer stateActiveCount) { this.stateActiveCount = stateActiveCount; }

    @ColumnInfo(name = "country_active_count")
    private Integer countryActiveCount;
    public Integer getCountryActiveCount() { return countryActiveCount; }
    public void setCountryActiveCount(Integer countryActiveCount) { this.countryActiveCount = countryActiveCount; }

    @ColumnInfo(name = "last_updated_api")
    private Calendar lastUpdatedApi;
    public Calendar getLastUpdatedApi() { return lastUpdatedApi; }
    public void setLastUpdatedApi(Calendar lastUpdatedApi) { this.lastUpdatedApi = lastUpdatedApi; }

    @ColumnInfo(name = "last_updated_room")
    private Calendar lastUpdatedRoom;
    public Calendar getLastUpdatedRoom() { return lastUpdatedRoom; }
    public void setLastUpdatedRoom(Calendar lastUpdatedRoom) { this.lastUpdatedRoom = lastUpdatedRoom; }

    // CONSTRUCTOR

    public CovidSnapshot() {
    }

    public CovidSnapshot(Integer locationId,
                         Integer countyActiveCount,
                         Integer stateActiveCount,
                         Integer countryActiveCount,
                         Calendar lastUpdatedApi,
                         Calendar lastUpdatedRoom) {
        this.locationId = locationId;
        this.countyActiveCount = countyActiveCount;
        this.stateActiveCount = stateActiveCount;
        this.countryActiveCount = countryActiveCount;
        this.lastUpdatedApi = lastUpdatedApi;
        this.lastUpdatedRoom = lastUpdatedRoom;
    }

}

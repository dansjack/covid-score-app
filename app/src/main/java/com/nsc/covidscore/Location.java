package com.nsc.covidscore;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "location")
class Location {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "location_id")
    private Integer locationId;
    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }

    @NonNull
    @ColumnInfo(name = "county")
    private String county;
    public String getCounty() { return county; }
    public void setCounty(String county) { this.county = county.toLowerCase(); }

    @NonNull
    @ColumnInfo(name = "state")
    private String state;
    public String getState() { return state; }
    public void setState(String state) { this.state = state.toLowerCase(); }

    @ColumnInfo(name = "last_updated")
    private Calendar lastUpdated;
    public Calendar getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Calendar lastUpdated) { this.lastUpdated = lastUpdated; }

    // CONSTRUCTOR

    public Location(String county, String state) {
        this.county = county.toLowerCase();
        this.state = state.toLowerCase();
        Calendar calendar = Calendar.getInstance();
        this.lastUpdated = calendar;
    }

    public String toApiFormat() { return county + "," + state; }

}

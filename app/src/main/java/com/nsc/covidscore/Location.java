package com.nsc.covidscore;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
    public void setCounty(String county) { this.county = county; }

    @NonNull
    @ColumnInfo(name = "state")
    private String state;
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    // CONSTRUCTOR

    public Location(Integer locationId, String county, String state) {
        this.locationId = locationId;
        this.county = county;
        this.state = state;
    }
}

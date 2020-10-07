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
    private int locationId;

    @NonNull
    @ColumnInfo(name = "county")
    private String county;

    @NonNull
    @ColumnInfo(name = "state")
    private String state;

    // CONSTRUCTOR

    public Location() {
        county = "placeholder_county";
        state = "placeholder_state";
    }

    // ACCESSORS & MUTATORS

    public int getLocationId() { return locationId; }
    // no setter for auto-incremented Primary Keys

    public String getCounty() { return county; }
    public void setCounty(String county) { this.county = county; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}

package com.nsc.covidscore;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "location")
class Location {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer id;
    public Integer getId() { return id; }

    @NonNull
    @ColumnInfo(name = "county")
    private String county;
    public String getCounty() { return county; }

    @NonNull
    @ColumnInfo(name = "state")
    private String state;
    public String getState() { return state; }

    // CONSTRUCTOR

    public Location() {
        county = "placeholder_county";
        state = "placeholder_state";
    }

    public Location(Integer id, String county, String state) {
        this.id = id;
        this.county = county;
        this.state = state;
    }
}

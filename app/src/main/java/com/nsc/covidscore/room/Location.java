package com.nsc.covidscore.room;

import androidx.room.Ignore;

import com.nsc.covidscore.Constants;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Calendar;

public class Location implements Serializable {

    private Integer locationId;
    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    private String county;
    public String getCounty() { return county; }
    public void setCounty(String county) {
        this.county = county;
    }

    private String state;
    public String getState() { return state; }
    public void setState(String state) {
        this.state = state;
    }

    private String countyFips;
    public String getCountyFips() { return countyFips; }
    public void setCountyFips(String countyFips) {
        this.countyFips = countyFips;
    }

    private String stateFips;
    public String getStateFips() { return stateFips; }
    public void setStateFips(String stateFips) {
        this.stateFips = stateFips;
    }

    // CONSTRUCTOR

    public Location() {}

    public Location(Integer locationId, String county, String state, String countyFips, String stateFips) {
        this.locationId = locationId;
        this.county = county;
        this.state = state;
        this.countyFips = countyFips;
        this.stateFips = stateFips;
    }

    public Location(String county, String state, String countyFips, String stateFips) {
        this.county = county;
        this.state = state;
        this.countyFips = countyFips;
        this.stateFips = stateFips;
    }

    @Ignore
    public Location(String county, String state) {
        this.county = county;
        this.state = state;
    }

    public void setAllState(Location other) {
        this.locationId = other.locationId;
        this.setCounty(other.county);
        this.setState(other.state);
        this.countyFips = other.countyFips;
        this.stateFips = other.stateFips;
    }

    // FORMATTING

    public String toApiFormat() {
        return county.toLowerCase() + "," + state.toLowerCase();
    }

    public String toDrawerItemTitleFormat() {
        return county + ", " + state;
    }

    @Override
    public String toString() {
        return "Location{" +
                "locationId=" + locationId +
                ", county='" + county + '\'' +
                ", state='" + state + '\'' +
                ", countyFips='" + countyFips + '\'' +
                ", stateFips='" + stateFips + '\'' +
                '}';
    }

    // VALIDATION

    public boolean fipsSet() {
        return this.countyFips != null && this.stateFips != null;
    }

    public boolean fipsNotEmpty() {
        return !this.countyFips.isEmpty() && !this.stateFips.isEmpty();
    }

    public boolean locationNamesSet() {
        return this.county != null && this.state != null;
    }

    public boolean locationNamesNotEmpty() {
        return !this.county.isEmpty() && !this.state.isEmpty();
    }

    public boolean locationNotNull() {
        return this.locationNamesSet() && this.fipsSet();
    }

    public boolean locationNotEmpty() {
        return this.locationNamesNotEmpty() && this.fipsNotEmpty();
    }

    public boolean hasFieldsSet() {
        return this.locationNotNull() && this.locationNotEmpty();
    }

    // COMPARISON

    public boolean hasSameLocationId(Location other) {
        return this.locationId.equals(other.locationId);
    }

    public boolean hasSameData(Location other) {
        return this.state.toLowerCase().equals(other.state.toLowerCase()) &&
                this.county.toLowerCase().equals(other.county.toLowerCase());
    }

    public boolean equals(Location other) {
        return this.hasSameData(other) && this.hasSameLocationId(other);
    }
}

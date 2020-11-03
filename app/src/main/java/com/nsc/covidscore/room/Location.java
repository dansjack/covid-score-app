package com.nsc.covidscore.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.nsc.covidscore.Constants;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public class Location implements Serializable {

    private Integer locationId;
    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) {
        propertyChangeSupport.firePropertyChange(Constants.LOCATION_ID_PK, this.locationId, locationId);
        this.locationId = locationId;
    }

    private String county;
    public String getCounty() { return county; }
    public void setCounty(String county) {
        propertyChangeSupport.firePropertyChange(Constants.COUNTY, this.county, county.toLowerCase());
        this.county = county.toLowerCase();
    }

    private String state;
    public String getState() { return state; }
    public void setState(String state) {
        propertyChangeSupport.firePropertyChange(Constants.STATE, this.state, state.toLowerCase());
        this.state = state.toLowerCase();
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

    private Calendar lastUpdated;
    public Calendar getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Calendar lastUpdated) {
        propertyChangeSupport.firePropertyChange(Constants.LAST_UPDATED_LOCATION, this.lastUpdated, lastUpdated);
        this.lastUpdated = lastUpdated;
    }

    // For Observer

    protected PropertyChangeSupport propertyChangeSupport;

//    public void setListener(PropertyChangeListener listener) {
//        propertyChangeSupport.addPropertyChangeListener(listener);
//    }
//
//    public void removeListener(PropertyChangeListener listener) {
//        propertyChangeSupport.removePropertyChangeListener(listener);
//    }

    // CONSTRUCTOR

    public Location() {}

    public Location(Integer locationId, String county, String state, String countyFips, String stateFips) {
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.locationId = locationId;
        this.county = county.toLowerCase();
        this.state = state.toLowerCase();
        this.countyFips = countyFips;
        this.stateFips = stateFips;
        Calendar calendar = Calendar.getInstance();
        this.lastUpdated = calendar;
    }

    public Location(String county, String state, String countyFips, String stateFips) {
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.county = county.toLowerCase();
        this.state = state.toLowerCase();
        this.countyFips = countyFips;
        this.stateFips = stateFips;
        Calendar calendar = Calendar.getInstance();
        this.lastUpdated = calendar;
    }

    @Ignore
    public Location(String county, String state) {
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.county = county.toLowerCase();
        this.state = state.toLowerCase();
    }

    public void setAllState(Location other) {
        this.locationId = other.locationId;
        this.setCounty(other.county);
        this.setState(other.state);
        this.countyFips = other.countyFips;
        this.stateFips = other.stateFips;
    }

    public String toApiFormat() {
        return county + "," + state;
    }

    public boolean hasFieldsSet() {
        boolean notNull = (this.county != null && this.state != null) && (this.countyFips != null && this.stateFips != null);
        boolean notEmpty = (!this.county.isEmpty() && !this.state.isEmpty()) && (!this.countyFips.isEmpty() && !this.stateFips.isEmpty());
        return notNull && notEmpty;
    }

    public boolean equals(Location other) {
        return this.hasSameData(other) && this.locationId.equals(other.locationId);
    }

    public boolean hasSameData(Location other) {
        return this.state.equals(other.state) && this.county.equals(other.county);
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
}

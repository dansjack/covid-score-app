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
        propertyChangeSupport.firePropertyChange(Constants.LOCATION_ID_PK, this.locationId, locationId);
        this.locationId = locationId;
    }

    private String county;
    public String getCounty() { return county; }
    public void setCounty(String county) {
        propertyChangeSupport.firePropertyChange(Constants.COUNTY, this.county, county);
        this.county = county;
    }

    private String state;
    public String getState() { return state; }
    public void setState(String state) {
        propertyChangeSupport.firePropertyChange(Constants.STATE, this.state, state);
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

    private Calendar lastUpdated;
    public Calendar getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Calendar lastUpdated) {
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

    public Location() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public Location(Integer locationId, String county, String state, String countyFips, String stateFips) {
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.locationId = locationId;
        this.county = county;
        this.state = state;
        this.countyFips = countyFips;
        this.stateFips = stateFips;
        Calendar calendar = Calendar.getInstance();
        this.lastUpdated = calendar;
    }

    public Location(String county, String state, String countyFips, String stateFips) {
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.county = county;
        this.state = state;
        this.countyFips = countyFips;
        this.stateFips = stateFips;
        Calendar calendar = Calendar.getInstance();
        this.lastUpdated = calendar;
    }

    @Ignore
    public Location(String county, String state) {
        propertyChangeSupport = new PropertyChangeSupport(this);
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

    public String toApiFormat() {
        return county.toLowerCase() + "," + state.toLowerCase();
    }

    public String toDrawerItemTitleFormat() {
        return county + ", " + state;
    }

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

    public boolean hasFieldsSet() {
        boolean notNull = locationNamesSet() && fipsSet();
        boolean notEmpty = locationNamesNotEmpty() && fipsNotEmpty();
        return notNull && notEmpty;
    }

    public boolean equals(Location other) {
        return this.hasSameData(other) && this.locationId.equals(other.locationId);
    }

    public boolean hasSameData(Location other) {
        return this.state.toLowerCase().equals(other.state.toLowerCase()) &&
                this.county.toLowerCase().equals(other.county.toLowerCase());
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

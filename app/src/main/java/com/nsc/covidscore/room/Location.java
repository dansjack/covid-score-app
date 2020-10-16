package com.nsc.covidscore.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.nsc.covidscore.Constants;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;

@Entity(tableName = "location")
public class Location {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "location_id")
    private Integer locationId;
    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) {
        propertyChangeSupport.firePropertyChange(Constants.LOCATION_ID_PK, this.locationId, locationId);
        this.locationId = locationId;
    }

    @NonNull
    @ColumnInfo(name = "county")
    private String county;
    public String getCounty() { return county; }
    public void setCounty(String county) {
        propertyChangeSupport.firePropertyChange(Constants.COUNTY, this.county, county.toLowerCase());
        this.county = county.toLowerCase();
    }

    @NonNull
    @ColumnInfo(name = "state")
    private String state;
    public String getState() { return state; }
    public void setState(String state) {
        propertyChangeSupport.firePropertyChange(Constants.STATE, this.state, state.toLowerCase());
        this.state = state.toLowerCase();
    }

    @ColumnInfo(name = "last_updated")
    private Calendar lastUpdated;
    public Calendar getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Calendar lastUpdated) {
//        propertyChangeSupport.firePropertyChange(Constants.LAST_UPDATED_LOCATION, this.lastUpdated, lastUpdated);
        this.lastUpdated = lastUpdated;
    }

    // For Observer

    @Ignore
    protected PropertyChangeSupport propertyChangeSupport;

    public void setListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    // CONSTRUCTOR

    public Location(String county, String state) {
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.county = county.toLowerCase();
        this.state = state.toLowerCase();
        Calendar calendar = Calendar.getInstance();
        this.lastUpdated = calendar;
    }

    public String toApiFormat() { return county + "," + state; }

    public boolean hasFieldsSet() { return this.county != null && this.state != null; }

    public boolean equals(Location other) {
        return !this.state.equals(other.state) && !this.county.equals(other.county);
    }

}

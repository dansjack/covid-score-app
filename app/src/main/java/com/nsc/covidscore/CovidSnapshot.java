package com.nsc.covidscore;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Observable;

@Entity(tableName = "covid_snapshot")
public class CovidSnapshot extends Observable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "covid_snapshot_id")
    private Integer covidSnapshotId;
    public Integer getCovidSnapshotId() { return covidSnapshotId; }
    public void setCovidSnapshotId(Integer covidSnapshotId) { this.covidSnapshotId = covidSnapshotId; }

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

    @ColumnInfo(name = "last_updated")
    private Calendar lastUpdated;
    public Calendar getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Calendar lastUpdated) { this.lastUpdated = lastUpdated; }

    // For Observer

    @Ignore
    protected PropertyChangeSupport propertyChangeSupport;

    public void setListener(PropertyChangeListener newListener) {
        propertyChangeSupport.addPropertyChangeListener(newListener);
    }

    // CONSTRUCTOR

    public CovidSnapshot() {
    }

    public CovidSnapshot(Integer locationId,
                         Integer countyActiveCount,
                         Integer stateActiveCount,
                         Integer countryActiveCount,
                         Calendar lastUpdated) {
        this.locationId = locationId;
        this.countyActiveCount = countyActiveCount;
        this.stateActiveCount = stateActiveCount;
        this.countryActiveCount = countryActiveCount;
        this.lastUpdated = lastUpdated;
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    @Override
    public String toString() {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        return "Snapshot ID: " + covidSnapshotId + " Location ID: " + locationId + " Active County: " + countyActiveCount
                + " Active State: " + stateActiveCount + " Active Country: " + countryActiveCount
                + " Last Updated: " + (lastUpdated != null ? date_format.format(lastUpdated.getTime()) : "null");
    }

    public boolean hasFieldsSet() {
        return ((locationId != null && countyActiveCount != null) && (stateActiveCount != null && countryActiveCount != null)) && lastUpdated != null;
    }
}

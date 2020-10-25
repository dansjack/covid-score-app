package com.nsc.covidscore.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.nsc.covidscore.Constants;

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
    public void setCovidSnapshotId(Integer covidSnapshotId) {
        propertyChangeSupport.firePropertyChange(Constants.COVID_SNAPSHOT_ID, this.covidSnapshotId, covidSnapshotId);
        this.covidSnapshotId = covidSnapshotId;
    }

    @NonNull
    @ColumnInfo(name = "location_id")
    private Integer locationId;
    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) {
        propertyChangeSupport.firePropertyChange(Constants.LOCATION_ID_FK, this.locationId, locationId);
        this.locationId = locationId;
    }

    @ColumnInfo(name = "county_active_count")
    private Integer countyActiveCount;
    public Integer getCountyActiveCount() { return countyActiveCount; }
    public void setCountyActiveCount(Integer countyActiveCount) {
        propertyChangeSupport.firePropertyChange(Constants.ACTIVE_COUNTY, this.countyActiveCount, countyActiveCount);
        this.countyActiveCount = countyActiveCount;
    }

    @ColumnInfo(name = "county_total_population")
    private Integer countyTotalPopulation;
    public Integer getCountyTotalPopulation() { return countyTotalPopulation; }
    public void setCountyTotalPopulation(Integer countyTotalPopulation) {
        propertyChangeSupport.firePropertyChange(Constants.TOTAL_COUNTY, this.countyTotalPopulation, countyTotalPopulation);
        this.countyTotalPopulation = countyTotalPopulation;
    }

    @ColumnInfo(name = "state_active_count")
    private Integer stateActiveCount;
    public Integer getStateActiveCount() { return stateActiveCount; }
    public void setStateActiveCount(Integer stateActiveCount) {
        propertyChangeSupport.firePropertyChange(Constants.ACTIVE_STATE, this.stateActiveCount, stateActiveCount);
        this.stateActiveCount = stateActiveCount;
    }

    @ColumnInfo(name = "state_total_population")
    private Integer stateTotalPopulation;
    public Integer getStateTotalPopulation() { return stateTotalPopulation; }
    public void setStateTotalPopulation(Integer stateTotalPopulation) {
        propertyChangeSupport.firePropertyChange(Constants.TOTAL_STATE, this.stateTotalPopulation, stateTotalPopulation);
        this.stateTotalPopulation = stateTotalPopulation;
    }

    @ColumnInfo(name = "country_active_count")
    private Integer countryActiveCount;
    public Integer getCountryActiveCount() { return countryActiveCount; }
    public void setCountryActiveCount(Integer countryActiveCount) {
        propertyChangeSupport.firePropertyChange(Constants.ACTIVE_COUNTRY, this.countryActiveCount, countryActiveCount);
        this.countryActiveCount = countryActiveCount;
    }

    @ColumnInfo(name = "country_total_population")
    private Integer countryTotalPopulation;
    public Integer getCountryTotalPopulation() { return countryTotalPopulation; }
    public void setCountryTotalPopulation(Integer countryTotalPopulation) {
        propertyChangeSupport.firePropertyChange(Constants.TOTAL_COUNTRY, this.countryTotalPopulation, countryTotalPopulation);
        this.countryTotalPopulation = countryTotalPopulation;
    }

    @ColumnInfo(name = "last_updated")
    private Calendar lastUpdated;
    public Calendar getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Calendar lastUpdated) {
        this.lastUpdated = lastUpdated; }

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

    public CovidSnapshot() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.lastUpdated = Calendar.getInstance();
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
                + " Total County: " + countyTotalPopulation + " Active State: " + stateActiveCount
                + " Total State: " + stateTotalPopulation + " Active Country: " + countryActiveCount
                + " Total Country: " + countryTotalPopulation
                + " Last Updated: " + (lastUpdated != null ? date_format.format(lastUpdated.getTime()) : "null");
    }

    public boolean hasFieldsSet() {
        boolean countsNotNull = countyActiveCount != null && (stateActiveCount != null && countryActiveCount != null);
        boolean populationsNotNull = countyTotalPopulation != null && (stateTotalPopulation != null && countryTotalPopulation != null);
        // TODO: change this if the pandemic ends :)
        boolean countryNotZero = countryActiveCount != null && countryActiveCount != 0;
        return (countsNotNull && populationsNotNull) && (countryNotZero && locationId != null);
    }

    public boolean equals(CovidSnapshot other) {
        if (this.covidSnapshotId == null) { return false; }
        boolean idsEqual = (this.covidSnapshotId.equals(other.covidSnapshotId)) && (this.locationId.equals(other.locationId));
        return idsEqual && this.hasSameData(other);
    }

    public boolean hasSameData(CovidSnapshot other) {
        if (this.locationId == null || other.locationId == null) { return false; }
        boolean locationsMatch = this.locationId.equals(other.locationId);
        boolean countsMatch = ((this.countyActiveCount.equals(other.countyActiveCount)) && (this.stateActiveCount.equals(other.stateActiveCount))) && (this.countryActiveCount.equals(other.countryActiveCount));
        boolean populationsMatch = ((this.countyTotalPopulation.equals(other.countyTotalPopulation)) && this.stateTotalPopulation.equals(other.stateTotalPopulation)) && this.countryTotalPopulation.equals(other.countryTotalPopulation);

        return locationsMatch && populationsMatch && countsMatch;
    }
}

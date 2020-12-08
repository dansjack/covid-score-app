package com.nsc.covidscore.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.nsc.covidscore.Constants;

import java.beans.PropertyChangeSupport;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Observable;

@Entity(tableName = "covid_snapshot")
public class CovidSnapshot {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "covid_snapshot_id")
    private Integer covidSnapshotId;
    public Integer getCovidSnapshotId() { return covidSnapshotId; }
    public void setCovidSnapshotId(Integer covidSnapshotId) {
        this.covidSnapshotId = covidSnapshotId;
    }

    @NonNull
    @ColumnInfo(name = "location_id")
    private Integer locationId;
    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    @ColumnInfo(name = "county_active_count")
    private Integer countyActiveCount;
    public Integer getCountyActiveCount() { return countyActiveCount; }
    public void setCountyActiveCount(Integer countyActiveCount) {
        this.countyActiveCount = countyActiveCount;
    }

    @ColumnInfo(name = "county_total_population")
    private Integer countyTotalPopulation;
    public Integer getCountyTotalPopulation() { return countyTotalPopulation; }
    public void setCountyTotalPopulation(Integer countyTotalPopulation) {
        this.countyTotalPopulation = countyTotalPopulation;
    }

    @ColumnInfo(name = "state_active_count")
    private Integer stateActiveCount;
    public Integer getStateActiveCount() { return stateActiveCount; }
    public void setStateActiveCount(Integer stateActiveCount) {
        this.stateActiveCount = stateActiveCount;
    }

    @ColumnInfo(name = "state_total_population")
    private Integer stateTotalPopulation;
    public Integer getStateTotalPopulation() { return stateTotalPopulation; }
    public void setStateTotalPopulation(Integer stateTotalPopulation) {
        this.stateTotalPopulation = stateTotalPopulation;
    }

    @ColumnInfo(name = "country_active_count")
    private Integer countryActiveCount;
    public Integer getCountryActiveCount() { return countryActiveCount; }
    public void setCountryActiveCount(Integer countryActiveCount) {
        this.countryActiveCount = countryActiveCount;
    }

    @ColumnInfo(name = "country_total_population")
    private Integer countryTotalPopulation;
    public Integer getCountryTotalPopulation() { return countryTotalPopulation; }
    public void setCountryTotalPopulation(Integer countryTotalPopulation) {
        this.countryTotalPopulation = countryTotalPopulation;
    }

    @ColumnInfo(name = "last_updated")
    private Calendar lastUpdated;
    public Calendar getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Calendar lastUpdated) {
        this.lastUpdated = lastUpdated; }

    // CONSTRUCTOR

    public CovidSnapshot() {
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
    }

    // FORMATTING

    @Override
    public String toString() {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
        return "Snapshot ID: " + covidSnapshotId + " Location ID: " + locationId + " Active County: " + countyActiveCount
                + " Total County: " + countyTotalPopulation + " Active State: " + stateActiveCount
                + " Total State: " + stateTotalPopulation + " Active Country: " + countryActiveCount
                + " Total Country: " + countryTotalPopulation
                + " Last Updated: " + (lastUpdated != null ? date_format.format(lastUpdated.getTime()) : "null");
    }

    // VALIDATION

    public boolean countsNotNull() {
        return countyActiveCount != null && stateActiveCount != null && countryActiveCount != null;
    }

    public boolean populationsNotNull() {
        return countyTotalPopulation != null && stateTotalPopulation != null && countryTotalPopulation != null;
    }

    public boolean fieldsNotNull() {
        return this.countsNotNull() && this.populationsNotNull();
    }

    /**
     * Checks if this CovidSnapshot is ready for entry into Room (can have null covidSnapshotId)
     * @return true if all fields other than covidSnapshotId are not null, false otherwise
     */
    public boolean hasFieldsSet() {
        // TODO: change this if the pandemic ends :)
        return this.fieldsNotNull() && locationId != null;
    }

    // COMPARISON

    public boolean hasSameLocation(CovidSnapshot other) {
        return this.locationId.equals(other.locationId);
    }

    public boolean idsEqual(CovidSnapshot other) {
        return this.covidSnapshotId.equals(other.covidSnapshotId) && hasSameLocation(other);
    }

    public boolean hasSameCounts(CovidSnapshot other) {
        return this.countyActiveCount.equals(other.countyActiveCount) &&
                this.stateActiveCount.equals(other.stateActiveCount) &&
                this.countryActiveCount.equals(other.countryActiveCount);
    }

    public boolean hasSamePopulations(CovidSnapshot other) {
        return this.countyTotalPopulation.equals(other.countyTotalPopulation) &&
                this.stateTotalPopulation.equals(other.stateTotalPopulation) &&
                this.countryTotalPopulation.equals(other.countryTotalPopulation);
    }

    public boolean hasSameData(CovidSnapshot other) {
        if (this.locationId == null || other.locationId == null) { return false; }
        return this.hasSameLocation(other) && this.hasSamePopulations(other) && this.hasSameCounts(other);
    }

    public boolean equals(CovidSnapshot other) {
        if (this.covidSnapshotId == null) { return false; }
        return idsEqual(other) && this.hasSameData(other);
    }
}

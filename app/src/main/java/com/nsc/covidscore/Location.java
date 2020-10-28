package com.nsc.covidscore;

public class Location {
    private String state;
    private String county;
    private String stateFips;
    private String countyFips;

    public Location(String county, String state, String stateFips, String countyFips) {
        this.county = county;
        this.state = state;
        this.stateFips = stateFips;
        this.countyFips = countyFips;
    }

    public Location() {
        this.state = null;
        this.county = null;
        this.stateFips = null;
        this.countyFips = null;
    }

    public String getState() {
        return state;
    }

    public String getCounty() {
        return county;
    }

    public String toApiFormat() { return (county + "," + state).toLowerCase(); }


    public String getStateFips() {
        return stateFips;
    }

    public String getCountyFips() {
        return countyFips;
    }

    @Override
    public String toString() {
        return "Location{" +
                "state='" + state + '\'' +
                ", county='" + county + '\'' +
                ", stateFips='" + stateFips + '\'' +
                ", countyFips='" + countyFips + '\'' +
                '}';
    }
}

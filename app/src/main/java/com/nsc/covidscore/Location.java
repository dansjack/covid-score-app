package com.nsc.covidscore;

public class Location {
    private String state;
    private String county;
    private String stateFips;
    private String countyFips;

    public Location(String state, String county, String stateFips, String countyFips) {
        this.state = state;
        this.county = county;
        this.stateFips = stateFips;
        this.countyFips = countyFips;
    }

    public String getState() {
        return state;
    }

    public String getCounty() {
        return county;
    }

    public String toApiFormat() { return county + "," + state; }


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

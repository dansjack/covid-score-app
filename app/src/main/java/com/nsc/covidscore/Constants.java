package com.nsc.covidscore;

public class Constants {
    public static final String CENSUS_API_KEY = "284928f69049b0901d7c04abfacf724536890594";
    public static final String COUNTY = "county";
    public static final String COUNTY_HISTORICAL = "countyHistorical";
    public static final String COUNTY_POPULATION = "countyPopulation";
    public static final String COUNTRY = "country";
    public static final String COUNTRY_HISTORICAL = "countryHistorical";
    public static final String COUNTRY_POPULATION = "countryPopulation";
    public static final String DISTRICT_OF_COLUMBIA = "district of columbia";
    public static final String LOCATION_FIPS_COUNTY_STRING = " county, ";
    public static final String POPULATION = "population";
    public static final String PROVINCE = "province";
    public static final String STATE = "state";
    public static final String STATE_POPULATION = "statePopulation";

    public static final String ACTIVE_COUNTY = "activeCounty";
    public static final String ACTIVE_COUNTRY = "activeCountry";
    public static final String ACTIVE_STATE = "activeState";
    public static final String COVID_SNAPSHOT_ID = "covidSnapshotId";
    public static final String CURRENT_LOCATION = "currentLocation";
    public static final String ERROR_STATE_COUNTY = "The State/County combination couldn't be found";
    public static final String LAST_UPDATED_LOCATION = "lastUpdatedLocation";
    public static final String LAST_UPDATED_SNAPSHOT = "lastUpdatedSnapshot";
    public static final String LOCATION_ID_FK = "locationIdFK";
    public static final String LOCATION_ID_PK = "locationIdPK";
    public static final String LOCATIONS_MAP_BY_STATE = "allLocationsMapByState";
    public static final String LOCATIONS_MAP_BY_ID = "allLocationsMapById";
    public static final String RISK_MAP = "riskMap";
    public static final String TOTAL_COUNTY = "totalCounty";
    public static final String TOTAL_COUNTRY = "totalCountry";
    public static final String TOTAL_STATE = "totalState";

    public static final String DAYS_01 = "1";
    public static final String DAYS_30 = "30";

    public static final String RESPONSE_ACTIVE = "active";
    public static final String RESPONSE_CASES = "cases";
    public static final String RESPONSE_CONFIRMED = "confirmed";
    public static final String RESPONSE_DEATHS = "deaths";
    public static final String RESPONSE_RECOVERED = "recovered";
    public static final String RESPONSE_STATS = "stats";
    public static final String RESPONSE_TIMELINE = "timeline";


    public static final String FRAGMENT_LMSF = "locationManualSelectionFragment";
    public static final String FRAGMENT_RDPF = "riskDetailPageFragment";

    public static final String LOCATION_FILENAME = "county_fips.json";

    public static final String COMMA = ",";
    public static final String COMMA_SPACE = ", ";
    public static final String SELECT_COUNTY = "Select County";
    public static final String SELECT_STATE = "Select State";

    public static final int[] GROUP_SIZES = {10, 20, 50, 100, 200};
}

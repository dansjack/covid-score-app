package com.nsc.covidscore;

public class CovidSnapshot {

    private Integer locationId;
    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    private Integer countyActiveCount;
    public Integer getCountyActiveCount() { return countyActiveCount; }
    public void setCountyActiveCount(Integer countyActiveCount) {
        this.countyActiveCount = countyActiveCount;
    }

    private Integer countyTotalPopulation;
    public Integer getCountyTotalPopulation() { return countyTotalPopulation; }
    public void setCountyTotalPopulation(Integer countyTotalPopulation) {
        this.countyTotalPopulation = countyTotalPopulation;
    }

    private Integer stateActiveCount;
    public Integer getStateActiveCount() { return stateActiveCount; }
    public void setStateActiveCount(Integer stateActiveCount) {
        this.stateActiveCount = stateActiveCount;
    }

    private Integer stateTotalPopulation;
    public Integer getStateTotalPopulation() { return stateTotalPopulation; }
    public void setStateTotalPopulation(Integer stateTotalPopulation) {
        this.stateTotalPopulation = stateTotalPopulation;
    }

    private Integer countryActiveCount;
    public Integer getCountryActiveCount() { return countryActiveCount; }
    public void setCountryActiveCount(Integer countryActiveCount) {
        this.countryActiveCount = countryActiveCount;
    }

    private Integer countryTotalPopulation;
    public Integer getCountryTotalPopulation() { return countryTotalPopulation; }
    public void setCountryTotalPopulation(Integer countryTotalPopulation) {
        this.countryTotalPopulation = countryTotalPopulation;
    }

    // CONSTRUCTOR

    public CovidSnapshot() {}

    public CovidSnapshot(Integer locationId,
                         Integer countyActiveCount,
                         Integer stateActiveCount,
                         Integer countryActiveCount) {
        this.locationId = locationId;
        this.countyActiveCount = countyActiveCount;
        this.stateActiveCount = stateActiveCount;
        this.countryActiveCount = countryActiveCount;
    }

    @Override
    public String toString() {
        return "\nActive County: " + countyActiveCount
                + "\nTotal County: " + countyTotalPopulation + "\nActive State: " + stateActiveCount
                + "\nTotal State: " + stateTotalPopulation + "\nActive Country: " + countryActiveCount
                + "\nTotal Country: " + countryTotalPopulation;
    }

        public boolean hasFieldsSet() {
        boolean countsNotNull = countyActiveCount != null && (stateActiveCount != null && countryActiveCount != null);
        boolean populationsNotNull = countyTotalPopulation != null && (stateTotalPopulation != null && countryTotalPopulation != null);
        // TODO: change this if the pandemic ends :)
        boolean countryNotZero = countryActiveCount != null && countryActiveCount != 0;
        return countsNotNull && populationsNotNull && countryNotZero;
    }

        public boolean hasSameData(CovidSnapshot other) {
            boolean countsMatch = ((this.countyActiveCount.equals(other.countyActiveCount)) && (this.stateActiveCount.equals(other.stateActiveCount))) && (this.countryActiveCount.equals(other.countryActiveCount));
            boolean populationsMatch = ((this.countyTotalPopulation.equals(other.countyTotalPopulation)) && this.stateTotalPopulation.equals(other.stateTotalPopulation)) && this.countryTotalPopulation.equals(other.countryTotalPopulation);

            return countsMatch && populationsMatch;
        }
}

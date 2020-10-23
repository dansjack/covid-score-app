package com.nsc.covidscore;

import java.math.BigDecimal;
import java.math.MathContext;

public class RiskCalculation {

    public static double calculateRisk(int activeCases, int totalPopulation, int groupSize) {
        BigDecimal oddsOfInfection;
        try {
            oddsOfInfection = new BigDecimal(activeCases).divide(new BigDecimal(totalPopulation));
        } catch (ArithmeticException e) {
            oddsOfInfection = new BigDecimal(activeCases).divide(new BigDecimal(totalPopulation), new MathContext(10000));
        }

        BigDecimal oddsOfNoInfection = new BigDecimal(1).subtract(oddsOfInfection);
        BigDecimal oddsOfNoInfectionInGroup = oddsOfNoInfection.pow(groupSize);
        BigDecimal covidRisk = new BigDecimal(1).subtract(oddsOfNoInfectionInGroup);
        return covidRisk.movePointRight(2).round(new MathContext(4)).doubleValue();
    }

}

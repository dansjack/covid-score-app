package com.nsc.covidscore;

import java.math.BigDecimal;
import java.math.MathContext;

public class RiskCalculation {

    public static String printRiskCalculations(Integer activeCases, Integer totalPopulation, int[] groupSizes) {
        StringBuilder sb = new StringBuilder("");

        for (int groupSize : groupSizes) {
            String row = "For a group of size " + groupSize + ", there is a ";
            row += calculateRisk(activeCases, totalPopulation, groupSize);
            row += "% chance that you will be exposed to COVID.\n";

            sb.append(row);
        }

        return sb.toString();
    }

    public static double calculateRisk(int activeCases, int totalPopulation, int groupSize) {
        BigDecimal oddsOfInfection;
        try {
            oddsOfInfection = new BigDecimal(activeCases).divide(new BigDecimal(totalPopulation));
        } catch (ArithmeticException e) {
            oddsOfInfection = new BigDecimal(activeCases).divide(new BigDecimal(totalPopulation), new MathContext(10000));
        }

        BigDecimal oddsOfNoInfection = new BigDecimal(1).subtract(oddsOfInfection);
        int exponent = 0;

        double oddsOfNoInfectionInGroup = oddsOfNoInfection.doubleValue();
        while (exponent < groupSize && (oddsOfNoInfectionInGroup >= 0.00001 && oddsOfNoInfectionInGroup <= 0.9999)) {
            oddsOfNoInfectionInGroup *= oddsOfNoInfection.doubleValue();
            exponent ++;
        }

        BigDecimal covidRisk = new BigDecimal(1).subtract(BigDecimal.valueOf(oddsOfNoInfectionInGroup));
        return covidRisk.movePointRight(2).round(new MathContext(4)).doubleValue();
    }

}

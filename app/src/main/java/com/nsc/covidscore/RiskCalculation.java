package com.nsc.covidscore;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RiskCalculation {

    public static HashMap<Integer, Double> getRiskCalculationsMap(Integer activeCases, Integer totalPopulation, int[] groupSizes) {
        HashMap<Integer, Double> riskMap = new HashMap<>();

        for (int groupSize : groupSizes) {
            riskMap.put(groupSize, calculateRisk(activeCases, totalPopulation, groupSize));
        }

        return riskMap;
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

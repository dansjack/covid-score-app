package com.nsc.covidscore;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RiskCalculationTest {
    @Test
    public void testCalculation() {
        // King County 10/23/20
        assertEquals(RiskCalculation.calculateRisk(24914, 2252782, 20), 20.83, 0);
        // WA State 10/23/20
        assertEquals(RiskCalculation.calculateRisk(52948, 7614893, 20), 13.63, 0);
        // US 10/23/20
        assertEquals(RiskCalculation.calculateRisk(4831614, 328239523, 20), 26.76, 0);
    }

    @Test
    public void testCalculationMapping() {
        Map<Integer, Double> riskMap = RiskCalculation.getRiskCalculationsMap(TestUtils.activeCases, TestUtils.totalPopulation, TestUtils.groupSizes);
        TestUtils.assertRiskMapMatchesTestData(riskMap);
    }
}

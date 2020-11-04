package com.nsc.covidscore;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class TestUtils {
    public static int[] groupSizes = {10, 20, 50, 100};
    public static Integer activeCases = 20000;
    public static Integer totalPopulation = 500000;

    public static void assertRiskMapMatchesTestData(Map<Integer, Double> riskMap) {
        Set<Integer> groupSizesFromTest = riskMap.keySet();
        for (int size : groupSizes) {
            assertTrue(groupSizesFromTest.contains(size));
        }
        assertTrue(riskMap.get((Integer) groupSizes[0]) == 36.18);
        assertTrue(riskMap.get((Integer) groupSizes[1]) == 57.57);
        assertTrue(riskMap.get((Integer) groupSizes[2]) == 87.53);
        assertTrue(riskMap.get((Integer) groupSizes[3]) == 98.38);
    }
}

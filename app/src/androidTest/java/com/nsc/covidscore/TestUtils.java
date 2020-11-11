package com.nsc.covidscore;

import com.nsc.covidscore.room.CovidSnapshot;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class TestUtils {
    // variable declarations
    public static String testState = "washington";
    public static String testCounty = "king";
    public static Integer resourceId1 = 100;
    public static Integer activeCases = 20000;
    public static Integer totalPopulation = 500000;
    public static int[] groupSizes = {10, 20, 50, 100};

    // constructors

    public static CovidSnapshot createCovidSnapshot() {
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_YEAR, -2);
        return new CovidSnapshot(resourceId1, 200, 300, 400, calendar);
    }

    // assertions

    public static void assertCovidSnapshotMatchesData(CovidSnapshot covidSnapshot) {
        assertTrue(covidSnapshot.getCovidSnapshotId().equals(1));
        assertTrue(covidSnapshot.getLocationId().equals(resourceId1));
        assertTrue(covidSnapshot.getCountyActiveCount().equals(200));
        assertTrue(covidSnapshot.getStateActiveCount().equals(300));
        assertTrue(covidSnapshot.getCountryActiveCount().equals(400));
    }

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

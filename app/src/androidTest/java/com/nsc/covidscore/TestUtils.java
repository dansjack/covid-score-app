package com.nsc.covidscore;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUtils {

    public static String testState = "washington";
    public static String testCounty = "king";
    public static Integer resourceId1 = 100;

    public static Location createLocation() {
        return new Location(testCounty, testState);
    }

    public static void assertGetAllMatchesData(List<Location> allLocations) {
        assertEquals(allLocations.size(), 1);
        assertTrue(allLocations.get(0).getLocationId().equals(1));
        assertTrue(allLocations.get(0).getCounty().equals(testCounty));
        assertTrue(allLocations.get(0).getState().equals(testState));
    }
}

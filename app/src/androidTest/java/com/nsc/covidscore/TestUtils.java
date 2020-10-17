package com.nsc.covidscore;

import com.nsc.covidscore.room.CovidSnapshot;
import com.nsc.covidscore.room.Location;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUtils {
    // variable declarations
    public static String testState = "washington";
    public static String testCounty = "king";
    public static Integer resourceId1 = 100;

    // constructors
    public static Location createLocation() {
        Location location = new Location(testCounty, testState);
        //location.setLocationId(resourceId1);
        return location;
    }

    public static CovidSnapshot createCovidSnapshot() {
        Calendar calendar = Calendar.getInstance();
        return new CovidSnapshot(resourceId1, 200, 300, 400, calendar);
    }

    // assertions

    public static void assertGetAllLocationsMatchesData(List<Location> allLocations) {
        assertEquals(allLocations.size(), 1);
        assertTrue(allLocations.get(0).getLocationId().equals(resourceId1));
        assertTrue(allLocations.get(0).getCounty().equals(testCounty));
        assertTrue(allLocations.get(0).getState().equals(testState));
    }

    public static void assertLocationMatchesData(Location location) {
        assertTrue(location.getLocationId().equals(resourceId1));
        assertTrue(location.getCounty().equals(testCounty));
        assertTrue(location.getState().equals(testState));
    }

    public static void assertCovidSnapshotMatchesData(CovidSnapshot covidSnapshot) {
        assertTrue(covidSnapshot.getCovidSnapshotId().equals(resourceId1));
        assertTrue(covidSnapshot.getLocationId().equals(resourceId1));
        assertTrue(covidSnapshot.getCountyActiveCount().equals(200));
        assertTrue(covidSnapshot.getStateActiveCount().equals(300));
        assertTrue(covidSnapshot.getCountryActiveCount().equals(400));
    }
}

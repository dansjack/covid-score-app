package com.nsc.covidscore;

import com.nsc.covidscore.room.Location;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocationUnitTest {
    private static Location TEST_LOCATION1;
    private static Location TEST_LOCATION2;
    private static Location TEST_LOCATION3;

    @BeforeClass
    public static void beforeClass() {
        TEST_LOCATION1 = new Location("King", "Washington", "99", "98");
        TEST_LOCATION2 = new Location("Pierce", "Washington");
        TEST_LOCATION1.setLocationId(1);
        TEST_LOCATION3 = new Location("Imperial", "California");
        TEST_LOCATION3.setState("Arizona");
        TEST_LOCATION3.setCounty("Yuma");
        TEST_LOCATION3.setStateFips("00");
        TEST_LOCATION3.setCountyFips("11");
        TEST_LOCATION3.setLastUpdated(Calendar.getInstance());
    }


    @Test
    public void locationMethodsTest() {
        assertTrue(TEST_LOCATION1.hasFieldsSet());
        assertFalse(TEST_LOCATION1.equals(TEST_LOCATION2));
        assertFalse(TEST_LOCATION1.hasSameData(TEST_LOCATION2));

        TEST_LOCATION2.setAllState(TEST_LOCATION1);
    }
}

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
    private static Location TEST_LOCATION4;
    private static Location TEST_LOCATION5;
    private static Location TEST_LOCATION6;
    private static Location TEST_LOCATION7;
    private static Location TEST_LOCATION8;
    private static Location TEST_LOCATION9;
    private static Location TEST_LOCATION10;
    private static Location TEST_LOCATION11;
    private static Location TEST_LOCATION12;
    private static Location TEST_LOCATION13;
    private static Location TEST_LOCATION14;
    private static Location TEST_LOCATION15;
    private static Location TEST_LOCATION16;
    private static Location TEST_LOCATION17;

    private static Calendar calendar;

    @BeforeClass
    public static void beforeClass() {
        calendar = Calendar.getInstance();
        TEST_LOCATION1 = new Location("King", "Washington", "99", "98");
        TEST_LOCATION2 = new Location("Pierce", "Washington");
        TEST_LOCATION1.setLocationId(1);
        TEST_LOCATION3 = new Location("Imperial", "California");
        TEST_LOCATION3.setState("Arizona");
        TEST_LOCATION3.setCounty("Yuma");
        TEST_LOCATION3.setStateFips("00");
        TEST_LOCATION3.setCountyFips("11");
        TEST_LOCATION3.setLastUpdated(calendar);

        TEST_LOCATION4 = new Location("Pierce", "Washington");
        TEST_LOCATION4.setCountyFips("99");

        TEST_LOCATION5 = new Location("Pierce", "Washington");
        TEST_LOCATION5.setStateFips("99");

        TEST_LOCATION6 = new Location("", "", "", "");

        TEST_LOCATION7 = new Location("King", "", "8", "");

        TEST_LOCATION8 = new Location("", "Washington", "", "9");

        TEST_LOCATION9 = new Location();

        TEST_LOCATION10 = new Location(null, "Washington");

        TEST_LOCATION11 = new Location("King", null);

        TEST_LOCATION12 = new Location();
        TEST_LOCATION12.setCountyFips("01");
        TEST_LOCATION12.setStateFips("01");

        TEST_LOCATION13 = new Location("", "", "", "");
        TEST_LOCATION13.setLocationId(2);

        TEST_LOCATION14 = new Location("King", "Washington", "", "");

        TEST_LOCATION15 = new Location("", "", "01", "01");

        TEST_LOCATION16 = new Location("", "", "", "");
        TEST_LOCATION16.setLocationId(1);

        TEST_LOCATION17 = new Location("a", "b", "c", "d");
        TEST_LOCATION17.setLocationId(2);
    }

    @Test
    public void locationHasFieldsSetTest() {
        assertFalse(TEST_LOCATION8.hasFieldsSet());
        assertFalse(TEST_LOCATION10.hasFieldsSet());
        assertTrue(TEST_LOCATION3.hasFieldsSet());
    }

    @Test
    public void locationNotNullTest() {
        // FALSE FALSE
        assertFalse(TEST_LOCATION10.locationNotNull());
        // TRUE FALSE
        assertFalse(TEST_LOCATION5.locationNotNull());
        // FALSE TRUE
        assertFalse(TEST_LOCATION12.locationNotNull());
        // TRUE TRUE
        assertTrue(TEST_LOCATION3.locationNotNull());
    }

    @Test
    public void locationNotEmptyTest() {
        // FALSE FALSE
        assertFalse(TEST_LOCATION13.locationNotEmpty());
        // TRUE FALSE
        assertFalse(TEST_LOCATION14.locationNotEmpty());
        // FALSE TRUE
        assertFalse(TEST_LOCATION15.locationNotEmpty());
        // TRUE TRUE
        assertTrue(TEST_LOCATION1.locationNotEmpty());
    }

    @Test
    public void locationBoolMethodsTest() {
        assertTrue(TEST_LOCATION1.hasFieldsSet());
        assertTrue(TEST_LOCATION1.hasSameData(TEST_LOCATION1));
        assertFalse(TEST_LOCATION1.hasSameData(TEST_LOCATION2));

        TEST_LOCATION2.setAllState(TEST_LOCATION1);
    }

    @Test
    public void locationEqualsTest() {
        // TRUE TRUE
        assertTrue(TEST_LOCATION1.equals(TEST_LOCATION1));
        // TRUE FALSE
        assertFalse(TEST_LOCATION13.equals(TEST_LOCATION16));
        // FALSE TRUE
        assertFalse(TEST_LOCATION13.equals(TEST_LOCATION17));
        // FALSE FALSE
        assertFalse(TEST_LOCATION1.equals(TEST_LOCATION17));
    }

    @Test
    public void locationGetMethodsTest() {
        assertEquals("11", TEST_LOCATION3.getCountyFips());
        assertEquals("00", TEST_LOCATION3.getStateFips());
        assertEquals(calendar, TEST_LOCATION3.getLastUpdated());
    }

    @Test
    public void locationFipsSetTest() {
        assertTrue(TEST_LOCATION1.fipsSet());
        assertFalse(TEST_LOCATION9.fipsSet());
        assertFalse(TEST_LOCATION4.fipsSet());
        assertFalse(TEST_LOCATION5.fipsSet());
    }

    @Test
    public void locationFipsNotEmptyTest() {
        assertTrue(TEST_LOCATION1.fipsNotEmpty());
        assertFalse(TEST_LOCATION6.fipsNotEmpty());
        assertFalse(TEST_LOCATION7.fipsNotEmpty());
        assertFalse(TEST_LOCATION8.fipsNotEmpty());
    }

    @Test
    public void locationNamesSetTest() {
        assertTrue(TEST_LOCATION1.locationNamesSet());
        assertFalse(TEST_LOCATION9.locationNamesSet());
        assertFalse(TEST_LOCATION10.locationNamesSet());
        assertFalse(TEST_LOCATION11.locationNamesSet());
    }

    @Test
    public void locationNamesNotEmpty() {
        assertTrue(TEST_LOCATION1.locationNamesNotEmpty());
        assertFalse(TEST_LOCATION6.locationNamesNotEmpty());
        assertFalse(TEST_LOCATION7.locationNamesNotEmpty());
        assertFalse(TEST_LOCATION8.locationNamesNotEmpty());
    }
}

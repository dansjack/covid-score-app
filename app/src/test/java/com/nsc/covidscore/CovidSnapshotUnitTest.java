package com.nsc.covidscore;

import com.nsc.covidscore.room.CovidSnapshot;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CovidSnapshotUnitTest {
    private static CovidSnapshot TEST_SNAPSHOT1;
    private static CovidSnapshot TEST_SNAPSHOT2;
    private static CovidSnapshot TEST_SNAPSHOT3;
    private static CovidSnapshot TEST_SNAPSHOT4;
    private static CovidSnapshot TEST_SNAPSHOT5;
    private static CovidSnapshot TEST_SNAPSHOT6;
    private static CovidSnapshot TEST_SNAPSHOT7;
    private static CovidSnapshot TEST_SNAPSHOT8;
    private static CovidSnapshot TEST_SNAPSHOT9;
    private static CovidSnapshot TEST_SNAPSHOT10;
    private static CovidSnapshot TEST_SNAPSHOT11;
    private static CovidSnapshot TEST_SNAPSHOT12;
    private static CovidSnapshot TEST_SNAPSHOT13;
    private static CovidSnapshot TEST_SNAPSHOT14;
    private static CovidSnapshot TEST_SNAPSHOT15;
    private static CovidSnapshot TEST_SNAPSHOT16;
    private static CovidSnapshot TEST_SNAPSHOT17;
    private static CovidSnapshot TEST_SNAPSHOT18;

    @BeforeClass
    public static void beforeClass() {
        Calendar calendar = Calendar.getInstance();
        TEST_SNAPSHOT1 = new CovidSnapshot(999, 0, 0, 0, calendar);
        TEST_SNAPSHOT1.setCovidSnapshotId(1);
        TEST_SNAPSHOT1.setCountyTotalPopulation(0);
        TEST_SNAPSHOT1.setStateTotalPopulation(0);
        TEST_SNAPSHOT1.setCountryTotalPopulation(0);

        TEST_SNAPSHOT2 = new CovidSnapshot(2, 1, 2, 3, calendar);
        TEST_SNAPSHOT2.setCovidSnapshotId(2);
        TEST_SNAPSHOT2.setCountyTotalPopulation(2);
        TEST_SNAPSHOT2.setStateTotalPopulation(2);
        TEST_SNAPSHOT2.setCountryTotalPopulation(2);
        TEST_SNAPSHOT2.setCountyTotalPopulation(3);
        TEST_SNAPSHOT2.setStateTotalPopulation(3);
        TEST_SNAPSHOT2.setCountryTotalPopulation(3);

        TEST_SNAPSHOT3 = new CovidSnapshot(3, 2, 3, 4, calendar);
        TEST_SNAPSHOT3.setCovidSnapshotId(3);

        TEST_SNAPSHOT4 = new CovidSnapshot();

        TEST_SNAPSHOT5 = new CovidSnapshot();
        TEST_SNAPSHOT5.setStateActiveCount(1);
        TEST_SNAPSHOT5.setCountryActiveCount(1);
        TEST_SNAPSHOT5.setStateTotalPopulation(1);
        TEST_SNAPSHOT5.setCountryTotalPopulation(1);

        TEST_SNAPSHOT6 = new CovidSnapshot();
        TEST_SNAPSHOT6.setCountyActiveCount(0);
        TEST_SNAPSHOT6.setCountryActiveCount(1);
        TEST_SNAPSHOT6.setCountyTotalPopulation(1);
        TEST_SNAPSHOT6.setCountryTotalPopulation(1);

        TEST_SNAPSHOT7 = new CovidSnapshot();
        TEST_SNAPSHOT7.setCountyActiveCount(1);
        TEST_SNAPSHOT7.setStateActiveCount(1);
        TEST_SNAPSHOT7.setCountyTotalPopulation(1);
        TEST_SNAPSHOT7.setStateTotalPopulation(1);

        TEST_SNAPSHOT8 = new CovidSnapshot();
        TEST_SNAPSHOT8.setCountyActiveCount(1);
        TEST_SNAPSHOT8.setCountyTotalPopulation(1);

        TEST_SNAPSHOT9 = new CovidSnapshot();
        TEST_SNAPSHOT9.setStateActiveCount(1);
        TEST_SNAPSHOT9.setStateTotalPopulation(1);

        TEST_SNAPSHOT10 = new CovidSnapshot();
        TEST_SNAPSHOT10.setCountyActiveCount(1);
        TEST_SNAPSHOT10.setCountryTotalPopulation(1);

        TEST_SNAPSHOT11 = TEST_SNAPSHOT2;

        TEST_SNAPSHOT12 = new CovidSnapshot();
        TEST_SNAPSHOT12.setCovidSnapshotId(999);
        TEST_SNAPSHOT12.setLocationId(2);

        TEST_SNAPSHOT13 = new CovidSnapshot();
        TEST_SNAPSHOT13.setLocationId(999);
        TEST_SNAPSHOT13.setCovidSnapshotId(2);

        TEST_SNAPSHOT14 = new CovidSnapshot(20, 1, 2, 3, calendar);

        TEST_SNAPSHOT15 = new CovidSnapshot(2, 1, 2, 3, calendar);
        TEST_SNAPSHOT15.setCovidSnapshotId(222);

        TEST_SNAPSHOT16 = new CovidSnapshot(288, 1, 2, 3, calendar);
        TEST_SNAPSHOT16.setCovidSnapshotId(222);

        TEST_SNAPSHOT17 = new CovidSnapshot();

        TEST_SNAPSHOT18 = new CovidSnapshot();
        TEST_SNAPSHOT18.setCountyTotalPopulation(1);
        TEST_SNAPSHOT18.setStateTotalPopulation(1);
        TEST_SNAPSHOT18.setCountryTotalPopulation(1);


    }

    @Test
    public void snapshotEqualsTest() {
        assertTrue(TEST_SNAPSHOT2.equals(TEST_SNAPSHOT2));
        assertFalse(TEST_SNAPSHOT2.equals(TEST_SNAPSHOT14));
        assertFalse(TEST_SNAPSHOT2.equals(TEST_SNAPSHOT15));
        assertFalse(TEST_SNAPSHOT2.equals(TEST_SNAPSHOT16));
        assertFalse(TEST_SNAPSHOT17.equals(TEST_SNAPSHOT2));
    }

    @Test
    public void snapshotMethodsTest() {
        assertFalse(TEST_SNAPSHOT1.equals(TEST_SNAPSHOT2));
        assertFalse(TEST_SNAPSHOT1.hasSameData(TEST_SNAPSHOT2));
        assertFalse(TEST_SNAPSHOT1.hasFieldsSet());
    }

    @Test
    public void snapshotFieldsNotNullTest() {
        // T T
        assertTrue(TEST_SNAPSHOT1.fieldsNotNull());
        // T F
        assertFalse(TEST_SNAPSHOT3.fieldsNotNull());
        // F T
        assertFalse(TEST_SNAPSHOT18.fieldsNotNull());
        // F F
        assertFalse(TEST_SNAPSHOT4.fieldsNotNull());
    }

    @Test
    public void snapshotHasFieldsSetTest() {
        //  T T T
        assertTrue(TEST_SNAPSHOT2.hasFieldsSet());
        //  T T F
        //  T F T
        //  T F F
        //  F T T
        //  F T F
        //  F F T
        //  F F F

    }

    @Test
    public void snapshotCountsNotNullTest() {
        assertTrue(TEST_SNAPSHOT1.countsNotNull());
        assertFalse(TEST_SNAPSHOT4.countsNotNull());
        assertFalse(TEST_SNAPSHOT5.countsNotNull());
        assertFalse(TEST_SNAPSHOT6.countsNotNull());
        assertFalse(TEST_SNAPSHOT7.countsNotNull());
        assertFalse(TEST_SNAPSHOT8.countsNotNull());
        assertFalse(TEST_SNAPSHOT9.countsNotNull());
        assertFalse(TEST_SNAPSHOT10.countsNotNull());
    }

    @Test
    public void snapshotPopulationsNotNull() {
        assertTrue(TEST_SNAPSHOT1.populationsNotNull());
        assertFalse(TEST_SNAPSHOT4.populationsNotNull());
        assertFalse(TEST_SNAPSHOT5.populationsNotNull());
        assertFalse(TEST_SNAPSHOT6.populationsNotNull());
        assertFalse(TEST_SNAPSHOT7.populationsNotNull());
        assertFalse(TEST_SNAPSHOT8.populationsNotNull());
        assertFalse(TEST_SNAPSHOT9.populationsNotNull());
        assertFalse(TEST_SNAPSHOT10.populationsNotNull());
    }

    @Test
    public void snapshotCountryNotZero() {
        assertTrue(TEST_SNAPSHOT1.countsNotNull());
        assertFalse(TEST_SNAPSHOT4.countsNotNull());
        assertFalse(TEST_SNAPSHOT6.countsNotNull());
    }

    @Test
    public void snapshotIdsEqualTest() {
        assertTrue(TEST_SNAPSHOT2.idsEqual(TEST_SNAPSHOT11));
        assertFalse(TEST_SNAPSHOT2.idsEqual(TEST_SNAPSHOT12));
        assertFalse(TEST_SNAPSHOT2.idsEqual(TEST_SNAPSHOT13));
        assertFalse(TEST_SNAPSHOT2.idsEqual(TEST_SNAPSHOT17));
    }
}

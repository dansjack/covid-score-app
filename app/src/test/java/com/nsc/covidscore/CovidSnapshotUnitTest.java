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
    private static CovidSnapshot TEST_SNAPSHOT19;
    private static CovidSnapshot TEST_SNAPSHOT20;
    private static CovidSnapshot TEST_SNAPSHOT21;
    private static CovidSnapshot TEST_SNAPSHOT22;
    private static CovidSnapshot TEST_SNAPSHOT23;
    private static CovidSnapshot TEST_SNAPSHOT24;
    private static CovidSnapshot TEST_SNAPSHOT25;
    private static CovidSnapshot TEST_SNAPSHOT26;
    private static CovidSnapshot TEST_SNAPSHOT27;
    private static CovidSnapshot TEST_SNAPSHOT28;
    private static CovidSnapshot TEST_SNAPSHOT29;
    private static CovidSnapshot TEST_SNAPSHOT30;
    private static CovidSnapshot TEST_SNAPSHOT31;
    private static CovidSnapshot TEST_SNAPSHOT32;
    private static CovidSnapshot TEST_SNAPSHOT33;
    private static CovidSnapshot TEST_SNAPSHOT34;
    private static CovidSnapshot TEST_SNAPSHOT35;
    private static CovidSnapshot TEST_SNAPSHOT36;
    private static CovidSnapshot TEST_SNAPSHOT37;
    private static CovidSnapshot TEST_SNAPSHOT38;

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

        TEST_SNAPSHOT19 = new CovidSnapshot();
        TEST_SNAPSHOT19.setCountyTotalPopulation(1);
        TEST_SNAPSHOT19.setStateTotalPopulation(1);
        TEST_SNAPSHOT19.setCountryTotalPopulation(1);
        TEST_SNAPSHOT19.setCountyActiveCount(1);
        TEST_SNAPSHOT19.setStateActiveCount(1);
        TEST_SNAPSHOT19.setCountryActiveCount(1);

        TEST_SNAPSHOT20 = new CovidSnapshot();
        TEST_SNAPSHOT20.setCountyTotalPopulation(0);
        TEST_SNAPSHOT20.setStateTotalPopulation(1);
        TEST_SNAPSHOT20.setCountryTotalPopulation(1);
        TEST_SNAPSHOT20.setCountyActiveCount(1);
        TEST_SNAPSHOT20.setStateActiveCount(1);
        TEST_SNAPSHOT20.setCountryActiveCount(0);
        TEST_SNAPSHOT20.setLocationId(1);

        TEST_SNAPSHOT21 = new CovidSnapshot();
        TEST_SNAPSHOT21.setCountyTotalPopulation(1);
        TEST_SNAPSHOT21.setStateTotalPopulation(0);
        TEST_SNAPSHOT21.setCountryTotalPopulation(1);
        TEST_SNAPSHOT21.setCountyActiveCount(1);
        TEST_SNAPSHOT21.setStateActiveCount(1);
        TEST_SNAPSHOT21.setCountryActiveCount(0);

        TEST_SNAPSHOT22 = new CovidSnapshot();
        TEST_SNAPSHOT22.setCountyTotalPopulation(1);
        TEST_SNAPSHOT22.setStateTotalPopulation(1);
        TEST_SNAPSHOT22.setCountryTotalPopulation(0);
        TEST_SNAPSHOT22.setCountryActiveCount(0);
        TEST_SNAPSHOT22.setLocationId(2);
        TEST_SNAPSHOT22.setCovidSnapshotId(2);

        TEST_SNAPSHOT23 = new CovidSnapshot();
        TEST_SNAPSHOT23.setCountryActiveCount(1);
        TEST_SNAPSHOT23.setCountyTotalPopulation(0);
        TEST_SNAPSHOT23.setStateTotalPopulation(0);
        TEST_SNAPSHOT23.setCountryTotalPopulation(1);

        TEST_SNAPSHOT24 = new CovidSnapshot(2, 1, 2, 3, calendar);
        TEST_SNAPSHOT24.setCovidSnapshotId(20);
        TEST_SNAPSHOT24.setCountyTotalPopulation(2);
        TEST_SNAPSHOT24.setStateTotalPopulation(2);
        TEST_SNAPSHOT24.setCountryTotalPopulation(2);
        TEST_SNAPSHOT24.setCountyTotalPopulation(3);
        TEST_SNAPSHOT24.setStateTotalPopulation(3);
        TEST_SNAPSHOT24.setCountryTotalPopulation(3);

        TEST_SNAPSHOT25 = new CovidSnapshot(2, 0, 2, 3, calendar);

        TEST_SNAPSHOT26 = new CovidSnapshot(2, 2, 0, 3, calendar);

        TEST_SNAPSHOT27 = new CovidSnapshot(2, 0, 2, 0, calendar);

        TEST_SNAPSHOT28 = new CovidSnapshot(2, 1, 2, 0, calendar);

        TEST_SNAPSHOT29 = new CovidSnapshot(2, 1, 0, 0, calendar);

        TEST_SNAPSHOT30 = new CovidSnapshot(2, 0, 0, 3, calendar);

        TEST_SNAPSHOT31 = new CovidSnapshot(999, 0, 0, 0, calendar);
        TEST_SNAPSHOT31.setCovidSnapshotId(1);
        TEST_SNAPSHOT31.setCountyTotalPopulation(0);
        TEST_SNAPSHOT31.setStateTotalPopulation(1);
        TEST_SNAPSHOT31.setCountryTotalPopulation(0);

        TEST_SNAPSHOT32 = new CovidSnapshot(999, 0, 0, 0, calendar);
        TEST_SNAPSHOT32.setCovidSnapshotId(1);
        TEST_SNAPSHOT32.setCountyTotalPopulation(1);
        TEST_SNAPSHOT32.setStateTotalPopulation(0);
        TEST_SNAPSHOT32.setCountryTotalPopulation(0);

        TEST_SNAPSHOT33 = new CovidSnapshot(999, 0, 0, 0, calendar);
        TEST_SNAPSHOT33.setCovidSnapshotId(1);
        TEST_SNAPSHOT33.setCountyTotalPopulation(1);
        TEST_SNAPSHOT33.setStateTotalPopulation(0);
        TEST_SNAPSHOT33.setCountryTotalPopulation(0);

        TEST_SNAPSHOT34 = new CovidSnapshot(999, 10, 0, 0, calendar);
        TEST_SNAPSHOT34.setCovidSnapshotId(1);
        TEST_SNAPSHOT34.setCountyTotalPopulation(1);
        TEST_SNAPSHOT34.setStateTotalPopulation(0);
        TEST_SNAPSHOT34.setCountryTotalPopulation(0);

        TEST_SNAPSHOT35 = new CovidSnapshot(998, 0, 0, 0, calendar);
        TEST_SNAPSHOT35.setCovidSnapshotId(1);
        TEST_SNAPSHOT35.setCountyTotalPopulation(1);
        TEST_SNAPSHOT35.setStateTotalPopulation(0);
        TEST_SNAPSHOT35.setCountryTotalPopulation(0);

        TEST_SNAPSHOT36 = new CovidSnapshot(998, 0, 0, 0, calendar);
        TEST_SNAPSHOT36.setCovidSnapshotId(1);
        TEST_SNAPSHOT36.setCountyTotalPopulation(0);
        TEST_SNAPSHOT36.setStateTotalPopulation(0);
        TEST_SNAPSHOT36.setCountryTotalPopulation(0);

        TEST_SNAPSHOT37 = new CovidSnapshot(998, 10, 0, 0, calendar);
        TEST_SNAPSHOT37.setCovidSnapshotId(1);
        TEST_SNAPSHOT37.setCountyTotalPopulation(0);
        TEST_SNAPSHOT37.setStateTotalPopulation(0);
        TEST_SNAPSHOT37.setCountryTotalPopulation(0);

        TEST_SNAPSHOT38 = new CovidSnapshot(999, 10, 0, 0, calendar);
        TEST_SNAPSHOT38.setCovidSnapshotId(1);
        TEST_SNAPSHOT38.setCountyTotalPopulation(0);
        TEST_SNAPSHOT38.setStateTotalPopulation(0);
        TEST_SNAPSHOT38.setCountryTotalPopulation(0);
    }

    @Test
    public void hasSameCountsTest() {
        // T T T
        assertTrue(TEST_SNAPSHOT1.hasSameCounts(TEST_SNAPSHOT1));
        // T T F
        assertFalse(TEST_SNAPSHOT1.hasSameCounts(TEST_SNAPSHOT30));
        // T F T
        assertFalse(TEST_SNAPSHOT1.hasSameCounts(TEST_SNAPSHOT27));
        // T F F
        assertFalse(TEST_SNAPSHOT1.hasSameCounts(TEST_SNAPSHOT25));
        // F T T
        assertFalse(TEST_SNAPSHOT1.hasSameCounts(TEST_SNAPSHOT29));
        // F T F
        assertFalse(TEST_SNAPSHOT1.hasSameCounts(TEST_SNAPSHOT26));
        // F F T
        assertFalse(TEST_SNAPSHOT1.hasSameCounts(TEST_SNAPSHOT28));
        // F F F
        assertFalse(TEST_SNAPSHOT1.hasSameCounts(TEST_SNAPSHOT2));
    }

    @Test
    public void hasSamePopulationsTest() {
        // T T T
        assertTrue(TEST_SNAPSHOT1.hasSamePopulations(TEST_SNAPSHOT1));
        // T T F
        assertFalse(TEST_SNAPSHOT1.hasSamePopulations(TEST_SNAPSHOT23));
        // T F T
        assertFalse(TEST_SNAPSHOT1.hasSamePopulations(TEST_SNAPSHOT31));
        // T F F
        assertFalse(TEST_SNAPSHOT1.hasSamePopulations(TEST_SNAPSHOT20));
        // F T T
        assertFalse(TEST_SNAPSHOT1.hasSamePopulations(TEST_SNAPSHOT32));
        // F T F
        assertFalse(TEST_SNAPSHOT1.hasSamePopulations(TEST_SNAPSHOT21));
        // F F T
        assertFalse(TEST_SNAPSHOT1.hasSamePopulations(TEST_SNAPSHOT22));
        // F F F
        assertFalse(TEST_SNAPSHOT1.hasSamePopulations(TEST_SNAPSHOT2));
    }

    @Test
    public void snapshotHasSameDataTest() {
        // T T T
        assertTrue(TEST_SNAPSHOT1.hasSameData(TEST_SNAPSHOT1));
        // T T F
        assertFalse(TEST_SNAPSHOT1.hasSameData(TEST_SNAPSHOT38));
        // T F T
        assertFalse(TEST_SNAPSHOT1.hasSameData(TEST_SNAPSHOT33));
        // T F F
        assertFalse(TEST_SNAPSHOT1.hasSameData(TEST_SNAPSHOT34));
        // F T T
        assertFalse(TEST_SNAPSHOT1.hasSameData(TEST_SNAPSHOT36));
        // F T F
        assertFalse(TEST_SNAPSHOT1.hasSameData(TEST_SNAPSHOT37));
        // F F T
        assertFalse(TEST_SNAPSHOT1.hasSameData(TEST_SNAPSHOT35));
        // F F F
        assertFalse(TEST_SNAPSHOT23.hasSameData(TEST_SNAPSHOT1));
        assertFalse(TEST_SNAPSHOT1.hasSameData(TEST_SNAPSHOT23));
        assertFalse(TEST_SNAPSHOT1.hasSameData(TEST_SNAPSHOT2));
    }

    @Test
    public void snapshotHasSameLocationTest() {
        assertTrue(TEST_SNAPSHOT1.hasSameLocation(TEST_SNAPSHOT1));
        assertFalse(TEST_SNAPSHOT1.hasSameLocation(TEST_SNAPSHOT2));
    }

    @Test
    public void snapshotEqualsTest() {
        // T T
        assertTrue(TEST_SNAPSHOT2.equals(TEST_SNAPSHOT2));
        // T F
        assertFalse(TEST_SNAPSHOT2.equals(TEST_SNAPSHOT22));
        // F T
        assertFalse(TEST_SNAPSHOT2.equals(TEST_SNAPSHOT22));
        // F F
        assertFalse(TEST_SNAPSHOT2.equals(TEST_SNAPSHOT14));
        assertFalse(TEST_SNAPSHOT23.equals(TEST_SNAPSHOT14));
    }

    @Test
    public void snapshotMethodsTest() {
        assertFalse(TEST_SNAPSHOT1.hasSameData(TEST_SNAPSHOT2));
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
        assertFalse(TEST_SNAPSHOT19.hasFieldsSet());
        //  T F T
        assertFalse(TEST_SNAPSHOT20.hasFieldsSet());
        //  T F F
        assertFalse(TEST_SNAPSHOT21.hasFieldsSet());
        //  F T T
        assertFalse(TEST_SNAPSHOT3.hasFieldsSet());
        //  F T F
        assertFalse(TEST_SNAPSHOT5.hasFieldsSet());
        //  F F T
        assertFalse(TEST_SNAPSHOT22.hasFieldsSet());
        //  F F F
        assertFalse(TEST_SNAPSHOT23.hasFieldsSet());

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

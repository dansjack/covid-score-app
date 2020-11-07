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

    @BeforeClass
    public static void beforeClass() {
        Calendar calendar = Calendar.getInstance();
        TEST_SNAPSHOT1 = new CovidSnapshot(1, 0, 0, 0, calendar);
        TEST_SNAPSHOT1.setCovidSnapshotId(1);
        TEST_SNAPSHOT1.setCountyTotalPopulation(0);
        TEST_SNAPSHOT1.setStateTotalPopulation(0);
        TEST_SNAPSHOT1.setCountryTotalPopulation(0);

        TEST_SNAPSHOT2 = new CovidSnapshot(2, 1, 2, 3, calendar);
        TEST_SNAPSHOT2.setCovidSnapshotId(2);
        TEST_SNAPSHOT2.setCountyTotalPopulation(2);
        TEST_SNAPSHOT2.setStateTotalPopulation(2);
        TEST_SNAPSHOT2.setCountryTotalPopulation(2);

        TEST_SNAPSHOT3 = new CovidSnapshot(3, 2, 3, 4, calendar);
        TEST_SNAPSHOT3.setCovidSnapshotId(3);
        TEST_SNAPSHOT2.setCountyTotalPopulation(3);
        TEST_SNAPSHOT2.setStateTotalPopulation(3);
        TEST_SNAPSHOT2.setCountryTotalPopulation(3);
    }

    @Test
    public void snapshotMethodsTest() {
        assertFalse(TEST_SNAPSHOT1.equals(TEST_SNAPSHOT2));
        assertFalse(TEST_SNAPSHOT1.hasSameData(TEST_SNAPSHOT2));

    }
}

package com.nsc.covidscore;

import androidx.lifecycle.MutableLiveData;

import com.nsc.covidscore.room.CovidSnapshot;
import com.nsc.covidscore.room.RoomHelpers;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RoomHelpersUnitTest {
    private static MutableLiveData<CovidSnapshot> TEST_MCS1;
    private static MutableLiveData<CovidSnapshot> TEST_MCS2;
    private static MutableLiveData<CovidSnapshot> TEST_MCS3;
    private static MutableLiveData<CovidSnapshot> TEST_MCS4;
    private static MutableLiveData<CovidSnapshot> TEST_MCS5;

    private static CovidSnapshot TEST_CS1;
    private static CovidSnapshot TEST_CS2;
    private static CovidSnapshot TEST_CS3;

    @BeforeClass
    public static void beforeClass() {
        Calendar c = Calendar.getInstance();
        TEST_MCS1 = new MutableLiveData<>(new CovidSnapshot());

        TEST_MCS2 = new MutableLiveData<>();

        TEST_MCS3 = new MutableLiveData<>(new CovidSnapshot());

        TEST_MCS4 = new MutableLiveData<>(new CovidSnapshot(1, 2, 3, 4, c));
        TEST_MCS4.getValue().setCountyTotalPopulation(1);
        TEST_MCS4.getValue().setStateTotalPopulation(1);
        TEST_MCS4.getValue().setCountryTotalPopulation(1);

        TEST_MCS5 = new MutableLiveData<>(new CovidSnapshot(1, 2, 3, 4, c));
        TEST_MCS5.getValue().setLocationId(null);
        TEST_MCS5.getValue().setCountyTotalPopulation(1);
        TEST_MCS5.getValue().setStateTotalPopulation(1);
        TEST_MCS5.getValue().setCountryTotalPopulation(1);

        TEST_CS1 = new CovidSnapshot();

        TEST_CS2 = new CovidSnapshot();
        TEST_CS2.setLocationId(1);

        TEST_CS3 = new CovidSnapshot(1, 2, 3, 4, c);
        TEST_CS3.setCountyTotalPopulation(1);
        TEST_CS3.setStateTotalPopulation(1);
        TEST_CS3.setCountryTotalPopulation(1);
    }

    @Test
    public void roomShouldInsertSnapshotTest() {
        // F F
        assertFalse(RoomHelpers.shouldInsertSnapshot(TEST_MCS4, TEST_CS3));
        // F T
        assertTrue(RoomHelpers.shouldInsertSnapshot(TEST_MCS1, TEST_CS1));
        // T F
        // T T
        assertTrue(RoomHelpers.shouldInsertSnapshot(TEST_MCS2, TEST_CS1));
    }
}

package com.nsc.covidscore;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.nsc.covidscore.room.AppDatabase;
import com.nsc.covidscore.room.CovidSnapshot;
import com.nsc.covidscore.room.CovidSnapshotDao;
import com.nsc.covidscore.room.Location;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class RoomTest {
    private CovidSnapshotDao covidSnapshotDao;
    private AppDatabase db;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries().build();
        covidSnapshotDao = db.covidSnapshotDao();

        // Add CovidSnapshot to Room
        CovidSnapshot covidSnapshot = TestUtils.createCovidSnapshot();
        covidSnapshotDao.insert(covidSnapshot);
    }

    @After
    public void tearDown() {
        if (db != null) {
            db.close();
        }
    }

    @Test
    public void covidSnapshotDao_getAll() throws InterruptedException {
        List<CovidSnapshot> allCovidSnapshots = LiveDataTestUtil.getValue(covidSnapshotDao.getAll());
        assertNotNull(allCovidSnapshots);
        assertEquals(allCovidSnapshots.size(), 1);
        TestUtils.assertCovidSnapshotMatchesData(allCovidSnapshots.get(0));
    }

    @Test
    public void covidSnapshotDao_findById() throws InterruptedException {
        CovidSnapshot covidSnapshotById = LiveDataTestUtil.getValue(covidSnapshotDao.findById(1));
        assertNotNull(covidSnapshotById);
        TestUtils.assertCovidSnapshotMatchesData(covidSnapshotById);
    }

    @Test
    public void covidSnapshotDao_findLatestByLocationId() throws InterruptedException {
        CovidSnapshot latestCovidSnapshotById = LiveDataTestUtil.getValue(covidSnapshotDao.findLatestByLocationId(TestUtils.resourceId1));
        assertNotNull(latestCovidSnapshotById);
        TestUtils.assertCovidSnapshotMatchesData(latestCovidSnapshotById);
    }

    @Test
    public void covidSnapshotDao_findLatest() throws InterruptedException {
        CovidSnapshot latestCovidSnapshot = LiveDataTestUtil.getValue(covidSnapshotDao.getLatest());
        assertNotNull(latestCovidSnapshot);
        TestUtils.assertCovidSnapshotMatchesData(latestCovidSnapshot);
    }
}


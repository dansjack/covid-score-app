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
import com.nsc.covidscore.room.LocationDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class RoomTest {
    private LocationDao locationDao;
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
        locationDao = db.locationDao();
        covidSnapshotDao = db.covidSnapshotDao();

        // Add Location to Room
        Location location = TestUtils.createLocation();
        locationDao.insert(location);

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
    public void locationDao_getAll() throws InterruptedException {
        List<Location> allLocations = LiveDataTestUtil.getValue(locationDao.getAll());
        assertEquals(allLocations.size(), 1);
        TestUtils.assertGetAllMatchesData(allLocations);
    }

    @Test
    public void locationDao_findByCountyAndState() throws InterruptedException {
        Location response_found = LiveDataTestUtil.getValue(locationDao.findByCountyAndState(TestUtils.testCounty, TestUtils.testState));
        Location response_notFound = LiveDataTestUtil.getValue(locationDao.findByCountyAndState("", ""));
        assertNotNull(response_found);
        assertNull(response_notFound);
    }

    @Test
    public void locationDao_findByLocationId() throws InterruptedException {
        Location locationById = LiveDataTestUtil.getValue(locationDao.findByLocationId(1));
        assertNotNull(locationById);
    }

    @Test
    public void locationDao_getMostRecent() throws InterruptedException {
        Location mostRecentLocation = LiveDataTestUtil.getValue(locationDao.getMostRecent());
        assertNotNull(mostRecentLocation);
    }

    @Test
    public void covidSnapshotDao_getAll() throws InterruptedException {
        List<CovidSnapshot> allCovidSnapshots = LiveDataTestUtil.getValue(covidSnapshotDao.getAll());
        assertNotNull(allCovidSnapshots);
    }

    @Test
    public void covidSnapshotDau_findById() throws InterruptedException {
        CovidSnapshot covidSnapshotById = LiveDataTestUtil.getValue(covidSnapshotDao.findById(1));
        assertNotNull(covidSnapshotById);
    }

    @Test
    public void covidSnapshotDao_findLatestByLocationId() throws InterruptedException {
        CovidSnapshot latestCovidSnapshotById = LiveDataTestUtil.getValue(covidSnapshotDao.findLatestByLocationId(1));
        assertNotNull(latestCovidSnapshotById);
    }

    @Test
    public void covidSnapshotDao_findLatest() throws InterruptedException {
        CovidSnapshot latestCovidSnapshot = LiveDataTestUtil.getValue(covidSnapshotDao.findLatest());
        assertNotNull(latestCovidSnapshot);
    }
}


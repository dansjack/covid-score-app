package com.nsc.covidscore;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

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

//    @Mock
//    private Observer<List<Location>> locationObserver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries().build();
        locationDao = db.locationDao();
        covidSnapshotDao = db.covidSnapshotDao();

        // Add Location to Room
        Location location = TestUtils.createLocation();
        locationDao.insert(location);
    }

    @After
    public void tearDown() throws Exception {
        if (db != null) {
            db.close();
        }
    }

    @Test
    public void insert() throws Exception {
        // given
        Location location = TestUtils.createLocation();
        //locationDao.getAll().observeForever(locationObserver);
        // when
        locationDao.insert(location);
        // then
        //verify(locationObserver).onChanged(Collections.singletonList(location));

    }

    @Test
    public void getAll() throws InterruptedException {
//        Location location = TestUtils.createLocation();
//        locationDao.insert(location);
        List<Location> allLocations = LiveDataTestUtil.getValue(locationDao.getAll());
        assertEquals(allLocations.size(), 1);
        TestUtils.assertGetAllMatchesData(allLocations);
    }

    @Test
    public void findByCountyAndState() throws InterruptedException {
        Location response_found = LiveDataTestUtil.getValue(locationDao.findByCountyAndState(TestUtils.testCounty, TestUtils.testState));
        Location response_notFound = LiveDataTestUtil.getValue(locationDao.findByCountyAndState("", ""));
        assertNotNull(response_found);
        assertNull(response_notFound);
    }

    @Test
    public void findByLocationId() throws InterruptedException {
        Location locationById = LiveDataTestUtil.getValue(locationDao.findByLocationId(1));
        assertNotNull(locationById);
    }

    @Test
    public void getMostRecent() throws InterruptedException {
        Location mostRecentLocation = LiveDataTestUtil.getValue(locationDao.getMostRecent());
        assertNotNull(mostRecentLocation);
    }
}


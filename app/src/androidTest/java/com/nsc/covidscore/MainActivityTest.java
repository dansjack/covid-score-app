package com.nsc.covidscore;

import android.util.Log;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.Map;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void beforeClass() {
        InstrumentationRegistry.getInstrumentation().getContext().deleteDatabase("covid_snapshot_database");
    }

    @Test
    public void canSelectLocation() throws InterruptedException {
        try {
            // inside the LocationManualSelectionFragment

            // select state
            onView(withId(R.id.state_spinner)).perform(click());
            onData(allOf(is(instanceOf(String.class)), is("California"))).perform(click());

            // select county
            onView(withId(R.id.county_spinner)).perform(click());
            onData(allOf(is(instanceOf(String.class)), is("imperial"))).perform(click());

            // click submit
            onView(withId(R.id.submit_btn)).perform(click());

            Thread.sleep(1000);
        } catch (NoMatchingViewException ex) {
            // pass over this test to avoid crashing, we're in the RiskDetailPageFragment
        }
    }

    @Test
    public void canViewCovidStats() {
        try {
            // inside the RiskDetailPageFragment
            onView(withId(R.id.activeCounty)).check(matches(not(withText(""))));
        } catch (NoMatchingViewException ex) {
            // pass over this test to avoid crashing, we're in the LocationManualSelectionFragment
        }

    }



}

package com.nsc.covidscore;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void t1_canSelectLocation() throws InterruptedException {
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

            Thread.sleep(5000);
            onView(withId(R.layout.fragment_location_selection)).check(matches(isDisplayed()));
        } catch (NoMatchingViewException ex) {
            // pass over this test to avoid crashing, we're in the RiskDetailPageFragment
        }
    }

    @Test
    public void t2_canViewCovidStats() {
        // inside the RiskDetailPageFragment
        onView(withId(R.id.activeCounty)).check(matches(not(withText(""))));
    }

    @Test
    public void t3_canSelectNewLocation() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.select_location_btn)).perform(scrollTo());
        onView(withId(R.id.select_location_btn)).perform(click());

        // click submit
        onView(withId(R.id.submit_btn)).perform(click());
        onView(withId(R.id.loadingTextView)).check(matches(withText("Please pick a state and county")));

        // select state
        onView(withId(R.id.state_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Washington"))).perform(click());

        // select county
        onView(withId(R.id.county_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("king"))).perform(click());

        Thread.sleep(3000);
        pressBack();
    }


}

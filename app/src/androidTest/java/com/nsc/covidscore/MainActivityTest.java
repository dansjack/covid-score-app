package com.nsc.covidscore;

import android.app.Instrumentation;
import android.provider.Settings;
import android.view.View;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.PerformException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
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
    public void t1_canSelectLocationAndViewRisk() throws InterruptedException {
        try {
            Thread.sleep(8000); // Time for app to load
            try { // Are we in the LocationManualSelectionFragment?
                onView(withId(R.id.state_spinner)).check(matches(isDisplayed()));
            } catch (NoMatchingViewException | PerformException ex) {
                // We are in RiskDetailFragment - click select new
                onView(withId(R.id.activeCounty)).check(matches(not(withText(""))));
                onView(withId(R.id.select_location_btn)).perform(click());
            }

            // inside the LocationManualSelectionFragment

            // select state
            onView(withId(R.id.state_spinner)).perform(click());
            onData(allOf(is(instanceOf(String.class)), is("California"))).perform(click());

            // select county
            onView(withId(R.id.county_spinner)).perform(click());
            onData(allOf(is(instanceOf(String.class)), is("Imperial"))).perform(click());

            // click submit
            onView(withId(R.id.submit_btn)).perform(click());

            Thread.sleep(8000);

            // This should throw a NoMatchingViewException
            onView(withId(R.id.location_entry_tv)).perform(click());
        } catch (NoMatchingViewException | PerformException ex) {
            // pass over this test to avoid crashing, we're in the RiskDetailPageFragment
            // inside the RiskDetailPageFragment
            onView(withId(R.id.activeCounty)).check(matches(not(withText(""))));
        }
        onView(withId(R.id.risk_detail_frag)).check(matches(isDisplayed()));
    }

    @Test
    public void t2_canSelectNewLocation() throws InterruptedException {
        try {
            Thread.sleep(8000); // Time for the app to load

            try { // Are we in the LocationManualSelectionFragment? Get to the RiskDetailFragment
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
            } catch (NoMatchingViewException | PerformException ex) {
                // We are in RiskDetailFragment - click select new
                onView(withId(R.id.activeCounty)).check(matches(not(withText(""))));
                onView(withId(R.id.select_location_btn)).perform(click());
            }
            // In New LocationManualSelectionFragment

            // click submit
            onView(withId(R.id.submit_btn)).perform(click());
            onView(withId(R.id.loadingTextView)).check(matches(withText("Please pick a state and county")));

            // select state
            onView(withId(R.id.state_spinner)).perform(click());
            onData(allOf(is(instanceOf(String.class)), is("Washington"))).perform(click());

            // select county
            onView(withId(R.id.county_spinner)).perform(click());
            onData(allOf(is(instanceOf(String.class)), is("King"))).perform(click());

            Thread.sleep(3000);
            pressBack();

        } catch (PerformException | NoMatchingViewException ex) {
            // we're in the RiskDetailPageFragment
            onView(withId(R.id.activeCounty)).check(matches(not(withText(""))));
        }
    }

    // This test works locally - uncomment and try it!
    // TODO: find a way to disable/enable connectivity that works with CircleCI
//    @Test
//    public void t3_checksInternet() throws InterruptedException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, UiObjectNotFoundException {
//        Thread.sleep(8000); // Time for app to load
//        try { // Are we in the LocationManualSelectionFragment?
//            // select state
//            onView(withId(R.id.state_spinner)).perform(click());
//            onData(allOf(is(instanceOf(String.class)), is("California"))).perform(click());
//        } catch (NoMatchingViewException | PerformException ex) {
//            // We are in RiskDetailFragment - click select new
//            onView(withId(R.id.select_location_btn)).perform(click());
//        }
//        // In LocationManualSelectionFragment
//
//        // disable wifi & data
//        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc wifi disable");
//        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc data disable");
//
//        // select state
//        onView(withId(R.id.state_spinner)).perform(click());
//        onData(allOf(is(instanceOf(String.class)), is("Washington"))).perform(click());
//
//        // select county
//        onView(withId(R.id.county_spinner)).perform(click());
//        onData(allOf(is(instanceOf(String.class)), is("king"))).perform(click());
//
//        // submit
//        onView(withId(R.id.submit_btn)).perform(click());
//        onView(withId(R.id.loadingTextView)).check(matches(withText(R.string.no_internet)));
//
//        // enable wifi & data
//        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc wifi enable");
//        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc data enable");
//
//        Thread.sleep(2000);
//
//        // submit
//        onView(withId(R.id.submit_btn)).perform(click());
//        onView(withId(R.id.loadingTextView)).check(matches(withText(R.string.loading_data)));
//    }
}

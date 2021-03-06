package com.nsc.covidscore;

import android.view.Gravity;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
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
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
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
    public void t0_noComparingNoLocations() throws InterruptedException {
        Thread.sleep(8000); // time for app to load

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nvView)).perform(NavigationViewActions
                .navigateTo(R.id.nav_compare_fragment)); // start compare fragment

        Thread.sleep(5000);

        onView(withId(R.id.comparing_tv))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.nothing_to_compare)));

        onView(withId(R.id.compare_location_1))
                .check(matches(not(isDisplayed())));

        onView(withId(R.id.compare_location_2))
                .check(matches(not(isDisplayed())));

        onView(withId(R.id.compare_location_3))
                .check(matches(not(isDisplayed())));

        onView(withId(R.id.comparisonGraph))
                .check(matches(not(isDisplayed())));
    }

    @Test
    public void t1_canSelectLocationAndViewRisk() throws InterruptedException {
        try {
            Thread.sleep(2000); // Time for app to load
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

    @Test
    public void t3_testNavigationView() throws InterruptedException {
        Thread.sleep(2000);

        // test nav_about_fragment
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nvView)).perform(NavigationViewActions
                .navigateTo(R.id.nav_about_fragment)); // start nav_about_fragment
        Thread.sleep(1000);

        //TODO: this test currently relies on the tests above selecting locations to complete
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nvView)).perform(NavigationViewActions
                .navigateTo(R.id.nav_location_fragment_1)); // start nav_location_fragment_1
        Thread.sleep(1000);

        //TODO: this test currently relies on the tests above selecting locations to complete
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nvView)).perform(NavigationViewActions
                .navigateTo(R.id.nav_location_fragment_2)); // start nav_location_fragment_2
        Thread.sleep(1000);

//        // test riskdetail3
//        onView(withId(R.id.drawer_layout))
//                .check(matches(isClosed(Gravity.LEFT)))
//                .perform(DrawerActions.open());
//        onView(withId(R.id.nvView)).perform(NavigationViewActions
//                .navigateTo(R.id.nav_location_fragment_3));
//        Thread.sleep(1000);

    }

    @Test
    public void t4_noComparingOneLocation() throws InterruptedException {
        Thread.sleep(2000);

        //TODO: this test currently relies on the tests above selecting locations to complete
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nvView)).perform(NavigationViewActions
                .navigateTo(R.id.nav_compare_fragment)); // start compare fragment

        Thread.sleep(1000);

        onView(withId(R.id.comparing_tv))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.need_more_locations)));

        onView(withId(R.id.compare_location_1))
                .check(matches(not(isDisplayed())));

        onView(withId(R.id.compare_location_2))
                .check(matches(not(isDisplayed())));

        onView(withId(R.id.compare_location_3))
                .check(matches(not(isDisplayed())));

        onView(withId(R.id.comparisonGraph))
                .check(matches(not(isDisplayed())));
    }

    public void t5_canCompareLocations() throws InterruptedException {
        Thread.sleep(2000);

        try { // Are we in the LocationManualSelectionFragment?
            onView(withId(R.id.state_spinner)).check(matches(isDisplayed()));
        } catch (NoMatchingViewException | PerformException ex) {
            // We are in RiskDetailFragment - click select new
            onView(withId(R.id.activeCounty)).check(matches(not(withText(""))));
            onView(withId(R.id.select_location_btn)).perform(click());
        }

        // inside the LocationManualSelectionFragment
        // Add 2nd Location
        // select state
        onView(withId(R.id.state_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Washington"))).perform(click());

        // select county
        onView(withId(R.id.county_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("King"))).perform(click());

        // click submit
        onView(withId(R.id.submit_btn)).perform(click());

        Thread.sleep(3000);

        //TODO: this test currently relies on the tests above selecting locations to complete
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nvView)).perform(NavigationViewActions
                .navigateTo(R.id.nav_compare_fragment)); // start compare fragment

        Thread.sleep(1000);

        onView(withId(R.id.comparing_tv))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.comparing_text)));

        onView(withId(R.id.compare_location_1))
                .check(matches(isDisplayed()))
                .check(matches(withText("King, Washington")));

        onView(withId(R.id.compare_location_2))
                .check(matches(isDisplayed()))
                .check(matches(withText("Imperial, California")));

        onView(withId(R.id.comparisonGraph))
                .check(matches(isDisplayed()));
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

    @Test
    public void t6_testAboutPageExpandable() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout))
                .check(matches(isDisplayed()))
                .perform(DrawerActions.open());
        onView(withId(R.id.nvView))
                .perform(NavigationViewActions.navigateTo(R.id.nav_about_fragment));
        Thread.sleep(1000);
        onView(withId(R.id.riskExplanationTextView))
                .perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.riskExplanationTextView))
                .perform(click());

        onView(withId(R.id.censusLink)).perform(click());
        Thread.sleep(1000);
    }

    @Test
    public void t7_testAboutDiseaseLink() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout))
                .check(matches(isDisplayed()))
                .perform(DrawerActions.open());
        onView(withId(R.id.nvView))
                .perform(NavigationViewActions.navigateTo(R.id.nav_about_fragment));

        Thread.sleep(1000);
        onView(withId(R.id.diseaseGithubLink)).perform(click());
    }

    @Test
    public void t8_testAboutAppGithubLink() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout))
                .check(matches(isDisplayed()))
                .perform(DrawerActions.open());
        onView(withId(R.id.nvView))
                .perform(NavigationViewActions.navigateTo(R.id.nav_about_fragment));

        onView(withId(R.id.atlanticLink))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        Thread.sleep(1000);
        onView(withId(R.id.appGithubLink)).perform(click());
    }

    @Test
    public void t9_testAboutGTechLink() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout))
                .check(matches(isDisplayed()))
                .perform(DrawerActions.open());
        onView(withId(R.id.nvView))
                .perform(NavigationViewActions.navigateTo(R.id.nav_about_fragment));

        Thread.sleep(1000);

        onView(withId(R.id.atlanticLink))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.gTechLink)).perform(click());
    }

    @Test
    public void t10_testAboutAtlanticLink() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.drawer_layout))
                .check(matches(isDisplayed()))
                .perform(DrawerActions.open());
        onView(withId(R.id.nvView))
                .perform(NavigationViewActions.navigateTo(R.id.nav_about_fragment));

        Thread.sleep(1000);

        onView(withId(R.id.atlanticLink))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.atlanticLink)).perform(click());
    }
}

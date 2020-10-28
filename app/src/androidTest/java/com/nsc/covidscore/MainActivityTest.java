package com.nsc.covidscore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule
            = new ActivityTestRule<>(MainActivity.class, true, true);

    @Test
    public void loadsViews() {
        onView(withId(R.id.hello_world))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testCalculation() {
        // King County 10/23/20
        assertEquals(RiskCalculation.calculateRisk(24914, 2252782, 20), 19.94, 0);
        // WA State 10/23/20
        assertEquals(RiskCalculation.calculateRisk(52948, 7614893, 20), 13.03, 0);
        // US 10/23/20
        assertEquals(RiskCalculation.calculateRisk(4831614, 328239523, 20), 25.66, 0);

    }
}
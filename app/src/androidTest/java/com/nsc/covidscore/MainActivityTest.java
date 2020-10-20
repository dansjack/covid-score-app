package com.nsc.covidscore;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule
            = new ActivityTestRule<>(MainActivity.class, true, true);

}
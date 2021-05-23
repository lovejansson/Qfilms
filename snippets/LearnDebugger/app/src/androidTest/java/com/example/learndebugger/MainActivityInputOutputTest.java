package com.example.learndebugger;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityInputOutputTest {

    @Rule
    public ActivityScenarioRule mActivityRule = new ActivityScenarioRule(
            MainActivity.class);
    
    @Test
    public void calculateSomething() {
        onView(withId(R.id.et_number_one)).perform(typeText("4"));
        onView(withId(R.id.et_number_two)).perform(typeText("4"));
        onView(withId(R.id.btn_calculate)).perform(click());

        onView(withId(R.id.tv_answer)).check(matches(withText("8")));
    }
}


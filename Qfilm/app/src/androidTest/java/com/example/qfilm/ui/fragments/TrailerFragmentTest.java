package com.example.qfilm.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.example.qfilm.R;
import com.example.qfilm.ui.fragments.TrailerFragment;

import org.junit.Test;
import qfilm.TestFragmentActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static utils.TestDummyData.VIDEO_ONE;


public class TrailerFragmentTest {

    @Test
    public void testYoutubeViewVisibility(){

        TrailerFragment trailerFragment = new TrailerFragment(VIDEO_ONE);

        // start the activity and launch fragment
        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(trailerFragment);
                    }
                });

        // verify

        onView(withId(R.id.youtube_player_view)).check(matches(isDisplayed()));
    }

}


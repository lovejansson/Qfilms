package com.example.qfilm.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.ui.fragments.dialogFragments.DialogFragmentGenres;
import com.example.qfilm.ui.utils.matchers.CustomMatchers;
import utils.MyTestViewModelFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import qfilm.TestFragmentActivity;
import utils.Mocks;
import utils.MyIdlingResource;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.qfilm.data.models.entities.MediaType.MOVIE;
import static org.mockito.Mockito.when;
import static utils.TestDummyData.COMEDY;
import static utils.TestDummyData.DRAMA;
import static utils.TestDummyData.GENRE_NAME_COMEDY;
import static utils.TestDummyData.GENRE_NAME_DRAMA;


public class DialogFragmentGenresTest {

    private static final String TAG = "DialogFragmentGenresTes";

    private DialogFragmentGenres dialogFragmentGenres;

    private MutableLiveData<DataResource<List<Genre>>> genresObservable;

    @Before
    public void setup(){

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

        genresObservable = new MutableLiveData<>();

        when(Mocks.resultWithGenreViewModelMock.getGenresObservable()).thenReturn(genresObservable);

        dialogFragmentGenres = new DialogFragmentGenres(DRAMA, MOVIE, new MyTestViewModelFactory());

    }


    @After
    public void teardown(){


        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());
        MyIdlingResource.reset();
        Mocks.reset();

    }


    @Test
    public void fetchGenres_success(){

        // setup

        List<Genre> genres = new ArrayList<>();

        genres.add(COMEDY);
        genres.add(DRAMA);

        when(Mocks.resultWithGenreViewModelMock.fetchGenres(MOVIE, "es")).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                genresObservable.setValue(DataResource.success(genres));

                return null;
            }

        });

        when(Mocks.resultWithGenreViewModelMock.fetchGenres(MOVIE, "en")).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                genresObservable.setValue(DataResource.success(genres));

                return null;
            }

        });


        // act

        // start the activity and launch fragment
        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(dialogFragmentGenres);
                    }
                });

        // verify

        onView(withId(R.id.progress_bar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.error_message)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.rv_genres)).check(matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(3)));

        onView(withId(R.id.rv_genres)).check(matches(CustomMatchers.recyclerViewMatcher().itemAtPositionHasText(1,
                R.id.tv_genre_name, GENRE_NAME_COMEDY)));

        onView(withId(R.id.rv_genres)).check(matches(CustomMatchers.recyclerViewMatcher().itemAtPositionHasText(2,
                R.id.tv_genre_name, GENRE_NAME_DRAMA)));

        onView(withId(R.id.rv_genres)).check(matches(CustomMatchers.recyclerViewMatcher().itemAtPositionIsSelected(2)));


    }


    @Test
    public void fetchGenres_loading(){

        // setup

        when(Mocks.resultWithGenreViewModelMock.fetchGenres(MOVIE, "es")).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                genresObservable.setValue(DataResource.loading(null));

                return null;
            }

        });

        when(Mocks.resultWithGenreViewModelMock.fetchGenres(MOVIE, "en")).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                genresObservable.setValue(DataResource.loading(null));

                return null;
            }

        });


        // act

        // start the activity and launch fragment
        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(dialogFragmentGenres);
                    }
                });


        // verify

        onView(withId(R.id.rv_genres)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.error_message)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()));

    }


    @Test
    public void fetchGenres_error_withOldData(){

        // setup

        List<Genre> genres = new ArrayList<>();

        genres.add(COMEDY);
        genres.add(DRAMA);

        when(Mocks.resultWithGenreViewModelMock.fetchGenres(MOVIE, "es")).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                genresObservable.setValue(DataResource.error(genres, null));

                return null;
            }

        });

        when(Mocks.resultWithGenreViewModelMock.fetchGenres(MOVIE, "en")).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                genresObservable.setValue(DataResource.error(genres, null));

                return null;
            }

        });


        // act

        // start the activity and launch fragment
        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(dialogFragmentGenres);
                    }
                });



        // verify

        onView(withId(R.id.progress_bar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.error_message)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.rv_genres)).check(matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(3)));

        onView(withId(R.id.rv_genres)).check(matches(CustomMatchers.recyclerViewMatcher().itemAtPositionHasText(1,
                R.id.tv_genre_name, GENRE_NAME_COMEDY)));

        onView(withId(R.id.rv_genres)).check(matches(CustomMatchers.recyclerViewMatcher().itemAtPositionHasText(2,
                R.id.tv_genre_name, GENRE_NAME_DRAMA)));

        onView(withId(R.id.rv_genres)).check(matches(CustomMatchers.recyclerViewMatcher().itemAtPositionIsSelected(2)));


    }


    @Test
    public void fetchGenres_error_nullData(){

        // setup

        when(Mocks.resultWithGenreViewModelMock.fetchGenres(MOVIE, "es")).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                genresObservable.setValue(DataResource.error(null, null));

                return null;
            }

        });

        when(Mocks.resultWithGenreViewModelMock.fetchGenres(MOVIE, "en")).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                genresObservable.setValue(DataResource.error(null, null));

                return null;
            }

        });


        // act

        // start the activity and launch fragment
        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(dialogFragmentGenres);
                    }
                });



        // verify

        onView(withId(R.id.progress_bar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.rv_genres)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.error_message)).check(matches(isDisplayed()));

        onView(withId(R.id.tv_error_title)).check(matches(withText(
                ApplicationProvider.getApplicationContext().getString(R.string.error_unknown_heading))));

        onView(withId(R.id.tv_error_description)).check(matches(withText(
                ApplicationProvider.getApplicationContext().getString(R.string.error_unknown_message))));

        onView(withId(R.id.btn_try_again)).check(matches(withText(
                ApplicationProvider.getApplicationContext().getString(R.string.btn_try_again))));

    }


    @Test
    public void fetchGenres_error_tryAgain(){

        // setup

        List<Genre> genres = new ArrayList<>();

        genres.add(COMEDY);
        genres.add(DRAMA);

        when(Mocks.resultWithGenreViewModelMock.fetchGenres(MOVIE, "es")).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                genresObservable.setValue(DataResource.error(null, null));

                return null;
            }

        }).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                genresObservable.setValue(DataResource.success(genres));

                return null;
            }

        });

        when(Mocks.resultWithGenreViewModelMock.fetchGenres(MOVIE, "en")).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                genresObservable.setValue(DataResource.error(null, null));

                return null;
            }

        }).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                genresObservable.setValue(DataResource.success(genres));

                return null;
            }

        });


        // act

        // start the activity and launch fragment
        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(dialogFragmentGenres);
                    }
                });


        onView(withId(R.id.btn_try_again)).perform(click());

        // verify

        onView(withId(R.id.progress_bar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.error_message)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.rv_genres)).check(matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(3)));

        onView(withId(R.id.rv_genres)).check(matches(CustomMatchers.recyclerViewMatcher().itemAtPositionHasText(1,
                R.id.tv_genre_name, GENRE_NAME_COMEDY)));

        onView(withId(R.id.rv_genres)).check(matches(CustomMatchers.recyclerViewMatcher().itemAtPositionHasText(2,
                R.id.tv_genre_name, GENRE_NAME_DRAMA)));

        onView(withId(R.id.rv_genres)).check(matches(CustomMatchers.recyclerViewMatcher().itemAtPositionIsSelected(2)));

    }

}

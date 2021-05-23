package com.example.qfilm.ui.fragments;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.MovieDetails;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.ui.utils.matchers.CustomMatchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Collections;

import qfilm.TestFragmentActivity;
import utils.Mocks;
import utils.MyIdlingResource;
import utils.MyTestViewModelFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static utils.TestDummyData.GENRES_ONE;
import static utils.TestDummyData.ID_ONE;
import static utils.TestDummyData.IMAGES_ONE;
import static utils.TestDummyData.IMAGE_FILE_PATH_BACKDROP_ONE;
import static utils.TestDummyData.IMAGE_FILE_PATH_POSTER_ONE;
import static utils.TestDummyData.IMDB_ID_ONE;
import static utils.TestDummyData.MOVIE_DETAILS_ONE;
import static utils.TestDummyData.ORG_TITLE_ONE;
import static utils.TestDummyData.OVERVIEW_ONE;
import static utils.TestDummyData.RATING_ONE;
import static utils.TestDummyData.RATING_VALUE_ONE;
import static utils.TestDummyData.RELEASE_DATE_ONE;
import static utils.TestDummyData.RESULT_ONE;
import static utils.TestDummyData.RUNTIME_ONE;
import static utils.TestDummyData.TIMESTAMP_ONE;
import static utils.TestDummyData.TITLE_ONE;
import static utils.TestDummyData.VIDEOS_EMPTY;
import static utils.TestDummyData.VIDEOS_ONE;

public class DetailPageFragmentTest {

    // tested
    DetailPageFragment detailPageFragment;

    MutableLiveData<DataResource<MovieDetails>> movieDetailsObservable;


    @Before
    public void setUp(){

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

        movieDetailsObservable = new MutableLiveData<>();

        when(Mocks.detailsViewModelMock.getMovieDetailsLiveData()).thenReturn(movieDetailsObservable);

        detailPageFragment = new DetailPageFragment(RESULT_ONE, Mocks.firebaseAuthMock, new MyTestViewModelFactory());

        MyIdlingResource.increment();

    }


    @After
    public void tearDown(){

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());

        MyIdlingResource.reset();

        Mocks.reset();
    }



    @Test
    public void testVisibilities_successResponse_allFieldsExists() throws InterruptedException {

        // arrange

        MovieDetails movieDetails = new MovieDetails(ID_ONE, IMDB_ID_ONE, TITLE_ONE,
                ORG_TITLE_ONE, GENRES_ONE, OVERVIEW_ONE, IMAGE_FILE_PATH_POSTER_ONE,
                IMAGE_FILE_PATH_BACKDROP_ONE, RELEASE_DATE_ONE, RUNTIME_ONE, VIDEOS_ONE, IMAGES_ONE,
                Collections.singletonList(RATING_ONE), TIMESTAMP_ONE, "en");

        movieDetails.setImdbRating(RATING_VALUE_ONE);


        when(Mocks.detailsViewModelMock.getMovieDetails(anyInt(), anyString())).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                movieDetailsObservable.setValue(DataResource.success(movieDetails));

                return null;
            }

        });

        // start the activity and launch fragment
        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(detailPageFragment);
                    }
                });



        // verify

        // TextViews

        Context context = ApplicationProvider.getApplicationContext();

        onView(withId(R.id.tv_title)).check(matches(withText(TITLE_ONE)));

        onView(withId(R.id.tv_release_year)).check(matches(withText(MOVIE_DETAILS_ONE.getReleaseYear())));
        onView(withId(R.id.tv_runtime)).check(matches(withText(MOVIE_DETAILS_ONE.getRuntime() + "min")));
        onView(withId(R.id.tv_genres)).check(matches(withText(MOVIE_DETAILS_ONE.genresToString())));

        onView(withId(R.id.tv_label_genres)).check(matches(withText(context.getString(R.string.tv_label_genres))));
        onView(withId(R.id.tv_label_release_year)).check(matches(withText(context.getString(R.string.tv_label_release_year))));
        onView(withId(R.id.tv_label_runtime)).check(matches(withText(ApplicationProvider.getApplicationContext()
        .getString(R.string.tv_label_runtime))));

        onView(withId(R.id.tv_imdb_rating)).check(matches(withText(movieDetails.getImdbRating())));
        onView(withId(R.id.tv_btn_trailer)).check(matches(withText(context.getString(R.string.watch_trailer))));

        onView(withId(R.id.tv_overview)).check(matches(withText(OVERVIEW_ONE)));
        onView(withId(R.id.tv_overview_headline)).check(matches(withText(context.getString(R.string.tv_overview_headline))));

        // Views with drawables
        onView(withId(R.id.iv_btn_trailer)).check(matches(CustomMatchers.drawableInImageViewMatcher(R.drawable.ic_play)));
        onView(withId(R.id.iv_close_detail_page)).check(matches(CustomMatchers.drawableInImageViewMatcher(R.drawable.ic_close_on_background)));
        onView(withId(R.id.iv_imdb_rating)).check(matches(CustomMatchers.drawableInImageViewMatcher(R.drawable.icon_imdb)));

        // other displayed stuff

        onView(withId(R.id.divider_line)).check(matches(isDisplayed()));
        onView(withId(R.id.iv_backdrop_image)).check(matches(isDisplayed()));

        // add to button
        onView(withId(R.id.btn_add_to)).check(matches(isDisplayed()));

        // image recyclerViews for posters and backdrops

        onView(withId(R.id.content)).perform(ViewActions.swipeUp()); // they are further down in the ScrollerView

        onView(withId(R.id.rv_posters)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_posters_headline)).check(matches(isDisplayed()));

        onView(withId(R.id.rv_images)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_images_headline)).check(matches(isDisplayed()));


    }

    @Test
    public void testVisibilities_successResponse_missingFields() throws InterruptedException {

        // this time the details object has no trailer or imdb rating

        // arrange

        MovieDetails movieDetails = new MovieDetails(ID_ONE, IMDB_ID_ONE, TITLE_ONE,
                ORG_TITLE_ONE, GENRES_ONE, OVERVIEW_ONE, IMAGE_FILE_PATH_POSTER_ONE,
                IMAGE_FILE_PATH_BACKDROP_ONE, RELEASE_DATE_ONE, RUNTIME_ONE, VIDEOS_EMPTY, IMAGES_ONE,
                Collections.singletonList(RATING_ONE), TIMESTAMP_ONE, "en");


        when(Mocks.detailsViewModelMock.getMovieDetails(anyInt(), anyString())).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                movieDetailsObservable.setValue(DataResource.success(movieDetails));

                return null;
            }

        });

        // start the activity and launch fragment
        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(detailPageFragment);
                    }
                });


        // verify

        onView(withId(R.id.tv_imdb_rating)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.tv_btn_trailer)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.iv_btn_trailer)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.iv_imdb_rating)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));


    }


    @Test
    public void testVisibilities_loadingResponse()  {

        // setup mocked repository response
        when(Mocks.detailsViewModelMock.getMovieDetails(anyInt(), anyString())).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                movieDetailsObservable.setValue(DataResource.loading(null));

                return null;
            }

        });


        // start the activity and launch fragment
        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(detailPageFragment);
                    }
                });


        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()));


    }


    @Test
    public void testVisibilities_errorResponse_attachedOldData(){

        // arrange

        MovieDetails movieDetails = new MovieDetails(ID_ONE, IMDB_ID_ONE, TITLE_ONE,
                ORG_TITLE_ONE, GENRES_ONE, OVERVIEW_ONE, IMAGE_FILE_PATH_POSTER_ONE,
                IMAGE_FILE_PATH_BACKDROP_ONE, RELEASE_DATE_ONE, RUNTIME_ONE, VIDEOS_ONE, IMAGES_ONE,
                Collections.singletonList(RATING_ONE), TIMESTAMP_ONE, "en");

        movieDetails.setImdbRating(RATING_VALUE_ONE);


        when(Mocks.detailsViewModelMock.getMovieDetails(anyInt(), anyString())).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                movieDetailsObservable.setValue(DataResource.error(movieDetails, null));

                return null;
            }

        });

        // start the activity and launch fragment
        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(detailPageFragment);
                    }
                });


        // verify

        // TextViews

        Context context = ApplicationProvider.getApplicationContext();

        onView(withId(R.id.tv_title)).check(matches(withText(TITLE_ONE)));

        onView(withId(R.id.tv_release_year)).check(matches(withText(MOVIE_DETAILS_ONE.getReleaseYear())));
        onView(withId(R.id.tv_runtime)).check(matches(withText(MOVIE_DETAILS_ONE.getRuntime() + "min")));
        onView(withId(R.id.tv_genres)).check(matches(withText(MOVIE_DETAILS_ONE.genresToString())));

        onView(withId(R.id.tv_label_genres)).check(matches(withText(context.getString(R.string.tv_label_genres))));
        onView(withId(R.id.tv_label_release_year)).check(matches(withText(context.getString(R.string.tv_label_release_year))));
        onView(withId(R.id.tv_label_runtime)).check(matches(withText(context.getString(R.string.tv_label_runtime))));

        onView(withId(R.id.tv_imdb_rating)).check(matches(withText(movieDetails.getImdbRating())));
        onView(withId(R.id.tv_btn_trailer)).check(matches(withText(context.getString(R.string.watch_trailer))));

        onView(withId(R.id.tv_overview)).check(matches(withText(OVERVIEW_ONE)));
        onView(withId(R.id.tv_overview_headline)).check(matches(withText(context.getString(R.string.tv_overview_headline))));

        // Views with drawables
        onView(withId(R.id.iv_btn_trailer)).check(matches(CustomMatchers.drawableInImageViewMatcher(R.drawable.ic_play)));
        onView(withId(R.id.iv_close_detail_page)).check(matches(CustomMatchers.drawableInImageViewMatcher(R.drawable.ic_close_on_background)));
        onView(withId(R.id.iv_imdb_rating)).check(matches(CustomMatchers.drawableInImageViewMatcher(R.drawable.icon_imdb)));

        // other displayed stuff

        onView(withId(R.id.divider_line)).check(matches(isDisplayed()));
        onView(withId(R.id.iv_backdrop_image)).check(matches(isDisplayed()));

        // add to button
        onView(withId(R.id.btn_add_to)).check(matches(isDisplayed()));

        // image recyclerViews for posters and backdrops

        onView(withId(R.id.content)).perform(ViewActions.swipeUp()); // they are further down in the ScrollerView

        onView(withId(R.id.rv_posters)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_posters_headline)).check(matches(isDisplayed()));

        onView(withId(R.id.rv_images)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_images_headline)).check(matches(isDisplayed()));


    }


    @Test
    public void testVisibilities_errorResponse_nullData(){

        // arrange

        when(Mocks.detailsViewModelMock.getMovieDetails(anyInt(), anyString())).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                movieDetailsObservable.setValue(DataResource.error(null, null));

                return null;
            }

        });

        // start the activity and launch fragment
        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(detailPageFragment);
                    }
                });


        // verify

        // TextViews

        onView(withId(R.id.content)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.tv_error_title)).check(matches(withText(ApplicationProvider.getApplicationContext()
        .getString(R.string.error_unknown_heading))));

        onView(withId(R.id.tv_error_description)).check(matches(withText(
                ApplicationProvider.getApplicationContext().getString(R.string.error_unknown_message))));


    }


    @Test
    public void testVisibilities_errorResponse_tryAgain(){

        // arrange

        MovieDetails movieDetails = new MovieDetails(ID_ONE, IMDB_ID_ONE, TITLE_ONE,
                ORG_TITLE_ONE, GENRES_ONE, OVERVIEW_ONE, IMAGE_FILE_PATH_POSTER_ONE,
                IMAGE_FILE_PATH_BACKDROP_ONE, RELEASE_DATE_ONE, RUNTIME_ONE, VIDEOS_ONE, IMAGES_ONE,
                Collections.singletonList(RATING_ONE), TIMESTAMP_ONE, "en");

        movieDetails.setImdbRating(RATING_VALUE_ONE);

        when(Mocks.detailsViewModelMock.getMovieDetails(anyInt(), anyString())).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                movieDetailsObservable.setValue(DataResource.error(null, null));

                return null;
            }

        }).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {


                movieDetailsObservable.setValue(DataResource.success(movieDetails));

                return null;
            }

        });

        // start the activity and launch fragment
        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(detailPageFragment);
                    }
                });


        // try fetch again

        onView(withId(R.id.btn_try_again)).perform(click());

        // verify

        onView(withId(R.id.content)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.error_message)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));


    }


}


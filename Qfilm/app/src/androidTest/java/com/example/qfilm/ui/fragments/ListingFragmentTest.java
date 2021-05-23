package com.example.qfilm.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.data.models.entities.MediaType;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.repositories.utils.DataResource;

import com.example.qfilm.ui.utils.matchers.CustomMatchers;
import utils.MyTestViewModelFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import qfilm.TestFragmentActivity;
import utils.Mocks;
import utils.MyIdlingResource;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class ListingFragmentTest {

    private static final String TAG = "ListingFragmentTest";

    // tested
    ListingFragment listingFragment;

    // observable to return to fragment
    MutableLiveData<DataResource<List<Result>>> observableResults;

    // data to set in observable
    List<Result> results;


    @Before
    public void setUp(){

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

        listingFragment = new ListingFragment(MediaType.MOVIE, new MyTestViewModelFactory());

        observableResults = new MutableLiveData<>();

        // fake response from viewModel
        results = new ArrayList<>();

        for(int i = 0; i < 20; ++i){

            Result result = new Result();
            result.setTitle(String.valueOf(i));
            result.setPopularity((double) i);
            result.setResultId(i);
            results.add(result);
        }

        when(Mocks.resultWithGenreViewModelMock.getResultsObservable()).thenReturn(observableResults);

        // start the activity and launch fragment
        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(listingFragment);
                    }
                });

    }


    @After
    public void teardown(){

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());

        MyIdlingResource.reset();

        Mocks.reset();
    }



    @Test
    public void testVisibilities_SuccessResponse(){

        // setup mocked viewModel response

        when(Mocks.resultWithGenreViewModelMock.fetchResultsWithGenre(eq(MediaType.MOVIE), any(Genre.class), anyString()))
                .thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableResults.setValue(DataResource.success(results));

                return null;
            }

        });

        // verify

        onView(withId(R.id.btn_select_genre)).check(matches(withText(ApplicationProvider.getApplicationContext()
                .getString(R.string.all_genres))));

        onView(withId(R.id.rv_results)).check(matches(isDisplayed()));

        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(20)));


        // RecyclerView first displays 4 items and they should contain the following views
        List<Integer> viewIds = Arrays.asList(R.id.tv_title, R.id.tv_overview, R.id.iv_poster);

        for(int i = 0; i < 4; ++i) {

            onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher()
                    .itemAtPositionHasViews(i, viewIds)));
        }


    }


    @Test
    public void testVisibilities_loadingResponse(){

        // setup mocked viewModel response

        when(Mocks.resultWithGenreViewModelMock.fetchResultsWithGenre(eq(MediaType.MOVIE), any(Genre.class), anyString()))
                .thenAnswer(new Answer<Object>() {

                    @Override
                    public Object answer(InvocationOnMock invocation) {

                        observableResults.setValue(DataResource.loading(null));

                        return null;
                    }

                });

        // verify

        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()));

    }


    @Test
    public void testVisibilities_ErrorResponse_attachedOldData(){

        // setup mocked viewModel response

        when(Mocks.resultWithGenreViewModelMock.fetchResultsWithGenre(eq(MediaType.MOVIE), any(Genre.class), anyString()))
                .thenAnswer(new Answer<Object>() {

                    @Override
                    public Object answer(InvocationOnMock invocation) {

                        observableResults.setValue(DataResource.error(results, null));

                        return null;
                    }

                });


        // verify

        onView(withId(R.id.btn_select_genre)).check(matches(withText(ApplicationProvider.getApplicationContext()
        .getString(R.string.all_genres))));

        onView(withId(R.id.rv_results)).check(matches(isDisplayed()));

        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(20)));


        // RecyclerView first displays 4 items and they should contain the following views

        List<Integer> viewIds = Arrays.asList(R.id.tv_title, R.id.tv_overview, R.id.iv_poster);

        for(int i = 0; i < 4; ++i) {

            onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher()
                    .itemAtPositionHasViews(i, viewIds)));
        }


    }


       @Test
    public void testVisibilities_ErrorResponse_nullData(){

        // setup mocked viewModel response

        when(Mocks.resultWithGenreViewModelMock.fetchResultsWithGenre(eq(MediaType.MOVIE), any(Genre.class), anyString()))
                .thenAnswer(new Answer<Object>() {

                    @Override
                    public Object answer(InvocationOnMock invocation) {

                        observableResults.setValue(DataResource.error(null, null));

                        return null;
                    }

                });


        // verify

        onView(withId(R.id.btn_select_genre)).check(matches(withText(ApplicationProvider.getApplicationContext()
                .getString(R.string.all_genres))));

        onView(withId(R.id.error_message)).check(matches(isDisplayed()));

        onView(withId(R.id.tv_error_title)).check(matches(withText(
                ApplicationProvider.getApplicationContext().getString(R.string.error_unknown_heading))));

        onView(withId(R.id.tv_error_description)).check(matches(withText(
                ApplicationProvider.getApplicationContext().getString(R.string.error_unknown_message))));

        onView(withId(R.id.rv_results)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

    }


    @Test
    public void testVisibilities_ErrorResponse_tryAgain(){

        // setup mocked viewModel response

        when(Mocks.resultWithGenreViewModelMock.fetchResultsWithGenre(eq(MediaType.MOVIE), any(Genre.class), anyString()))
                .thenAnswer(new Answer<Object>() {

                    @Override
                    public Object answer(InvocationOnMock invocation) {

                        observableResults.setValue(DataResource.error(null, null));

                        return null;
                    }

                }).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableResults.setValue(DataResource.success(results));

                return null;
            }

        });

        onView(withId(R.id.btn_try_again)).perform(click());

        // verify

        onView(withId(R.id.btn_select_genre)).check(matches(withText(ApplicationProvider.getApplicationContext()
                .getString(R.string.all_genres))));

        onView(withId(R.id.rv_results)).check(matches(isDisplayed()));

        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(20)));


        // RecyclerView first displays 4 items and they should contain the following views
        List<Integer> viewIds = Arrays.asList(R.id.tv_title, R.id.tv_overview, R.id.iv_poster);

        for(int i = 0; i < 4; ++i) {

            onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher()
                    .itemAtPositionHasViews(i, viewIds)));
        }


    }


    @Test
    public void testVisibilities_pagination_successResponse(){

        // setup mocked viewModel response

        when(Mocks.resultWithGenreViewModelMock.fetchResultsWithGenre(eq(MediaType.MOVIE), any(Genre.class), anyString()))
                .thenAnswer(new Answer<Object>() {

                    @Override
                    public Object answer(InvocationOnMock invocation) {

                        observableResults.setValue(DataResource.success(results));

                        return null;
                    }

                });


        when(Mocks.resultWithGenreViewModelMock.fetchNextPage(eq(MediaType.MOVIE), any(Genre.class), anyString()))
                .thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                // results is affected in code, so re do the list

                results = new ArrayList<>();

                for(int i = 0; i < 20; ++i){

                    Result result = new Result();
                    result.setTitle(String.valueOf(i));
                    result.setPopularity((double) i);
                    result.setResultId(i);
                    results.add(result);
                }

                observableResults.setValue(DataResource.success(results));

                return null;
            }

        });

        // verify

        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(20)));


        // scroll down

        onView(withId(R.id.rv_results)).perform(actionOnItemAtPosition(19, scrollTo()));

        onView(withId(R.id.rv_results)).perform(actionOnItemAtPosition(19, scrollTo()));

        onView(withId(R.id.rv_results)).perform(ViewActions.swipeUp());

        // verify more items

        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(40)));


    }


    @Test
    public void testVisibilities_pagination_loadingResponse(){


        // setup mocked viewModel response first success then loading

        when(Mocks.resultWithGenreViewModelMock.fetchResultsWithGenre(eq(MediaType.MOVIE), any(Genre.class), anyString()))
                .thenAnswer(new Answer<Object>() {

                    @Override
                    public Object answer(InvocationOnMock invocation) {

                        observableResults.setValue(DataResource.success(results));

                        return null;
                    }

                });


        when(Mocks.resultWithGenreViewModelMock.fetchNextPage(eq(MediaType.MOVIE), any(Genre.class), anyString()))
                .thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                observableResults.setValue(DataResource.loading(null));
                return null;
            }
        });


        // verify

        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(20)));


        // scroll

        onView(withId(R.id.rv_results)).perform(actionOnItemAtPosition(19, scrollTo()));

        onView(withId(R.id.rv_results)).perform(ViewActions.swipeUp());


        // verify + 1 progressbar

        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(21)));

        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher()
                .itemAtPositionHasViews(20, Collections.singletonList(R.id.progress_bar_item))));




    }


    @Test
    public void testVisibilities_pagination_errorResponse_oldDataAttached(){

        // setup mocked viewModel response first success then error

        when(Mocks.resultWithGenreViewModelMock.fetchResultsWithGenre(eq(MediaType.MOVIE), any(Genre.class), anyString()))
                .thenAnswer(new Answer<Object>() {

                    @Override
                    public Object answer(InvocationOnMock invocation) {

                        observableResults.setValue(DataResource.success(results));

                        return null;
                    }

                });

        when(Mocks.resultWithGenreViewModelMock.fetchNextPage(eq(MediaType.MOVIE), any(Genre.class), anyString()))
        .thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                // results is affected in code, so re do the list

                results = new ArrayList<>();

                for(int i = 0; i < 20; ++i){

                    Result result = new Result();
                    result.setTitle(String.valueOf(i));
                    result.setPopularity((double) i);
                    result.setResultId(i);
                    results.add(result);
                }

                observableResults.setValue(DataResource.error(results, null));
                return null;
            }
        });


        // verify

        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(20)));

        // scrolling down to paginate

        onView(withId(R.id.rv_results)).perform(actionOnItemAtPosition(19, scrollTo()));

        onView(withId(R.id.rv_results)).perform(ViewActions.swipeUp());

        // verify that items are shown

        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(40)));


    }


    @Test
    public void testVisibilities_pagination_errorResponse_nullData(){

        // setup mocked viewModel response first success then error

        when(Mocks.resultWithGenreViewModelMock.fetchResultsWithGenre(eq(MediaType.MOVIE), any(Genre.class), anyString()))
                .thenAnswer(new Answer<Object>() {

                    @Override
                    public Object answer(InvocationOnMock invocation) {

                        observableResults.setValue(DataResource.success(results));

                        return null;
                    }

                });


        when(Mocks.resultWithGenreViewModelMock.fetchNextPage(eq(MediaType.MOVIE), any(Genre.class), anyString()))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        observableResults.setValue(DataResource.error(null, null));
                        return null;
                    }
                });



        // verify

        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(20)));

        // scrolling down to paginate

        onView(withId(R.id.rv_results)).perform(actionOnItemAtPosition(19, scrollTo()));

        onView(withId(R.id.rv_results)).perform(ViewActions.swipeUp());


        // verify that error section is shown and not recyclerView

        onView(withId(R.id.error_message)).check(matches(isDisplayed()));

        onView(withId(R.id.tv_error_title)).check(matches(withText(
                ApplicationProvider.getApplicationContext().getString(R.string.error_unknown_heading))));

        onView(withId(R.id.tv_error_description)).check(matches(withText(
                ApplicationProvider.getApplicationContext().getString(R.string.error_unknown_message))));

        onView(withId(R.id.rv_results)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));


    }


    @Test
    public void testVisibilities_pagination_errorResponse_tryAgain() throws InterruptedException {

        // setup mocked viewModel response first success then error


        when(Mocks.resultWithGenreViewModelMock.fetchResultsWithGenre(eq(MediaType.MOVIE), any(Genre.class), anyString()))
                .thenAnswer(new Answer<Object>() {

                    @Override
                    public Object answer(InvocationOnMock invocation) {

                        // results is affected in code, so re do the list

                        results = new ArrayList<>();

                        for(int i = 0; i < 20; ++i){

                            Result result = new Result();
                            result.setTitle(String.valueOf(i));
                            result.setPopularity((double) i);
                            result.setResultId(i);
                            results.add(result);
                        }

                        observableResults.setValue(DataResource.success(results));

                        return null;
                    }

                });


        when(Mocks.resultWithGenreViewModelMock.fetchNextPage(eq(MediaType.MOVIE), any(Genre.class), anyString()))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        
                        observableResults.setValue(DataResource.error(null, null));

                        return null;
                    }
                }).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                // results is affected in code, so re do the list

                results = new ArrayList<>();

                for(int i = 0; i < 20; ++i){

                    Result result = new Result();
                    result.setTitle(String.valueOf(i));
                    result.setPopularity((double) i);
                    result.setResultId(i);
                    results.add(result);
                }

                observableResults.setValue(DataResource.success(results));

                return null;
            }
        });

        // verify

        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(20)));

        // scrolling down to paginate

        onView(withId(R.id.rv_results)).perform(actionOnItemAtPosition(19, scrollTo()));

         onView(withId(R.id.rv_results)).perform(ViewActions.swipeUp());


        // click try again when error

        onView(withId(R.id.btn_try_again)).perform(click());

        // verify

        onView(withId(R.id.rv_results)).check(matches(isDisplayed()));


    }

}




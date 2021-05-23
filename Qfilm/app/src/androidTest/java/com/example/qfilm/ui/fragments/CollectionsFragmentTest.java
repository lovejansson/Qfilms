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

import com.example.qfilm.data.models.entities.Collection;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.ui.utils.matchers.CustomMatchers;
import utils.MyTestViewModelFactory;
import com.example.qfilm.viewmodels.FireStoreViewModel;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
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

import static org.mockito.Mockito.when;
import static utils.TestDummyData.COLLECTION;


public class CollectionsFragmentTest {

    // tested
    CollectionsFragment collectionsFragment;

    // observables to return when calling getObservable methods from viewModel
    private MutableLiveData<DataResource<List<Collection>>> observableCollections;
    private MutableLiveData<DataResource<FireStoreViewModel.FireStoreEdit>> observableFireStoreEdit;

    // return value when fetchCollections()
    private List<Collection> collections;



    @Before
    public void setup(){

        collectionsFragment = new CollectionsFragment(new MyTestViewModelFactory());

        collections = new ArrayList<>();

        collections.add(COLLECTION);

        observableCollections = new MutableLiveData<>();
        observableFireStoreEdit = new MutableLiveData<>();


        when(Mocks.fireStoreViewModelMock.getCollectionsObservable()).thenReturn(observableCollections);
        when(Mocks.fireStoreViewModelMock.getFireStoreEditsObservable()).thenReturn(observableFireStoreEdit);
        

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

    }


    @After
    public void tearDown(){

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());

        MyIdlingResource.reset();

        Mocks.reset();
    }


    @Test
    public void testFetchCollections_error(){

        // setup viewModel response

        when(Mocks.fireStoreViewModelMock.fetchCollections()).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableCollections.setValue(DataResource.error(null, null));

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
                        ((TestFragmentActivity)activity).startFragment(collectionsFragment);
                    }
                });

        // verify

        onView(withId(R.id.rv_collections)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.ll_no_saved_collections)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.error_message)).check(matches(isDisplayed()));

        onView(withId(R.id.tv_error_title)).check(matches(withText(ApplicationProvider.getApplicationContext().getString(R.string.error_unknown_heading))));

        onView(withId(R.id.tv_error_description)).check(matches(withText(ApplicationProvider.getApplicationContext().getString(R.string.error_unknown_message))));

    }


    @Test
    public void testFetchCollections_error_tryAgain(){

        // setup viewModel response

        when(Mocks.fireStoreViewModelMock.fetchCollections()).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableCollections.setValue(DataResource.error(null, null));

                return null;
            }

        }).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                observableCollections.setValue(DataResource.success(collections));

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
                        ((TestFragmentActivity)activity).startFragment(collectionsFragment);
                    }
                });

        // act press try again btn

        onView(withId(R.id.btn_try_again)).perform(click());

        // verify

        onView(withId(R.id.rv_collections)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.ll_no_saved_collections)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.error_message)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.rv_collections)).check(matches(CustomMatchers.recyclerViewMatcher()
                .itemAtPositionHasText(0, R.id.tv_name, String.valueOf("my collection"))));

        String moviesAndSeries = ApplicationProvider.getApplicationContext().getString(R.string.movies_and_series, 0);

        onView(withId(R.id.rv_collections)).check(matches(CustomMatchers.recyclerViewMatcher()
                .itemAtPositionHasText(0, R.id.tv_size, moviesAndSeries)));

        onView(withId(R.id.rv_collections)).check(matches(CustomMatchers.recyclerViewMatcher()
                .itemAtPositionHasViews(0, Arrays.asList(R.id.tv_name, R.id.tv_size, R.id.iv_poster_first_item))));

    }


    @Test
    public void testFetchCollections_success_noCollections(){

        // setup viewModel response

        when(Mocks.fireStoreViewModelMock.fetchCollections()).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableCollections.setValue(DataResource.success(new ArrayList<>()));

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
                        ((TestFragmentActivity)activity).startFragment(collectionsFragment);
                    }
                });


        // verify

        onView(withId(R.id.rv_collections)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.ll_no_saved_collections)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.error_message)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.tv_heading_no_saved_collections)).check(matches(withText(R.string.tv_heading_no_saved_collections)));

        onView(withId(R.id.tv_no_saved_collections)).check(matches(withText(R.string.tv_no_saved_collections)));


    }


    @Test
    public void testFetchCollections_success_hasCollections(){

        // setup viewModel response

        when(Mocks.fireStoreViewModelMock.fetchCollections()).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableCollections.setValue(DataResource.success(collections));

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
                        ((TestFragmentActivity)activity).startFragment(collectionsFragment);
                    }
                });


        // verify

        onView(withId(R.id.rv_collections)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.ll_no_saved_collections)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.error_message)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.rv_collections)).check(matches(CustomMatchers.recyclerViewMatcher()
                .itemAtPositionHasText(0, R.id.tv_name, String.valueOf("my collection"))));

       String moviesAndSeries = ApplicationProvider.getApplicationContext().getString(R.string.movies_and_series, 0);

        onView(withId(R.id.rv_collections)).check(matches(CustomMatchers.recyclerViewMatcher()
                .itemAtPositionHasText(0, R.id.tv_size, moviesAndSeries)));

        onView(withId(R.id.rv_collections)).check(matches(CustomMatchers.recyclerViewMatcher()
                .itemAtPositionHasViews(0, Arrays.asList(R.id.tv_name, R.id.tv_size,  R.id.iv_poster_first_item))));

    }

}

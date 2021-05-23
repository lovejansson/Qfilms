package com.example.qfilm.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;

import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.ui.utils.matchers.CustomMatchers;
import com.example.qfilm.viewmodels.FireStoreViewModel;

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
import utils.MyTestViewModelFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static utils.TestDummyData.COLLECTION;
import static utils.TestDummyData.RESULT_ONE;

public class CollectionFragmentTest {

    // tested
    CollectionFragment collectionFragment;

    // observables to return when calling getObservable methods from viewModel
    private MutableLiveData<DataResource<List<Result>>> observableCollectionItems;
    private MutableLiveData<DataResource<FireStoreViewModel.FireStoreEdit>> observableFireStoreEdit;

    // return value when fetchCollectionItems()
    private List<Result> collectionItems;



    @Before
    public void setup(){

        collectionFragment = new CollectionFragment(COLLECTION, new MyTestViewModelFactory());

        collectionItems = new ArrayList<>();

        collectionItems.add(RESULT_ONE);

        observableCollectionItems = new MutableLiveData<>();
        observableFireStoreEdit = new MutableLiveData<>();


        when(Mocks.fireStoreViewModelMock.getCollectionItemsObservable()).thenReturn(observableCollectionItems);
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
    public void fetchCollectionItems_emptyCollection(){

        // setup viewModel response

        when(Mocks.fireStoreViewModelMock.fetchCollectionItems(eq(COLLECTION.getDocumentId()), anyString())).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableCollectionItems.setValue(DataResource.success(new ArrayList<>()));

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
                        ((TestFragmentActivity)activity).startFragment(collectionFragment);
                    }
                });

        // verify

        onView(withId(R.id.tv_heading_empty_collection)).check(matches(withText(R.string.tv_heading_empty_collection)));
        onView(withId(R.id.tv_empty_collection)).check(matches(withText(R.string.tv_empty_collection)));
        onView(withId(R.id.rv_results)).check(matches(withEffectiveVisibility(GONE)));
        onView(withId(R.id.error_message)).check(matches(withEffectiveVisibility(GONE)));

    }


    @Test
    public void fetchCollectionItems_HasItemsInCollection(){

        // setup viewModel response

        when(Mocks.fireStoreViewModelMock.fetchCollectionItems(eq(COLLECTION.getDocumentId()), anyString())).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableCollectionItems.setValue(DataResource.success(collectionItems));

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
                        ((TestFragmentActivity)activity).startFragment(collectionFragment);
                    }
                });

        // verify

        onView(withId(R.id.ll_empty_collection)).check(matches(withEffectiveVisibility(GONE)));
        onView(withId(R.id.error_message)).check(matches(withEffectiveVisibility(GONE)));

        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemAtPositionHasText(0, R.id.tv_title, RESULT_ONE.getTitle())));
        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemAtPositionHasText(0, R.id.tv_overview, RESULT_ONE.getOverview())));

    }

    @Test
    public void fetchCollectionItems_errorAndTryAgain(){

        // setup viewModel response

        when(Mocks.fireStoreViewModelMock.fetchCollectionItems(eq(COLLECTION.getDocumentId()), anyString())).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableCollectionItems.setValue(DataResource.error(null, null));

                return null;
            }

        }).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableCollectionItems.setValue(DataResource.success(collectionItems));

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
                        ((TestFragmentActivity)activity).startFragment(collectionFragment);
                    }
                });

        // verify

        // error message shown
        onView(withId(R.id.ll_empty_collection)).check(matches(withEffectiveVisibility(GONE)));
        onView(withId(R.id.error_message)).check(matches(isDisplayed()));
        onView(withId(R.id.rv_results)).check(matches(withEffectiveVisibility(GONE)));

        // click try again
        onView(withId(R.id.btn_try_again)).perform(click());

        // items are shown

        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemAtPositionHasText(0, R.id.tv_title, RESULT_ONE.getTitle())));
        onView(withId(R.id.rv_results)).check(matches(CustomMatchers.recyclerViewMatcher().itemAtPositionHasText(0, R.id.tv_overview, RESULT_ONE.getOverview())));

    }

}

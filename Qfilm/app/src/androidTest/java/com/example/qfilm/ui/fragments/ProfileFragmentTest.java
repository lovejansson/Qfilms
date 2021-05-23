package com.example.qfilm.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.assertion.ViewAssertions;

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
import java.util.List;

import qfilm.TestFragmentActivity;
import utils.Mocks;
import utils.MyIdlingResource;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.mockito.Mockito.when;
import static utils.TestDummyData.COLLECTION;

public class ProfileFragmentTest {

    private static final String TAG = "ProfileFragmentTest";

    // tested
    private ProfileFragment profileFragment;

    // observables to return when calling getObservable methods from viewModel
    private MutableLiveData<DataResource<FireStoreViewModel.FireStoreEdit>> observableFireStoreEdit;
    private MutableLiveData<DataResource<List<Collection>>> observableCollections;

    // return value when fetchCollections()
    private List<Collection> collections;


    @Before
    public void setUp(){

        CollectionsFragment collectionsFragment = new CollectionsFragment(new MyTestViewModelFactory());

        profileFragment = new ProfileFragment(Mocks.firebaseAuthMock, collectionsFragment);

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

        collections = new ArrayList<>();

        collections.add(COLLECTION);

        setupMocks();

        Intent intent = Intent.makeMainActivity(new ComponentName(ApplicationProvider.getApplicationContext(),
                TestFragmentActivity.class));

        ActivityScenario.launch(intent)
                .onActivity(new ActivityScenario.ActivityAction() {
                    @Override
                    public void perform(Activity activity) {
                        ((TestFragmentActivity)activity).startFragment(profileFragment);
                    }
                });

    }


    @After
    public void teardown(){

        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());

        Mocks.reset();
    }


    private void setupMocks() {

        when(Mocks.firebaseAuthMock.getCurrentUser()).thenReturn(Mocks.firebaseUserMock);

        when(Mocks.firebaseUserMock.getDisplayName()).thenReturn("Love");


        observableFireStoreEdit = new MutableLiveData<>();

        observableCollections = new MutableLiveData<>();


        when(Mocks.fireStoreViewModelMock.getFireStoreEditsObservable()).thenReturn(observableFireStoreEdit);

        when(Mocks.fireStoreViewModelMock.getCollectionsObservable()).thenReturn(observableCollections);


        when(Mocks.fireStoreViewModelMock.fetchCollections()).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableCollections.setValue(DataResource.success(collections));

                return null;
            }

        });

    }


    @Test
    public void testVisibilities_startUp(){

        onView(withId(R.id.tv_username)).check(ViewAssertions.matches(withText("Love")));

        onView(withId(R.id.rv_collections)).check(ViewAssertions.matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(1)));

        onView(withId(R.id.rv_collections)).check(ViewAssertions.matches(CustomMatchers.recyclerViewMatcher().itemAtPositionHasText(0, R.id.tv_name, "my collection")));

        String moviesAndSeries = ApplicationProvider.getApplicationContext().getString(R.string.movies_and_series, 0);

        onView(withId(R.id.rv_collections)).check(ViewAssertions.matches(CustomMatchers.recyclerViewMatcher().itemAtPositionHasText(0, R.id.tv_size, moviesAndSeries)));

    }


    @Test
    public void testCreateNewCollection_success(){

        // setup mocks

        Collection collection = new Collection("new collection", 0,
                null, "id", (int) System.currentTimeMillis());


        when(Mocks.fireStoreViewModelMock.createNewCollection("new collection", null))
                .thenReturn(collection);

        when(Mocks.fireStoreViewModelMock.addNewCollection(collection, null)).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableFireStoreEdit.setValue(DataResource.success(FireStoreViewModel.FireStoreEdit.ADD_EMPTY_COLLECTION));

                return null;
            }

        });


        // act

        onView(withId(R.id.btn_new_collection)).perform(click());

        onView(withId(R.id.et_collection_name)).perform(typeText("new collection"));

        onView(withId(R.id.btn_save)).perform(click());


        // verify that the new collection item is present in recyclerView

        onView(withId(R.id.rv_collections)).check(ViewAssertions.matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(2)));

        onView(withId(R.id.rv_collections)).check(ViewAssertions.matches(CustomMatchers.recyclerViewMatcher()
               .itemAtPositionHasText(1, R.id.tv_name, "new collection")));

    }


    @Test
    public void testCreateNewCollection_cancel(){


        // act

        onView(withId(R.id.btn_new_collection)).perform(click());

        onView(withId(R.id.et_collection_name)).perform(typeText("new collection"));

        onView(withId(R.id.btn_cancel)).perform(click());


        // verify not added

        onView(withId(R.id.rv_collections)).check(ViewAssertions.matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(1)));

    }


    @Test
    public void testCreateNewCollection_error(){

        // setup specific mocks for this procedure

        Collection collection = new Collection("new collection", 0,
                 null, "id", (int) System.currentTimeMillis());


        when(Mocks.fireStoreViewModelMock.createNewCollection("new collection", null))
                .thenReturn(collection);

        when(Mocks.fireStoreViewModelMock.addNewCollection(collection, null)).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableFireStoreEdit.setValue(DataResource.error(FireStoreViewModel.FireStoreEdit.ADD_EMPTY_COLLECTION, null));

                return null;
            }

        });


        // act

        onView(withId(R.id.btn_new_collection)).perform(click());

        onView(withId(R.id.et_collection_name)).perform(typeText("new collection"));

        onView(withId(R.id.btn_save)).perform(click());


        // verify that the new collection item isn't present in RecyclerView and snackBar is shown

        onView(withId(R.id.rv_collections)).check(ViewAssertions.matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(1)));

        String snackBarMessage = ApplicationProvider.getApplicationContext().getString(R.string.create_empty_collection_failed, "new collection");

        onView(withText(snackBarMessage)).check(ViewAssertions.matches(isDisplayed()));


    }


    @Test
    public void testDeleteCollection_success(){

        // setup mocks

        when(Mocks.fireStoreViewModelMock.deleteCollection(COLLECTION)).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableFireStoreEdit.setValue(DataResource.success(FireStoreViewModel.FireStoreEdit.DELETE_COLLECTION));

                return null;
            }

        });


        // act

        onView(withId(R.id.btn_action)).perform(click());

        onView(withId(R.id.tv_btn_delete_collection)).perform(click());

        onView(withId(R.id.btn_ok)).perform(click());


        // verify that the initial collection is deleted

        onView(withId(R.id.rv_collections)).check(ViewAssertions.matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(0)));


    }


    @Test
    public void testDeleteCollection_cancel(){

        // act

        onView(withId(R.id.btn_action)).perform(click());

        onView(withId(R.id.tv_btn_delete_collection)).perform(click());

        onView(withId(R.id.btn_cancel)).perform(click());

        // verify that the initial collection isn't deleted

        onView(withId(R.id.rv_collections)).check(ViewAssertions.matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(1)));


    }


    @Test
    public void testDeleteCollection_error(){

        // setup specific mocks for this procedure

        when(Mocks.fireStoreViewModelMock.deleteCollection(COLLECTION)).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableFireStoreEdit.setValue(DataResource.error(FireStoreViewModel.FireStoreEdit.DELETE_COLLECTION, null));

                return null;
            }

        });


        // act

        onView(withId(R.id.btn_action)).perform(click());

        onView(withId(R.id.tv_btn_delete_collection)).perform(click());

        onView(withId(R.id.btn_ok)).perform(click());

        // verify that the initial collection isn't deleted and snackBar is shown

        onView(withId(R.id.rv_collections)).check(ViewAssertions.matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(1)));

        String snackBarMessage = ApplicationProvider.getApplicationContext().getString(R.string.delete_collection_failed, "my collection");

        onView(withText(snackBarMessage)).check(ViewAssertions.matches(isDisplayed()));

    }


    @Test
    public void testUpdateCollectionName_success(){

        // setup specific mocks for this procedure

        when(Mocks.fireStoreViewModelMock.changeCollectionName(COLLECTION, "new name")).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableFireStoreEdit.setValue(DataResource.success(FireStoreViewModel.FireStoreEdit.UPDATE_COLLECTION_NAME));

                return null;
            }

        });


        // act

        onView(withId(R.id.btn_action)).perform(click());

        onView(withId(R.id.tv_btn_edit_collection_name)).perform(click());

        onView(withId(R.id.et_collection_name)).perform(typeText("new name"));

        onView(withId(R.id.btn_save)).perform(click());

        // verify that the initial collection has new name

        onView(withId(R.id.rv_collections)).check(ViewAssertions.matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(1)));

        onView(withId(R.id.rv_collections)).check(ViewAssertions.matches(CustomMatchers.recyclerViewMatcher()
                .itemAtPositionHasText(0, R.id.tv_name, "new name")));

    }


    @Test
    public void testUpdateCollectionName_error(){

        // setup specific mocks for this procedure

        when(Mocks.fireStoreViewModelMock.changeCollectionName(COLLECTION, "new name")).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                observableFireStoreEdit.setValue(DataResource.error(FireStoreViewModel.FireStoreEdit.UPDATE_COLLECTION_NAME, null));

                return null;
            }

        });


        // act

        onView(withId(R.id.btn_action)).perform(click());

        onView(withId(R.id.tv_btn_edit_collection_name)).perform(click());

        onView(withId(R.id.et_collection_name)).perform(typeText("new name"));

        onView(withId(R.id.btn_save)).perform(click());


        // verify that the initial collection has the same name as before and snackBar is shown

        onView(withId(R.id.rv_collections)).check(ViewAssertions.matches(CustomMatchers.recyclerViewMatcher().itemCountMatcher(1)));

        onView(withId(R.id.rv_collections)).check(ViewAssertions.matches(CustomMatchers.recyclerViewMatcher()
                .itemAtPositionHasText(0, R.id.tv_name, "my collection")));

        String snackBarMessage = ApplicationProvider.getApplicationContext().getString(R.string.update_collection_name_failed);

        onView(withText(snackBarMessage)).check(ViewAssertions.matches(isDisplayed()));

    }

}

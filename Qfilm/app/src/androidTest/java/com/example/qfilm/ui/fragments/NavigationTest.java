package com.example.qfilm.ui.fragments;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.NoActivityResumedException;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.example.qfilm.R;
import com.example.qfilm.TestComponentRule;
import com.example.qfilm.data.models.entities.Collection;
import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.data.models.entities.MediaType;
import com.example.qfilm.data.models.entities.MovieDetails;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.ui.activities.MainActivity;
import com.example.qfilm.ui.utils.matchers.CustomMatchers;
import com.example.qfilm.viewmodels.FireStoreViewModel;
import com.example.qfilm.viewmodels.FirebaseAuthViewModel;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import utils.Mocks;
import utils.MyIdlingResource;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static utils.TestDummyData.COLLECTION;
import static utils.TestDummyData.DRAMA;
import static utils.TestDummyData.GENRE_NAME_DRAMA;
import static utils.TestDummyData.ID_ONE;
import static utils.TestDummyData.MOVIE_DETAILS_ONE;
import static utils.TestDummyData.RESULT_ONE;
import static utils.TestDummyData.TITLE_ONE;


public class NavigationTest {


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule =
            new InstantTaskExecutorRule();

    private static final String TAG = "NavigationTest";

    // observables to return to fragments from view models
    private MutableLiveData<DataResource<List<Result>>> resultsObservable;
    private MutableLiveData<DataResource<List<Genre>>> genresObservable;
    private MutableLiveData<DataResource<MovieDetails>> detailsObservable;
    private MutableLiveData<DataResource<FirebaseAuthViewModel.AuthOperation>> authOperationObservable;
    private MutableLiveData<DataResource<FireStoreViewModel.FireStoreEdit>> fireStoreEditObservable;
    private MutableLiveData<DataResource<List<Collection>>> collectionsObservable;

    // dagger component that uses mocks instead of real things
    @Rule
    public final TestComponentRule component =
            new TestComponentRule(ApplicationProvider.getApplicationContext());


    @Before
    public void setUp(){

        // liveData's

        resultsObservable = new MutableLiveData<>();
        genresObservable = new MutableLiveData<>();
        detailsObservable = new MutableLiveData<>();
        authOperationObservable = new MutableLiveData<>();
        collectionsObservable = new MutableLiveData<>();
        fireStoreEditObservable = new MutableLiveData<>();


        when(Mocks.resultWithGenreViewModelMock.getResultsObservable()).thenReturn(resultsObservable);
        when(Mocks.resultWithGenreViewModelMock.getGenresObservable()).thenReturn(genresObservable);
        when(Mocks.detailsViewModelMock.getMovieDetailsLiveData()).thenReturn(detailsObservable);
        when(Mocks.firebaseAuthViewModelMock.getAuthOperation()).thenReturn(authOperationObservable);
        when(Mocks.algoliaSearchViewModelMock.getQueryResult()).thenReturn(resultsObservable);
        when(Mocks.fireStoreViewModelMock.getCollectionsObservable()).thenReturn(collectionsObservable);
        when(Mocks.fireStoreViewModelMock.getCollectionItemsObservable()).thenReturn(resultsObservable);
        when(Mocks.fireStoreViewModelMock.getFireStoreEditsObservable()).thenReturn(fireStoreEditObservable);


        // setting return values when data is asked for

        when(Mocks.resultWithGenreViewModelMock.fetchResultsWithGenre(any(MediaType.class), any(Genre.class), anyString()))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        resultsObservable.setValue(DataResource.success(
                                Collections.singletonList(RESULT_ONE)));
                        return null;
                    }
                });


        when(Mocks.resultWithGenreViewModelMock.fetchGenres(eq(MediaType.MOVIE), anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                genresObservable.setValue(DataResource.success(new ArrayList<>(Collections.singleton(DRAMA))));
                return null;
            }
        });

        when(Mocks.detailsViewModelMock.getMovieDetails(eq(ID_ONE), anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                detailsObservable.setValue(DataResource.success(MOVIE_DETAILS_ONE));
                return null;
            }
        });

        when(Mocks.fireStoreViewModelMock.fetchCollections()).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {

                collectionsObservable.setValue(DataResource.success(new ArrayList<>(Collections.singletonList(COLLECTION))));

                return null;
            }

        });

        when(Mocks.fireStoreViewModelMock.fetchCollectionItems(anyString(), anyString())).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        resultsObservable.setValue(DataResource.success(
                                Collections.singletonList(RESULT_ONE)));
                        return null;
                    }
                }
        );


        IdlingRegistry.getInstance().register(MyIdlingResource.getInstance());


        ActivityScenario<MainActivity> mainActivityScenario = ActivityScenario.launch(MainActivity.class);

    }


    @After
    public void teardown(){

        IdlingRegistry.getInstance().unregister(MyIdlingResource.getInstance());

        MyIdlingResource.reset();

        Mocks.reset();
    }



    @Test
    public void navigation_bottomNavigationView(){

        List<Integer> ids = Arrays.asList(R.id.bottom_nav_item_home, R.id.bottom_nav_item_search, R.id.bottom_nav_item_user);

        Context context = ApplicationProvider.getApplicationContext();

        List<String> labels = Arrays.asList(context.getString(R.string.bottom_nav_item_home),
                context.getString(R.string.bottom_nav_item_search),
                context.getString(R.string.bottom_nav_item_profile));

        onView(withId(R.id.bottom_navigation)).check(matches(CustomMatchers.bottomNavigationViewContentMatcher(ids, labels)));

        // Home is checked at start
        onView(withId(R.id.bottom_navigation)).check(matches(CustomMatchers.isCheckedInBottomNavigationView(0)));

        // no user sign in status changes
        when(Mocks.firebaseAuthMock.getCurrentUser()).thenReturn(null)
                .thenReturn(Mocks.firebaseUserMock);

        // click user
        onView(withId(R.id.bottom_nav_item_user)).perform(click());

        // user is checked
        onView(withId(R.id.bottom_navigation)).check(matches(CustomMatchers.isCheckedInBottomNavigationView(2)));

        // Sign in fragment is displayed
        onView(withId(R.id.fragment_sign_in)).check(matches(isDisplayed()));

        // click search
        onView(withId(R.id.bottom_nav_item_search)).perform(click());

        // search is checked
        onView(withId(R.id.bottom_navigation)).check(matches(CustomMatchers.isCheckedInBottomNavigationView(1)));

        // search fragment is displayed
        onView(withId(R.id.fragment_search)).check(matches(isDisplayed()));

        // now user is signed in with google so MainActivity navigates to profile fragment

        List mockedProvidersList =  Mockito.mock(List.class);
        UserInfo userInfoMock = Mockito.mock(UserInfo.class);


        when(Mocks.firebaseUserMock.getProviderData()).thenReturn(mockedProvidersList);
        when(mockedProvidersList.get(1)).thenReturn(userInfoMock);
        when(userInfoMock.getProviderId()).thenReturn(GoogleAuthProvider.PROVIDER_ID);
        when(Mocks.firebaseUserMock.getDisplayName()).thenReturn("Love");


        // click user
        onView(withId(R.id.bottom_nav_item_user)).perform(click());

        // profile fragment is displayed
        onView(withId(R.id.fragment_profile)).check(matches(isDisplayed()));


    }


    @Test
    public void navigateToProfile_thenSignOut(){

        // user is signed in until they presses sign out

        when(Mocks.firebaseAuthMock.getCurrentUser())
                .thenReturn(Mocks.firebaseUserMock)
                .thenReturn(Mocks.firebaseUserMock)
                .thenReturn(Mocks.firebaseUserMock)
                .thenReturn(Mocks.firebaseUserMock)
                .thenReturn(Mocks.firebaseUserMock)
                .thenReturn(Mocks.firebaseUserMock)
                .thenReturn(Mocks.firebaseUserMock)
                .thenReturn(null);



        List mockedProvidersList =  Mockito.mock(List.class);
        UserInfo userInfoMock = Mockito.mock(UserInfo.class);


        when(Mocks.firebaseUserMock.getProviderData()).thenReturn(mockedProvidersList);
        when(mockedProvidersList.get(1)).thenReturn(userInfoMock);
        when(userInfoMock.getProviderId()).thenReturn(GoogleAuthProvider.PROVIDER_ID);
        when(Mocks.firebaseUserMock.getDisplayName()).thenReturn("Love");

        // click user
        onView(withId(R.id.bottom_nav_item_user)).perform(click());

        // profile fragment is displayed
        onView(withId(R.id.fragment_profile)).check(matches(isDisplayed()));

        // now log out and make sure that sign in fragment is shown again and navigation works

        // click settings

        onView(withId(R.id.btn_settings)).perform(click());

        //sign out

        // click sign out

        onView(withId(R.id.btn_sign_out)).perform(click());

        // sign in fragment is displayed
        onView(withId(R.id.fragment_sign_in)).check(matches(isDisplayed()));

        // make sure navigation works properly, special handling for when user auth state changes

        // click search

        onView(withId(R.id.bottom_nav_item_search)).perform(click());

        // click user again

        onView(withId(R.id.bottom_nav_item_user)).perform(click());

        // sign in fragment is displayed
        onView(withId(R.id.fragment_sign_in)).check(matches(isDisplayed()));

        Espresso.pressBack();

        // search is displayed
        onView(withId(R.id.fragment_search)).check(matches(isDisplayed()));

        Espresso.pressBack();

        // home is displayed
        onView(withId(R.id.fragment_home)).check(matches(isDisplayed()));


    }


    @Test
    public void navigation_backStack(){

        /**
        *
        * If a destination is already in the back stack and added again,
        * the previous entry is removed so that the user don't get stuck in a weird loop
        * between fragments if they have pressed the bottom navigation icons a few times
        * and then starts to use the built in back button.
         *
         *
         * Navigation path before popping back stack: home -> search -> profile -> search -> profile
         *
         * before navigating back, backstack should look like: home -> search -> profile
         *
        **/


        // no user is logged in
        when(Mocks.firebaseAuthMock.getCurrentUser()).thenReturn(null);


        // click search
        onView(withId(R.id.bottom_nav_item_search)).perform(click());
        onView(withId(R.id.fragment_search)).check(matches(isDisplayed()));

        // click user
        onView(withId(R.id.bottom_nav_item_user)).perform(click());
        onView(withId(R.id.fragment_sign_in)).check(matches(isDisplayed()));

        // click search
        onView(withId(R.id.bottom_nav_item_search)).perform(click());
        onView(withId(R.id.fragment_search)).check(matches(isDisplayed()));

        // click user
        onView(withId(R.id.bottom_nav_item_user)).perform(click());
        onView(withId(R.id.fragment_sign_in)).check(matches(isDisplayed()));


        // start to navigate back

        Espresso.pressBack();

        // search is displayed
        onView(withId(R.id.fragment_search)).check(matches(isDisplayed()));

        Espresso.pressBack();

        // home is displayed
        onView(withId(R.id.fragment_home)).check(matches(isDisplayed()));


        // app is killed

        try {
            Espresso.pressBack();
            fail("Should have thrown NoActivityResumedException");

        }catch (NoActivityResumedException noActivityResumedException){
        }

    }


    @Test
    public void innerNavigation_AuthenticationFragments() {

        /**
         navigation path: SignInFragment -> SettingsActivity -> SignInFragment ->
         SignInEmailFragment -> ResetPasswordFragment -> SignInEmailFragment ->
         CreateAccountFragment -> SignInEmailFragment -> SignInFragment

         **/

        // no user is logged in
        when(Mocks.firebaseAuthMock.getCurrentUser()).thenReturn(null);

        onView(withId(R.id.bottom_nav_item_user)).perform(click());

        onView(withId(R.id.fragment_sign_in)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_settings)).perform(click());

        onView(withId(R.id.activity_settings)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_navigate_back)).perform(click());

        onView(withId(R.id.fragment_sign_in)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_sign_in_email)).perform(click());

        onView(withId(R.id.fragment_sign_in_email)).check(matches(isDisplayed()));

        onView(withId(R.id.tv_forgot_your_password)).perform(click());

        onView(withId(R.id.fragment_reset_password)).check(matches(isDisplayed()));

        if(ApplicationProvider.getApplicationContext().getResources().getBoolean(R.bool.is_tablet)){
            onView(withId(R.id.btn_close_reset_password)).perform(click());

        }
        else {
            onView(withId(R.id.btn_navigate_back)).perform(click());
        }

        onView(withId(R.id.fragment_sign_in_email)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_create_account)).perform(click());

        onView(withId(R.id.fragment_create_account)).check(matches(isDisplayed()));

        if(ApplicationProvider.getApplicationContext().getResources().getBoolean(R.bool.is_tablet)){
            onView(withId(R.id.btn_close_create_account)).perform(click());

        }
        else {
            onView(withId(R.id.btn_navigate_back)).perform(click());
        }

        onView(withId(R.id.fragment_sign_in_email)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_navigate_back)).perform(click());

        onView(withId(R.id.fragment_sign_in)).check(matches(isDisplayed()));

    }



    @Test
    public void innerNavigation_ListingFragment_DetailPage_TrailerFragment(){


        // navigate to DetailPageFragment via item in RecyclerView
        onView(withId(R.id.rv_results)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // verify
        onView(withId(R.id.fragment_details_page)).check(matches(isDisplayed()));

        // correct movie is displayed
        onView(withId(R.id.tv_title)).check(matches(withText(TITLE_ONE)));

        // navigate to TrailerFragment
        onView(withId(R.id.btn_trailer)).perform(click());

        //verify
        onView(withId(R.id.fragment_trailer)).check(matches(isDisplayed()));

        // navigate back to DetailPageFragment
        onView(withId(R.id.btn_close_trailer)).perform(click());

        //verify
        onView(withId(R.id.fragment_details_page)).check(matches(isDisplayed()));

        // navigate back to ListingFragment with close icon

        onView(withId(R.id.iv_close_detail_page)).perform(click());

        // verify
        onView(withId(R.id.fragment_listing)).check(matches(isDisplayed()));


    }


    @Test
    public void innerNavigation_ListingFragment_DialogFragmentGenres()  {

        // clicking select genre button and verifies that DialogFragmentGenres is displayed

        onView(withId(R.id.btn_select_genre)).perform(click());

        onView(withId(R.id.dialog_fragment_genres)).check(matches(isDisplayed()));

        // chooses first genre (Drama), should trigger navigation back to ListingFragment with the
        // new genre as argument

        onView(withId(R.id.rv_genres)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.fragment_listing)).check(matches(isDisplayed()));

        // ListingFragment is fetching data for new genre drama and updating text in button

        onView(withId(R.id.btn_select_genre)).check(matches(withText(GENRE_NAME_DRAMA)));

    }


    @Test
    public void innerNavigation_ListingFragment_DialogFragmentGenres_noGenreChosen() {


        // clicking select genre button and verifies that DialogFragmentGenres is displayed

        onView(withId(R.id.btn_select_genre)).perform(click());

        onView(withId(R.id.dialog_fragment_genres)).check(matches(isDisplayed()));

        // exits dialog

        onView(withId(R.id.btn_close_dialog)).perform(click());

        onView(withId(R.id.fragment_listing)).check(matches(isDisplayed()));

        // ListingFragment is unchanged

        onView(withId(R.id.btn_select_genre)).check(matches(withText(ApplicationProvider.getApplicationContext()
        .getString(R.string.all_genres))));

    }


    @Test
    public void innerNavigation_ProfileFragment_CollectionFragment_DetailPageFragment(){

        // user is signed in
        when(Mocks.firebaseAuthMock.getCurrentUser())
                .thenReturn(Mocks.firebaseUserMock);

        List mockedProvidersList =  Mockito.mock(List.class);
        UserInfo userInfoMock = Mockito.mock(UserInfo.class);

        when(Mocks.firebaseUserMock.getProviderData()).thenReturn(mockedProvidersList);
        when(mockedProvidersList.get(1)).thenReturn(userInfoMock);
        when(userInfoMock.getProviderId()).thenReturn(GoogleAuthProvider.PROVIDER_ID);
        when(Mocks.firebaseUserMock.getDisplayName()).thenReturn("Love");

        // click user
        onView(withId(R.id.bottom_nav_item_user)).perform(click());

        // click first collection
        onView(withId(R.id.rv_collections)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Collection fragment is displayed
        onView(withId(R.id.fragment_collection)).check(matches(isDisplayed()));

        // click first item
        onView(withId(R.id.rv_results)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //verify
        onView(withId(R.id.fragment_details_page)).check(matches(isDisplayed()));

    }


}



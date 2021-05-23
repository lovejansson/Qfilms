package com.example.qfilm.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.qfilm.MainApplication;
import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.Collection;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.data.models.entities.Video;

import com.example.qfilm.ui.fragments.CollectionFragment;
import com.example.qfilm.ui.fragments.SearchFragment;
import com.example.qfilm.ui.utils.MyFragmentFactory;
import com.example.qfilm.ui.utils.UiUtil;
import com.example.qfilm.ui.utils.navigation.NavigationInterface;
import com.example.qfilm.ui.fragments.CreateAccountFragment;
import com.example.qfilm.ui.fragments.DetailPageFragment;
import com.example.qfilm.ui.fragments.HomeFragment;
import com.example.qfilm.ui.fragments.ProfileFragment;
import com.example.qfilm.ui.fragments.ResetPasswordFragment;
import com.example.qfilm.ui.fragments.SignInEmailFragment;
import com.example.qfilm.ui.fragments.SignInFragment;
import com.example.qfilm.ui.fragments.TrailerFragment;
import com.example.qfilm.ui.utils.navigation.NavigationUtil;
import com.example.qfilm.utils.Constants;
import com.example.qfilm.ui.utils.SettingsManager;
import com.example.qfilm.viewmodels.utils.MyViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import static android.view.View.GONE;

/**
 *
 * MainActivity implements NavigationInterface via which fragments are navigating, uses NavigationUtil to
 * perform appropriate fragment transactions, listening for settings changes and
 * initializing variables from Remote Config.
 *
 * **/

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener
        , NavigationInterface, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigationView;

    private NavigationUtil navigationUtil;

    private HomeFragment homeFragment;

    private ProfileFragment profileFragment;

    private SignInFragment signInFragment;

    private DetailPageFragment detailPageFragment;

    private Fragment currentTabFragment;

    private CollectionFragment collectionFragment;

    private List<String> changedSettings;

    private SharedPreferences sharedPreferences;

    private String currentLanguage;

    private SearchFragment searchFragment;

    @Inject
    public FirebaseAuth firebaseAuth;

    @Inject
    public MyViewModelFactory myViewModelFactory;

    @Inject
    public  MyFragmentFactory myFragmentFactory;


    // adjust configuration according to the language the user has selected
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(SettingsManager.setAppLanguage(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ((MainApplication)getApplication()).getApplicationComponent().inject(this);

        getSupportFragmentManager().setFragmentFactory(myFragmentFactory);

        super.onCreate(savedInstanceState);

        // values from constants comes from remote config variables
        try {
            Constants.initializeConstants();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // member variables

        String language = getSharedPreferences("com.example.qfilm", MODE_PRIVATE).getString("language", "English");

        currentLanguage = language.substring(0, 2).toLowerCase();

        changedSettings = new ArrayList<>();

        sharedPreferences = getSharedPreferences("com.example.qfilm", MODE_PRIVATE);

        navigationUtil = new NavigationUtil(getSupportFragmentManager(), Arrays.asList(HomeFragment.class, ProfileFragment.class,
                CollectionFragment.class, SignInFragment.class));

        SettingsManager.setTheme(this);

        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // if this is start of MainActivity first time and not due to changes in settings
        if(!getIntent().getBooleanExtra("settings_has_changed", false)){
            
            FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .build();

            firebaseRemoteConfig.setConfigSettingsAsync(configSettings);

            // activates previous fetched variables and fetches new ones

            firebaseRemoteConfig.activate();

            firebaseRemoteConfig.fetch();
            
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        navigateToHomeFragment();

        // navigates to user tab if restart was due to changes in settings
        if(getIntent().getBooleanExtra("settings_has_changed", false)){

            bottomNavigationView.setSelectedItemId(R.id.bottom_nav_item_user);
        }

    }


    private void navigateToHomeFragment(){

        if(!(currentTabFragment instanceof HomeFragment)) {

            if (homeFragment == null) {

                homeFragment = (HomeFragment) createFragment(HomeFragment.class.getSimpleName());

                navigationUtil.addFragment(homeFragment, R.anim.fade_in, R.anim.fade_out);
            } else {

                navigationUtil.detachAndAttachFragments(homeFragment, R.anim.fade_in, R.anim.fade_out,
                        R.anim.fade_in, R.anim.fade_out);

            }

            currentTabFragment = homeFragment;

            bottomNavigationView.setVisibility(View.VISIBLE);

        }

    }


    @Override
    public void navigateToDetailPageFragment(Result result) {

        ((MyFragmentFactory) getSupportFragmentManager().getFragmentFactory()).setResult(result);

        detailPageFragment = (DetailPageFragment) createFragment(DetailPageFragment.class.getSimpleName());

        // detail page is shown as dialog on tablet

        if(getResources().getBoolean(R.bool.is_tablet)){


                detailPageFragment.show(getSupportFragmentManager(), null);


        }else{

            navigationUtil.detachAndAddFragments(detailPageFragment,
                    R.anim.slide_up, R.anim.fade_out, R.anim.fade_in, R.anim.slide_down_out);

             bottomNavigationView.setVisibility(GONE);

        }
    }


    @Override
    public void navigateToTrailerFragment(Video video) {

        ((MyFragmentFactory) getSupportFragmentManager().getFragmentFactory()).setVideo(video);

        TrailerFragment trailerFragment = (TrailerFragment) createFragment(TrailerFragment.class.getSimpleName());

        if(getResources().getBoolean(R.bool.is_tablet)){



                trailerFragment.show(getSupportFragmentManager(), null);

        }else{

            navigationUtil.detachAndAddFragments(trailerFragment, R.anim.fade_in,
                    R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        }

    }


    @Override
    public void navigateToCreateAccountFragment() {

        CreateAccountFragment createAccountFragment = (CreateAccountFragment)createFragment(CreateAccountFragment.class.getSimpleName());

        if(getResources().getBoolean(R.bool.is_tablet)) {

            createAccountFragment.show(getSupportFragmentManager(), null);

        }else{

            navigationUtil.replaceFragment(createAccountFragment, R.anim.slide_in_right,
                    R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_right);

            bottomNavigationView.setVisibility(GONE);

        }

    }


    @Override
    public void navigateToSignInEmailFragment() {

       SignInEmailFragment signInEmailFragment = (SignInEmailFragment) createFragment(SignInEmailFragment.class.getSimpleName());

        navigationUtil.replaceFragment(signInEmailFragment, R.anim.slide_in_right,
                R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_right);

        bottomNavigationView.setVisibility(GONE);
    }


    @Override
    public void navigateToResetPasswordFragment() {

        ResetPasswordFragment resetPasswordFragment = (ResetPasswordFragment) createFragment(ResetPasswordFragment.class.getSimpleName());

        if(getResources().getBoolean(R.bool.is_tablet)){
            resetPasswordFragment.show(getSupportFragmentManager(), null);

        }else{

            navigationUtil.replaceFragment(resetPasswordFragment, R.anim.slide_in_right,
                    R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_right);
            bottomNavigationView.setVisibility(GONE);
        }

    }


    public void navigateToUserFragment(){

            if(UiUtil.isSignedIn(firebaseAuth)){

                navigateToProfileFragment();

            }else{

                navigateToSignInFragment();
            }

            bottomNavigationView.setVisibility(View.VISIBLE);

    }


    @Override
    public void navigateToSettingsActivity() {

        Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);

    }


    @Override
    public void navigateToCollectionFragment(Collection collection) {

        ((MyFragmentFactory)getSupportFragmentManager().getFragmentFactory()).setCollection(collection);

        collectionFragment = (CollectionFragment) createFragment(CollectionFragment.class.getSimpleName());

        navigationUtil.detachAndAddFragments(collectionFragment,
                R.anim.slide_in_right, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_right);

        bottomNavigationView.setVisibility(GONE);

    }


    private void navigateToSearchFragment(){

        if(currentTabFragment != searchFragment) {

            if (searchFragment == null) {

                searchFragment = (SearchFragment) createFragment(SearchFragment.class.getSimpleName());

                navigationUtil.detachAndAddFragments(searchFragment, R.anim.fade_in, R.anim.fade_out,
                        R.anim.fade_in, R.anim.fade_out);

            } else {
                navigationUtil.detachAndAttachFragments(searchFragment, R.anim.fade_in, R.anim.fade_out,
                        R.anim.fade_in, R.anim.fade_out);
            }

            currentTabFragment = searchFragment;

        }
    }


    private void navigateToProfileFragment(){

        if(currentTabFragment != profileFragment) {

            if(currentTabFragment == signInFragment){

                if(profileFragment == null){

                    profileFragment = (ProfileFragment) createFragment(ProfileFragment.class.getSimpleName());
                }

                navigationUtil.toggleProfileAndSignInFragments(profileFragment);

            }else{

                if(profileFragment == null){

                    profileFragment = (ProfileFragment) createFragment(ProfileFragment.class.getSimpleName());

                    navigationUtil.detachAndAddFragments(profileFragment,
                            R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                }else{


                    navigationUtil.detachAndAttachFragments(profileFragment,
                            R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                }

            }


            currentTabFragment = profileFragment;

        }

    }


    private void navigateToSignInFragment(){


        if(currentTabFragment != signInFragment) {

            if(currentTabFragment == profileFragment){

                if(signInFragment == null){

                    signInFragment = (SignInFragment) createFragment(SignInFragment.class.getSimpleName());
                }

                navigationUtil.toggleProfileAndSignInFragments(signInFragment);

                profileFragment = null;


            }else{

                if(signInFragment == null){

                    signInFragment = (SignInFragment) createFragment(SignInFragment.class.getSimpleName());

                    navigationUtil.detachAndAddFragments(signInFragment,
                            R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

                }else{

                    navigationUtil.detachAndAttachFragments(signInFragment,
                            R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                }

            }


            currentTabFragment = signInFragment;

        }

    }


    @Override
    public void onBackPressed() {

        if(navigationUtil.popBackStack()){

            Fragment fragment = navigationUtil.getLastEntryInBackStack();

            changeMenuIcons(fragment);

        }else{

            super.onBackPressed();
        }
    }


    private void changeMenuIcons(Fragment fragment){

        Menu menu = bottomNavigationView.getMenu();

        MenuItem user;

        if(fragment instanceof HomeFragment) {

            MenuItem home = menu.findItem(R.id.bottom_nav_item_home);

            home.setChecked(true);

            bottomNavigationView.setVisibility(View.VISIBLE);

        }else if(fragment instanceof ProfileFragment || fragment instanceof SignInFragment){


            user = menu.findItem(R.id.bottom_nav_item_user);

            user.setChecked(true);

            bottomNavigationView.setVisibility(View.VISIBLE);

        }else if(fragment instanceof SearchFragment){

            MenuItem search = menu.findItem(R.id.bottom_nav_item_search);

            search.setChecked(true);

            bottomNavigationView.setVisibility(View.VISIBLE);
        }

        else{
            bottomNavigationView.setVisibility(GONE);
        }

        currentTabFragment = fragment;

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.bottom_nav_item_home:

                navigateToHomeFragment();

                break;

            case R.id.bottom_nav_item_user:

                navigateToUserFragment();

                break;

            case R.id.bottom_nav_item_search:

                navigateToSearchFragment();

                break;
        }

        return true;

    }


    @Override
    protected  void onPause(){
        super.onPause();

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();

        if(!changedSettings.isEmpty()){

            Intent intent = new Intent(MainActivity.this, MainActivity.class);

            intent.putExtra("settings_has_changed", true);

            startActivity(intent);

            finish();

            overridePendingTransition(0, 0);
        }

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

    }


    @Override
    protected  void onDestroy(){

        super.onDestroy();

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // keeping track of if settings changes because then MainActivity needs to be recreated later.

        if(key.equals("language"))
        {
            changedSettings.remove(key);

            String language = getSharedPreferences("com.example.qfilm", MODE_PRIVATE).getString("language", "English");

            if(!language.equals(currentLanguage)){

                changedSettings.add(key);
            }
        }else {

            if (changedSettings.contains(key)) {
                changedSettings.remove(key);
            } else {
                changedSettings.add(key);
            }
        }
    }


    private Fragment createFragment(String className){

        FragmentManager fragmentManager = getSupportFragmentManager();

        MyFragmentFactory fragmentFactory = (MyFragmentFactory) fragmentManager.getFragmentFactory();

        return fragmentFactory.instantiate(ClassLoader.getSystemClassLoader(), className);
    }

}


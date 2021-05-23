package com.example.qfilm.ui.utils.navigation;

import com.example.qfilm.data.models.entities.Collection;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.data.models.entities.Video;

public interface NavigationInterface {

    void navigateToDetailPageFragment(Result result);

    void navigateToTrailerFragment(Video video);

    void navigateToCreateAccountFragment();

    void navigateToSignInEmailFragment();

    void navigateToResetPasswordFragment();

    void onBackPressed();

    void navigateToUserFragment();

    void navigateToSettingsActivity();

    void navigateToCollectionFragment(Collection collection);
}

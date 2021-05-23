package com.example.qfilm.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.qfilm.R;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.ui.utils.UiUtil;
import com.example.qfilm.ui.utils.navigation.NavigationInterface;
import com.example.qfilm.viewmodels.FirebaseAuthViewModel;
import com.example.qfilm.viewmodels.utils.MyViewModelFactory;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.Task;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.view.Gravity.CENTER;


/**
 *
 * Using FirebaseAuthViewModel to sign in with credential based on Google or facebook token,
 * or to navigate to the page where user can sign in via email.
 *
 * **/

public class SignInFragment extends Fragment {

    private static final String TAG = "SignInFragment";

    private NavigationInterface navigationInterface;

    private CallbackManager callbackManager;

    private ActivityResultLauncher<Intent> signInGoogleActivityResultLauncher;

    private MyViewModelFactory myViewModelFactory;

    private FirebaseAuthViewModel firebaseAuthViewModel;


    public SignInFragment(MyViewModelFactory myViewModelFactory) {

        this.myViewModelFactory = myViewModelFactory;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        navigationInterface = (NavigationInterface) requireActivity();

        firebaseAuthViewModel = new ViewModelProvider(this, myViewModelFactory).get(FirebaseAuthViewModel.class);

        callbackManager = CallbackManager.Factory.create();

        signInGoogleActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {

                            Intent data = result.getData();

                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                            try {

                                // Google Sign In was successful, authenticate with Firebase

                                GoogleSignInAccount account = task.getResult(ApiException.class);

                                AuthCredential credential =  GoogleAuthProvider.getCredential(account.getIdToken(), null);

                                firebaseAuthViewModel.signInWithCredential(credential);


                            } catch (ApiException e) {

                                Log.e(TAG, "Google sign in failed", e);

                                UiUtil.showSnackBarWithLabel(getString(R.string.failed_sign_in_google), requireView(), 4000);

                            }

                    }
                }});
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        // the real fb button is wrapped in another button to be able to style it

        LoginButton facebookLoginButton = view.findViewById(R.id.btn_sign_in_facebook_original);

        Button customFacebookLoginButton = view.findViewById(R.id.btn_sign_in_facebook);

        setupFacebookLoginButton(facebookLoginButton, customFacebookLoginButton);

        // centering heading on tablets

        if(getResources().getBoolean(R.bool.is_tablet)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((TextView)view.findViewById(R.id.tv_heading_sign_in)).setGravity(CENTER);
            }
        }

        setupOnClickListeners(view);

        firebaseAuthViewModel.getAuthOperation().observe(getViewLifecycleOwner(), new Observer<DataResource<FirebaseAuthViewModel.AuthOperation>>() {
            @Override
            public void onChanged(DataResource<FirebaseAuthViewModel.AuthOperation> authOperationDataResource) {
                handleResponse(authOperationDataResource);
            }
        });


        return view;

    }


    private void setupFacebookLoginButton(LoginButton facebookLoginButton, Button customFacebookLoginButton) {

        facebookLoginButton.setReadPermissions("email");

        facebookLoginButton.setFragment(this);

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // Facebook Sign In was successful, authenticate with Firebase

                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());

                firebaseAuthViewModel.signInWithCredential(credential);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {

                UiUtil.showSnackBarWithLabel(getString(R.string.login_fb_failed), requireView(), 4000);
            }
        });

        customFacebookLoginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                facebookLoginButton.performClick();

            }
        });

    }


    private void setupOnClickListeners(View view) {

        view.findViewById(R.id.btn_settings).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                navigationInterface.navigateToSettingsActivity();

            }

        });


        view.findViewById(R.id.btn_sign_in_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    signInGoogle();

            }
        });


        view.findViewById(R.id.btn_sign_in_email).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                navigationInterface.navigateToSignInEmailFragment();
            }
        });

    }


    private void handleResponse(DataResource<FirebaseAuthViewModel.AuthOperation> authOperationDataResource) {

        switch(authOperationDataResource.getStatus()){

            case SUCCESS:

                navigationInterface.navigateToUserFragment(); // will redirect to ProfileFragment

                break;

            case ERROR:

                if (UiUtil.isConnectedToInternet(requireContext())) {

                    UiUtil.showSnackBarWithLabel(getString(R.string.snackbar_unknown_error),
                            requireView(), 4000);

                } else {

                    UiUtil.showSnackBarWithLabel(getString(R.string.snackbar_offline),
                            requireView(), 4000);
                }

                break;
        }
    }


    private void signInGoogle(){

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions);

        Intent signInIntent = googleSignInClient.getSignInIntent();

        signInGoogleActivityResultLauncher.launch(signInIntent);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}

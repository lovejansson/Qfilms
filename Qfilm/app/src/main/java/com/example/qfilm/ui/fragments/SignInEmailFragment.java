package com.example.qfilm.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.text.TextWatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.qfilm.R;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.ui.utils.InputValidator;
import com.example.qfilm.ui.utils.UiUtil;
import com.example.qfilm.ui.utils.navigation.NavigationInterface;
import com.example.qfilm.viewmodels.FirebaseAuthViewModel;
import com.example.qfilm.viewmodels.utils.MyViewModelFactory;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import utils.MyIdlingResource;

import static android.view.Gravity.CENTER;


/**
 *
 * Using FirebaseAuthViewModel to sign in with email
 *
 * **/

public class SignInEmailFragment extends Fragment {

    private static final String TAG = "SignInFragment";

    private NavigationInterface navigationInterface;

    private TextInputLayout layoutEtEmail;

    private TextInputEditText etEmail;

    private TextInputLayout layoutEtPassword;

    private TextInputEditText etPassword;

    private TextWatcher textWatcherPassword;

    private TextWatcher textWatcherEmail;

    private MyViewModelFactory myViewModelFactory;

    private FirebaseAuthViewModel firebaseAuthViewModel;


    public SignInEmailFragment(MyViewModelFactory myViewModelFactory) {

        this.myViewModelFactory = myViewModelFactory;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        navigationInterface = (NavigationInterface) requireActivity();

        firebaseAuthViewModel = new ViewModelProvider(this, myViewModelFactory).get(FirebaseAuthViewModel.class);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_in_email, container, false);

        layoutEtPassword = view.findViewById(R.id.layout_et_password);

        etPassword = view.findViewById(R.id.et_password);

        textWatcherPassword = UiUtil.createAuthenticationInputTextWatcher(layoutEtPassword, etPassword);

        layoutEtEmail = view.findViewById(R.id.layout_et_email);

        etEmail = view.findViewById(R.id.et_email);

        textWatcherEmail = UiUtil.createAuthenticationInputTextWatcher(layoutEtEmail, etEmail);

        firebaseAuthViewModel.getAuthOperation().observe(getViewLifecycleOwner(), new Observer<DataResource<FirebaseAuthViewModel.AuthOperation>>() {
            @Override
            public void onChanged(DataResource<FirebaseAuthViewModel.AuthOperation> authOperationDataResource) {

                handleResponse(authOperationDataResource);
            }
        });

        // centering heading on tablets

        if(getResources().getBoolean(R.bool.is_tablet)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((TextView)view.findViewById(R.id.tv_heading_sign_in_email)).setGravity(CENTER);
            }
        }

        setupOnClickListeners(view);

        return view;

    }


    private void setupOnClickListeners(View view) {

        view.findViewById(R.id.btn_navigate_back).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                navigationInterface.onBackPressed();
            }

        });


        view.findViewById(R.id.tv_forgot_your_password).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                navigationInterface.navigateToResetPasswordFragment();

            }
        });

        // hide keyboard if user touches somewhere outside of edit fields
        view.findViewById(R.id.fragment_sign_in_email).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!(view instanceof EditText)) {

                    UiUtil.hideKeyboard(view);

                    etEmail.clearFocus();

                    etPassword.clearFocus();

                }
            }
        });


        view.findViewById(R.id.btn_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UiUtil.hideKeyboard(view);

                if (isValidInput()) {

                    MyIdlingResource.increment();

                    firebaseAuthViewModel.signInWithEmail(etEmail.getText().toString(),
                            etPassword.getText().toString());
                }
            }
        });


        view.findViewById(R.id.btn_create_account).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                UiUtil.hideKeyboard(view);

                navigationInterface.navigateToCreateAccountFragment();
            }
        });

    }


    private boolean isValidInput() {

        boolean isValid = true;

        if(!InputValidator.isValidEmail(requireContext(), layoutEtEmail, etEmail)){

            etEmail.addTextChangedListener(textWatcherEmail);

            isValid = false;
        }

        if(!InputValidator.isValidPassword(requireContext(), layoutEtPassword, etPassword)){

            etPassword.addTextChangedListener(textWatcherPassword);

            isValid = false;
        }

        return isValid;

    }


    private void handleResponse(DataResource<FirebaseAuthViewModel.AuthOperation> authOperationDataResource) {

        if(authOperationDataResource.getStatus() == DataResource.Status.SUCCESS){

            if(authOperationDataResource.getData() == FirebaseAuthViewModel.AuthOperation.SIGN_IN){

                navigationInterface.navigateToUserFragment();

            }else if(authOperationDataResource.getData() == FirebaseAuthViewModel.AuthOperation.EMAIL_VERIFICATION){
                UiUtil.showSnackBarWithLabel(getString(R.string.email_verification_sent), requireView(),
                        3000);
            }

        }else {

            if (authOperationDataResource.getData() == FirebaseAuthViewModel.AuthOperation.SIGN_IN) {

                if (firebaseAuthViewModel.getFirebaseAuthException() instanceof FirebaseAuthViewModel.EmailNotVerified) {

                    Snackbar snackbar = UiUtil.createSnackBarWithLabel(getString(R.string.email_address_not_verified), requireView(), 10000);

                    snackbar.setAction(requireActivity().getResources().getString(R.string.snackBar_action_resend), new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            MyIdlingResource.increment();

                            firebaseAuthViewModel.sendVerificationEmail();
                        }
                    });

                    snackbar.show();

                } else if (firebaseAuthViewModel.getFirebaseAuthException() instanceof FirebaseAuthInvalidCredentialsException) {

                    layoutEtPassword.setError(getString(R.string.input_wrong_password));

                    etPassword.addTextChangedListener(textWatcherPassword);

                } else if (firebaseAuthViewModel.getFirebaseAuthException() instanceof FirebaseAuthInvalidUserException) {

                    layoutEtEmail.setError(getString(R.string.input_user_no_exist));

                    etEmail.addTextChangedListener(textWatcherEmail);

                } else {

                    if (UiUtil.isConnectedToInternet(requireContext())) {

                        UiUtil.showSnackBarWithLabel(getString(R.string.snackbar_unknown_error),
                                requireView(), 4000);
                    } else {
                        UiUtil.showSnackBarWithLabel(getString(R.string.snackbar_offline),
                                requireView(), 4000);
                    }
                }

            } else if (authOperationDataResource.getData() == FirebaseAuthViewModel.AuthOperation.EMAIL_VERIFICATION) {
                if (UiUtil.isConnectedToInternet(requireContext())) {

                    UiUtil.showSnackBarWithLabel(getString(R.string.snackbar_unknown_error),
                            requireView(), 4000);
                } else {
                    UiUtil.showSnackBarWithLabel(getString(R.string.snackbar_offline),
                            requireView(), 4000);
                }
            }
        }

        MyIdlingResource.decrement();
    }

}

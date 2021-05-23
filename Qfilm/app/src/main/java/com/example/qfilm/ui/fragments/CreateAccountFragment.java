package com.example.qfilm.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.qfilm.R;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.ui.utils.UiUtil;
import com.example.qfilm.ui.utils.InputValidator;
import com.example.qfilm.ui.utils.navigation.NavigationInterface;
import com.example.qfilm.viewmodels.FirebaseAuthViewModel;
import com.example.qfilm.viewmodels.utils.MyViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


import utils.MyIdlingResource;

import static android.view.View.GONE;

/**
 *
 * Using FirebaseAuthViewModel to handle creation of user account
 *
 * **/

public class CreateAccountFragment extends DialogFragment {

    private static final String TAG = "CreateAccountFragment";

    private NavigationInterface navigationInterface;

    private TextInputLayout layoutEtUsername;
    private TextInputEditText etUsername;
    private TextWatcher textWatcherUsername;

    private TextInputLayout layoutEtEmail;
    private TextInputEditText etEmail;
    private TextWatcher textWatcherEmail;

    private TextInputLayout layoutEtPassword;
    private TextInputEditText etPassword;
    private TextWatcher textWatcherPassword;

    private MyViewModelFactory myViewModelFactory;

    private FirebaseAuthViewModel firebaseAuthViewModel;


    public CreateAccountFragment(MyViewModelFactory myViewModelFactory) {

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

        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        setupInputFields(view);

        setupOnClickListeners(view);

        firebaseAuthViewModel.getAuthOperation().observe(getViewLifecycleOwner(),
                new Observer<DataResource<FirebaseAuthViewModel.AuthOperation>>() {
            @Override
            public void onChanged(DataResource<FirebaseAuthViewModel.AuthOperation> authOperationDataResource) {
                handleResponse(authOperationDataResource);
            }
        });

        return view;

    }


    @Override
    public void onStart()
    {
        super.onStart();

        // adjust dialog size according to tablet mode

        if(getResources().getBoolean(R.bool.is_tablet)) {

            Dialog dialog = getDialog();

            if (dialog != null) {

                UiUtil.setDialogFragmentDimensions(dialog);

                dialog.getWindow().setWindowAnimations(R.style.Window_DialogAnimation);
            }
        }
    }



    private void setupInputFields(View view) {

        layoutEtUsername = view.findViewById(R.id.layout_et_username);
        etUsername = view.findViewById(R.id.et_username);
        textWatcherUsername = UiUtil.createAuthenticationInputTextWatcher(layoutEtUsername, etUsername);

        layoutEtEmail = view.findViewById(R.id.layout_et_email);
        etEmail = view.findViewById(R.id.et_email);
        textWatcherEmail = UiUtil.createAuthenticationInputTextWatcher(layoutEtEmail, etEmail);

        layoutEtPassword = view.findViewById(R.id.layout_et_password);
        etPassword = view.findViewById(R.id.et_password);
        textWatcherPassword = UiUtil.createAuthenticationInputTextWatcher(layoutEtPassword, etPassword);
    }


    private void setupOnClickListeners(View view) {

        view.findViewById(R.id.btn_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isValidInput()) {

                    MyIdlingResource.increment();

                    UiUtil.hideKeyboard(view);

                    firebaseAuthViewModel.createAccount(etEmail.getText().toString(),
                            etPassword.getText().toString());

                }
            }
        });

        // hide keyboard if user touches somewhere outside of edit fields
        view.findViewById(R.id.fragment_create_account).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(!(view instanceof EditText)){

                    UiUtil.hideKeyboard(view);

                    etUsername.clearFocus();

                    etEmail.clearFocus();

                    etPassword.clearFocus();

                }

            }
        });

        // different back navigation if shown as dialog or not

        if(getResources().getBoolean(R.bool.is_tablet)) {

            view.findViewById(R.id.btn_navigate_back).setVisibility(GONE);

            view.findViewById(R.id.btn_close_create_account).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    dismiss();
                }

            });

        }else{

            view.findViewById(R.id.btn_close_create_account).setVisibility(GONE);

            view.findViewById(R.id.btn_navigate_back).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    navigationInterface.onBackPressed();
                }

            });
        }
    }


    private boolean isValidInput() {

        boolean isValidInput = true;

        if(!InputValidator.isValidUsername(requireContext(), layoutEtUsername, etUsername)){

           isValidInput = false;

           etUsername.addTextChangedListener(textWatcherUsername);
        }

        if(!InputValidator.isValidEmail(requireContext(), layoutEtEmail, etEmail)){

            etEmail.addTextChangedListener(textWatcherEmail);

            isValidInput = false;
        }

        if(!InputValidator.isValidPassword(requireContext(), layoutEtPassword, etPassword)){

            etPassword.addTextChangedListener(textWatcherPassword);

            isValidInput = false;
        }

        return isValidInput;

    }


    private void handleResponse(DataResource<FirebaseAuthViewModel.AuthOperation> authOperationDataResource) {

        if(authOperationDataResource.getStatus() == DataResource.Status.SUCCESS){

            if(authOperationDataResource.getData() == FirebaseAuthViewModel.AuthOperation.CREATE_ACCOUNT){

                firebaseAuthViewModel.sendVerificationEmail();

                firebaseAuthViewModel.upDateUsername(etUsername.getText().toString());

                etUsername.getText().clear();

                etEmail.getText().clear();

                etPassword.getText().clear();

            }else if(authOperationDataResource.getData() == FirebaseAuthViewModel.AuthOperation.EMAIL_VERIFICATION){
                UiUtil.showSnackBarWithLabel(getString(R.string.email_verification_sent), requireView(),
                        5000);
            }

        }else {

            if (authOperationDataResource.getData() == FirebaseAuthViewModel.AuthOperation.CREATE_ACCOUNT) {

                if (firebaseAuthViewModel.getFirebaseAuthException() instanceof FirebaseAuthInvalidCredentialsException) {

                    layoutEtEmail.setError(requireActivity().getResources().getString(R.string.input_invalid_email_format));

                    etEmail.addTextChangedListener(textWatcherEmail);

                } else if (firebaseAuthViewModel.getFirebaseAuthException() instanceof FirebaseAuthUserCollisionException) {

                    layoutEtEmail.setError(requireActivity().getResources().getString(R.string.account_with_email_already_exist));

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
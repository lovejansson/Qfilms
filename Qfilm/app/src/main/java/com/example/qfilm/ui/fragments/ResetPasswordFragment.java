package com.example.qfilm.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.qfilm.R;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.ui.utils.UiUtil;
import com.example.qfilm.ui.utils.InputValidator;
import com.example.qfilm.ui.utils.navigation.NavigationInterface;
import com.example.qfilm.viewmodels.FirebaseAuthViewModel;
import com.example.qfilm.viewmodels.utils.MyViewModelFactory;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.FirebaseAuthInvalidUserException;


import utils.MyIdlingResource;

import static android.view.View.GONE;

/**
 *
 * Using FirebaseAuthViewModel to handle reset password
 *
 * **/

public class ResetPasswordFragment extends DialogFragment {

    private static final String TAG = "ResetPasswordFragment";

    private NavigationInterface navigationInterface;

    private TextWatcher textWatcher;

    private TextInputEditText etEmail;

    private TextInputLayout layoutEtEmail;

    private MyViewModelFactory myViewModelFactory;

    private FirebaseAuthViewModel firebaseAuthViewModel;


    public ResetPasswordFragment(MyViewModelFactory myViewModelFactory) {

        this.myViewModelFactory = myViewModelFactory;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        navigationInterface = (NavigationInterface) requireActivity();

        firebaseAuthViewModel = new ViewModelProvider(this, myViewModelFactory).get(FirebaseAuthViewModel.class);

        textWatcher = UiUtil.createAuthenticationInputTextWatcher(layoutEtEmail, etEmail);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        layoutEtEmail = view.findViewById(R.id.layout_et_email);

        etEmail =  view.findViewById(R.id.et_email);

        firebaseAuthViewModel.getAuthOperation().observe(getViewLifecycleOwner(), new Observer<DataResource<FirebaseAuthViewModel.AuthOperation>>() {
            @Override
            public void onChanged(DataResource<FirebaseAuthViewModel.AuthOperation> authOperationDataResource) {
                handleResponse(authOperationDataResource);
            }
        });

        setupOnclickListeners(view);

        return view;

    }


    @Override
    public void onStart()
    {
        super.onStart();

        if(getResources().getBoolean(R.bool.is_tablet)) {

            Dialog dialog = getDialog();

            if (dialog != null) {

                UiUtil.setDialogFragmentDimensions(dialog);

                dialog.getWindow().setWindowAnimations(R.style.Window_DialogAnimation);

            }
        }
    }


    private void setupOnclickListeners(View view) {

        // hide keyboard if user touches somewhere outside of edit fields
        view.findViewById(R.id.fragment_reset_password).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(!(view instanceof EditText)){

                    UiUtil.hideKeyboard(view);

                    etEmail.clearFocus();

                }

            }
        });

        // different back navigation depending on if shown as Dialog

        if(getResources().getBoolean(R.bool.is_tablet)) {

            view.findViewById(R.id.btn_navigate_back).setVisibility(GONE);

            view.findViewById(R.id.btn_close_reset_password).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    dismiss();
                }

            });

        }else{

            view.findViewById(R.id.btn_close_reset_password).setVisibility(GONE);

            view.findViewById(R.id.btn_navigate_back).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    navigationInterface.onBackPressed();
                }

            });
        }

        view.findViewById(R.id.btn_send_email_reset_password).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                UiUtil.hideKeyboard(view);

                if(InputValidator.isValidEmail(requireActivity(), layoutEtEmail, etEmail)){

                    MyIdlingResource.increment();

                    firebaseAuthViewModel.sendPasswordResetEmail(etEmail.getText().toString());

                }else{

                    etEmail.addTextChangedListener(textWatcher);
                }
            }
        });

    }


    private void handleResponse(DataResource<FirebaseAuthViewModel.AuthOperation> authOperationDataResource) {

        if(authOperationDataResource.getStatus() == DataResource.Status.SUCCESS){

            etEmail.getText().clear();
            
            UiUtil.showSnackBarWithLabel(getString(R.string.snackbar_reset_password_email),
                    requireView(),4000);


        }else{
            if(firebaseAuthViewModel.getFirebaseAuthException() instanceof  FirebaseAuthInvalidUserException){

                layoutEtEmail.setError(getString(R.string.input_user_no_exist));

                etEmail.addTextChangedListener(textWatcher);
            }else{

                if(UiUtil.isConnectedToInternet(requireContext())){

                UiUtil.showSnackBarWithLabel(getString(R.string.snackbar_unknown_error),
                        requireView(),4000);
                }else{
                    UiUtil.showSnackBarWithLabel(getString(R.string.snackbar_offline),
                            requireView(),4000);
                }
            }
        }

        MyIdlingResource.decrement();
    }

}

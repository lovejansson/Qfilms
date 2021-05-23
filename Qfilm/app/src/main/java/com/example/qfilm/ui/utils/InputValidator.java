package com.example.qfilm.ui.utils;

import android.content.Context;

import com.example.qfilm.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


/**
 *
 *  validates input and sets potential errors to edit text
 *
 * **/
public class InputValidator {


    public static Boolean isValidUsername(Context context,  TextInputLayout layoutEtUsername, TextInputEditText etUsername){

        if(etUsername.getText().toString().isEmpty()){
            layoutEtUsername.setError(context.getResources().getString(R.string.input_required_field));
            return false;
        }

        return true;
    }


    public static Boolean isValidPassword(Context context, TextInputLayout layoutEtPassword, TextInputEditText etPassword ) {
        String password = etPassword.getText().toString();

        if (password.isEmpty()) {
            layoutEtPassword.setError(context.getResources().getString(R.string.input_required_field));
            return false;
        }
        if (password.length() < 6) {
            layoutEtPassword.setError(context.getResources().getString(R.string.input_password_length_requirement));
            return false;
        }

        return true;
    }


    public static Boolean isValidEmail(Context context,TextInputLayout layoutEtEmail, TextInputEditText etEmail) {

        String email = etEmail.getText().toString();

        if(etEmail.getText().toString().isEmpty()){
            layoutEtEmail.setError(context.getResources().getString(R.string.input_required_field));
            return false;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            layoutEtEmail.setError(context.getString(R.string.input_invalid_email_format));
            return false;
        }

        return true;
    }
}

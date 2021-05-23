package com.example.qfilm.ui.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.qfilm.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 *
 * different methods for functionality that was needed by more than one fragments
 *
 * **/

public class UiUtil {


    public static void hideKeyboard(View view){

        InputMethodManager inputMethodManager = (InputMethodManager)
                view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static Boolean isConnectedToInternet(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }


    public static void displayErrorMessage(Context context, View errorLayout){

        String title = context.getString(R.string.error_unknown_heading);

        String message = context.getString(R.string.error_unknown_message);

        if (!isConnectedToInternet(context)) {

            title = context.getString(R.string.error_no_network_headline);
            message = context.getString(R.string.error_no_network_message);
        }

        ((TextView) errorLayout.findViewById(R.id.tv_error_title)).setText(title);

        ((TextView) errorLayout.findViewById(R.id.tv_error_description)).setText(message);

        errorLayout.setVisibility(View.VISIBLE);

    }



    public static Snackbar createSnackBarWithLabel(String content, View view, int duration) {

        int[] attrs = {R.attr.colorOnBackground, R.attr.colorSurface};

        TypedArray ta = view.getContext().obtainStyledAttributes(attrs);

        int colorBackground = ta.getColor(0, R.attr.colorOnBackground);

        int colorForeground = ta.getColor(1, R.attr.colorSurface);

        ta.recycle();

        Snackbar snackbar = Snackbar.make(view, content, duration);

        snackbar.setBackgroundTint(colorBackground);

        snackbar.setTextColor(colorForeground);

        return snackbar;

    }


    public static void showSnackBarWithLabel(String content, View view, int duration) {

        Snackbar snackbar = createSnackBarWithLabel(content, view, duration);

        snackbar.show();

    }


    // for edit texts in authentication fragments which erases errors when user starts to type again
    public static TextWatcher createAuthenticationInputTextWatcher(TextInputLayout textInputLayout, TextInputEditText textInputEditText){

       return new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void afterTextChanged(Editable editable) {
                textInputLayout.setError(null);
                textInputEditText.removeTextChangedListener(this);
           }
       };

    }


    public static Boolean isSignedIn(FirebaseAuth firebaseAuth){

        // if user is signed in with google/facebook or with email and that email is verified

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {

            String providerId = currentUser.getProviderData().get(1).getProviderId();

            return !providerId.equals(EmailAuthProvider.PROVIDER_ID) || currentUser.isEmailVerified();

        }

        return false;


    }


    // used in DetailPageFragment, TrailerFragment, SignInEmailFragment and ResetPasswordFragment
    // when they are shown as dialogs in tabletMode

    public static void setDialogFragmentDimensions(Dialog dialog){

        DisplayMetrics displayMetrics = new DisplayMetrics();

        dialog.getOwnerActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        double percentageOfParent = dialog.getOwnerActivity().getResources().getInteger(R.integer.percentage_of_parent);

        percentageOfParent /= 100;

        int height = (int) (displayMetrics.heightPixels * percentageOfParent);

        int width =  (int)(displayMetrics.widthPixels * percentageOfParent);

        dialog.getWindow().setLayout(width, height);

    }

}

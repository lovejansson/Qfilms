package com.example.qfilm.ui.utils.matchers;

import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class TextInputLayoutErrorTextMatcher extends TypeSafeMatcher<View> {

    int stringResId;

    public TextInputLayoutErrorTextMatcher(int stringResId) {
        super(View.class);
        this.stringResId = stringResId;

    }



    @Override
    public boolean matchesSafely(View view) {
        if (!(view instanceof TextInputLayout)) {
            return false;
        }

        TextInputLayout layout = (TextInputLayout)view;

        CharSequence error = layout.getError();

        if (error == null) {
            return false;
        }

        String correctErrorText = error.toString();

        String errorText = layout.getContext().getResources().getString(stringResId);

        return errorText.equals(correctErrorText);
    }

    @Override
    public void describeTo(Description description) {

    }
};


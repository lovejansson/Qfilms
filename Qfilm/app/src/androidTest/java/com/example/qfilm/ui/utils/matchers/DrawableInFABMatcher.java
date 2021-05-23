package com.example.qfilm.ui.utils.matchers;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.DrawableRes;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class DrawableInFABMatcher extends TypeSafeMatcher<View> {

    private @DrawableRes final int expectedDrawableId;


    public DrawableInFABMatcher( @DrawableRes  int expectedDrawable) {
        super(View.class);
        this.expectedDrawableId = expectedDrawable;
    }


    @Override
    protected boolean matchesSafely(View item) {

        if(!(item instanceof FloatingActionButton)){
            return false;

        }

        FloatingActionButton fab = (FloatingActionButton) item;

        if(expectedDrawableId < 0){
            return fab.getDrawable() == null;
        }

        Drawable expectedDrawable = item.getContext().getResources().getDrawable(expectedDrawableId,null );

        return expectedDrawable != null;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with drawable from resource id: ");
        description.appendValue(expectedDrawableId);
    }
}

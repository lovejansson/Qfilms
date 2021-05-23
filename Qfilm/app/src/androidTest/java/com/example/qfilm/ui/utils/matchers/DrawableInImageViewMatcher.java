package com.example.qfilm.ui.utils.matchers;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class DrawableInImageViewMatcher extends TypeSafeMatcher<View> {

    @DrawableRes final int expectedDrawableId;

    public DrawableInImageViewMatcher( @DrawableRes int expectedDrawable) {
        super(View.class);
        this.expectedDrawableId = expectedDrawable;
    }



    @Override
    protected boolean matchesSafely(View item) {

        if(!(item instanceof ImageView)){
            return false;

        }

        ImageView imageView = (ImageView) item;

        if(expectedDrawableId < 0){
            return imageView.getDrawable() == null;
        }

        Drawable expectedDrawable = item.getContext().getResources().getDrawable(expectedDrawableId,
                null);

        return expectedDrawable != null;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with drawable from resource id: ");
        description.appendValue(expectedDrawableId);
    }
}

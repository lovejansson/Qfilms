package com.example.qfilm.ui.utils.matchers;

import android.view.View;
import android.widget.Spinner;

import com.example.qfilm.data.models.entities.Genre;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

public class SpinnerContentMatcher extends TypeSafeMatcher<View> {

    List<Genre> genres;

    public SpinnerContentMatcher(List<Genre> genres) {
        super(View.class);
        this.genres = genres;
    }

    @Override
    protected boolean matchesSafely(View item) {
        if(!(item instanceof Spinner)){
            return false;
        }

        Spinner spinner = (Spinner) item;

        int spinnerItemCount = spinner.getAdapter().getCount();

        if(genres.size() != spinnerItemCount){
            return false;
        }

        for(int i = 0 ; i < genres.size(); ++i){
            if(!genres.get(i).equals(spinner.getAdapter().getItem(i))){
                return false;
            }
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Spinner does not contain provided" +
                "content");
    }
}

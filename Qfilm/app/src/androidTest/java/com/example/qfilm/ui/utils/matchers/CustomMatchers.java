package com.example.qfilm.ui.utils.matchers;

import androidx.annotation.DrawableRes;

import com.example.qfilm.data.models.entities.Genre;

import java.util.List;

public class CustomMatchers {

   public static DrawableInFABMatcher drawableInFABMatcher(@DrawableRes int id){
       return new DrawableInFABMatcher(id);
   }

   public static DrawableInImageViewMatcher drawableInImageViewMatcher(@DrawableRes int id){
       return new DrawableInImageViewMatcher(id);
   }

    public static LabelsInTabLayoutMatcher labelsInTabLayoutMatcher(List<String> labels){
        return new LabelsInTabLayoutMatcher(labels);
    }

    public static IsCheckedInBottomNavigationView isCheckedInBottomNavigationView(int position){
       return new IsCheckedInBottomNavigationView(position);
    }

    public static BottomNavigationViewContentMatcher bottomNavigationViewContentMatcher(List<Integer> ids, List<String> labels){
       return new BottomNavigationViewContentMatcher(ids, labels);
    }

    public static SpinnerContentMatcher spinnerContentMatcher(List<Genre> genres){
        return new SpinnerContentMatcher(genres);
    }

    public static RecyclerViewMatcher recyclerViewMatcher(){
        return new RecyclerViewMatcher();
    }

    public static TextInputLayoutErrorTextMatcher textInputLayoutErrorTextMatcher(int stringResId) {
        return new TextInputLayoutErrorTextMatcher(stringResId);

    }



}

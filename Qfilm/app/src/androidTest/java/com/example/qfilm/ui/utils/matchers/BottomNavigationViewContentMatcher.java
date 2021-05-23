package com.example.qfilm.ui.utils.matchers;

import android.view.Menu;
import android.view.View;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import java.util.List;

public class BottomNavigationViewContentMatcher extends TypeSafeMatcher<View> {

   private List<String> labels;
   private List<Integer> ids;

    public BottomNavigationViewContentMatcher(List<Integer> ids, List<String> labels) {
        this.labels = labels;
        this.ids = ids;

    }


    @Override
    protected boolean matchesSafely(View item) {

        if(!(item instanceof BottomNavigationView)){
            return false;
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) item;

        if(labels.size() != bottomNavigationView.getMenu().size()){
            return false;
        }

        if(ids.size() != bottomNavigationView.getMenu().size()){
            return false;
        }

        Menu menu = bottomNavigationView.getMenu();

        for(int i = 0; i < bottomNavigationView.getMenu().size(); ++i){

            String label = menu.getItem(i).getTitle().toString();

            int id = menu.getItem(i).getItemId();

            if(!label.equals(labels.get(i)) || id != ids.get(i)){
                return false;
            }
        }

        return true;

    }

    @Override
    public void describeTo(Description description) {

        description.appendText("BottomNavigationView does not contain the provided" +
                "content");

    }
}

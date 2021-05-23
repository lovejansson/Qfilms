package com.example.qfilm.ui.utils.matchers;

import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class IsCheckedInBottomNavigationView extends TypeSafeMatcher<View> {
    private final int position;

    public IsCheckedInBottomNavigationView(int position) {
        super(View.class);
        this.position = position;
    }

    @Override
    protected boolean matchesSafely(View item) {
        if(!(item instanceof BottomNavigationView)){
            return false;
        }

       BottomNavigationView bottomNavigationView = (BottomNavigationView) item;

       MenuItem menuItem = bottomNavigationView.getMenu().getItem(position);

       return menuItem.isChecked();

    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Menu item in BottomNavigationView at position " + position +
                "is not checked, expected it to be checked");
    }
}

package com.example.qfilm.ui.utils.matchers;

import android.view.View;

import com.google.android.material.tabs.TabLayout;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

public class LabelsInTabLayoutMatcher extends TypeSafeMatcher<View> {

    private List<String> labels;


    public LabelsInTabLayoutMatcher(List<String> labels) {
        super(View.class);
        this.labels = labels;
    }


    @Override
    protected boolean matchesSafely(View item) {

        if(!(item instanceof TabLayout)){
            return false;
        }

        TabLayout tabLayout = (TabLayout) item;

        if(tabLayout.getTabCount() != labels.size()){
            return false;
        }

        for(int i = 0; i < tabLayout.getTabCount(); ++i){

            TabLayout.Tab tab = tabLayout.getTabAt(i);

            if(tab != null && tab.getText() != null){
                String text = tab.getText().toString();

                if(!text.equals(labels.get(i))){
                    return false;
                }
            }
        }

        return true;

    }

    @Override
    public void describeTo(Description description) {
        description.appendText("text labels in TabLayout");
    }
}

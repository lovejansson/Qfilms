package com.example.qfilm.ui.utils.matchers;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

public class RecyclerViewMatcher {

    public RecyclerViewMatcher() {

    }

    public Matcher<View> itemCountMatcher(int count) {
        return new TypeSafeMatcher<View>() {

            private int actualCount;

            @Override
            protected boolean matchesSafely(View item) {
                if (!(item instanceof RecyclerView)) {
                    return false;
                }

                RecyclerView recyclerView = (RecyclerView) item;

                if (recyclerView.getAdapter() != null) {

                  actualCount = recyclerView.getAdapter().getItemCount();

                    Log.d("matcher", "matchesSafely: " + actualCount);

                  return actualCount == count;
                }

                return false;

            }

            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView ItemCount " + actualCount + " didn't match provided value "
                + count);

            }
        };

    }


    public Matcher<View> itemAtPositionHasViews(int position, List<Integer> viewIds){
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (!(item instanceof RecyclerView)) {
                    return false;
                }

                RecyclerView recyclerView = (RecyclerView) item;
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);

                if(viewHolder != null){
                    View childView = viewHolder.itemView;
                    for(Integer id : viewIds){
                        if(childView.findViewById(id) == null){
                            return false;
                        }
                    }

                }else{
                    return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView item didn't contain provided views");

            }
        };
    }


    public Matcher<View> itemAtPositionHasText(int position, Integer id, String text){
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (!(item instanceof RecyclerView)) {
                    return false;
                }

                RecyclerView recyclerView = (RecyclerView) item;
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);

                if(viewHolder != null){
                        View childView = viewHolder.itemView;

                        View view = childView.findViewById(id);

                        if(view == null){
                            return false;
                        }

                        if(!(view instanceof TextView)){
                            return false;
                        }

                        TextView textView = (TextView) view;

                    return textView.getText().toString().equals(text);

                }else{
                    return false;
                }

            }

            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView item didn't contain provided text value");

            }
        };
    }

    public Matcher<View> itemAtPositionIsSelected(int position){
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (!(item instanceof RecyclerView)) {
                    return false;
                }

                RecyclerView recyclerView = (RecyclerView) item;
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);

                if(viewHolder != null){
                    View childView = viewHolder.itemView;


                    return childView.isSelected();

                }else{
                    return false;
                }

            }

            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView item is not selected");

            }
        };
    }

}

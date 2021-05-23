package com.example.qfilm.ui.utils;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 * creates spaces between columns when recycler views are displayed as grids in tablet mode
 *
 * **/

public class ColumnSpacingDecoration  extends RecyclerView.ItemDecoration {

    private int space;
    private int numberOfColumns;

    public ColumnSpacingDecoration(int space, int numberOfColumns) {

        this.space = space;
        this.numberOfColumns = numberOfColumns;
    }


    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {

        int itemPosition = parent.getChildAdapterPosition(view);

        if((itemPosition + 1) % numberOfColumns != 0) {

            outRect.right = space;

        }

    }
}


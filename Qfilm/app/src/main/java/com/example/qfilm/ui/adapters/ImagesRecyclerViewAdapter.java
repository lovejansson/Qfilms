package com.example.qfilm.ui.adapters;

import androidx.annotation.Nullable;

import com.example.qfilm.data.models.entities.Image;

public class ImagesRecyclerViewAdapter extends BaseRecyclerViewAdapter<Image> {

    private int layoutId;

    public ImagesRecyclerViewAdapter(@Nullable OnClickListItemListener<Image> onClickListItemListener,
                                     OnClickActionButtonListener<Image> onClickActionButtonListener, int layoutId) {
        super(onClickListItemListener, onClickActionButtonListener);

        this.layoutId = layoutId;
    }

    @Override
    public int getItemViewType(int position) {

        return layoutId;
    }

}

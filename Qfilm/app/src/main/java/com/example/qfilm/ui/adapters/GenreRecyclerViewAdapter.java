package com.example.qfilm.ui.adapters;

import androidx.annotation.Nullable;

import com.example.qfilm.data.models.entities.Genre;


public class GenreRecyclerViewAdapter extends BaseRecyclerViewAdapter<Genre> {

    private static final String TAG = "GenreRecyclerViewAdapte";
    
    private int layoutId;

    private Genre selectedGenre;
    
    public GenreRecyclerViewAdapter(@Nullable OnClickListItemListener<Genre> onClickListItemListener,
                                    OnClickActionButtonListener<Genre> onClickActionButtonListener,
                                    int layoutId, Genre selectedGenre) {
        super(onClickListItemListener, onClickActionButtonListener);
        
        this.layoutId = layoutId;
        this.selectedGenre = selectedGenre;
    }

    @Override
    public int getItemViewType(int position) {
        return layoutId;
    }

}

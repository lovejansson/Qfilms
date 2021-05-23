package com.example.qfilm.ui.adapters;
import androidx.annotation.Nullable;
import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.Collection;


public class CollectionsRecyclerViewAdapter extends BaseRecyclerViewAdapter<Collection> {

    private static final String TAG = "CollectionsRecyclerView";

    private int layoutId;

    public CollectionsRecyclerViewAdapter(@Nullable OnClickListItemListener<Collection> onClickListItemListener,
                                          OnClickActionButtonListener<Collection> onClickActionButtonListener,
                                          int layoutId) {

        super(onClickListItemListener, onClickActionButtonListener);

        this.layoutId = layoutId;
    }

    @Override
    public int getItemViewType(int position) {

        if(data.get(position) == null){

            return R.layout.recyclerview_item_progress_bar;

        }else{

            return layoutId;
        }

    }
}

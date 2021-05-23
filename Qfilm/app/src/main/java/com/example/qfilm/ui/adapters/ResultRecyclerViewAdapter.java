package com.example.qfilm.ui.adapters;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;

import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.Result;

public class ResultRecyclerViewAdapter extends BaseRecyclerViewAdapter<Result> {

    private static final String TAG = "ResultRecyclerViewAdapt";

    private int layoutId;

    private Boolean showOriginalTitles;


    public ResultRecyclerViewAdapter(@Nullable OnClickListItemListener<Result> onClickListItemListener,
                                     @Nullable OnClickActionButtonListener<Result> onClickActionButtonListener,
                                     int layoutId, Boolean showOriginalTitles) {
        super(onClickListItemListener, onClickActionButtonListener);

        this.layoutId = layoutId;

        this.showOriginalTitles = showOriginalTitles;

    }

    @Override
    public int getItemViewType(int position) {

        if(data.get(position) == null){

            return R.layout.recyclerview_item_progress_bar;

        }else{

            return layoutId;
        }

    }


    @Override
    protected void bindAdditionalProperties(ViewDataBinding binding) {
        binding.setVariable(BR.showOriginalTitles, showOriginalTitles);
    }
}

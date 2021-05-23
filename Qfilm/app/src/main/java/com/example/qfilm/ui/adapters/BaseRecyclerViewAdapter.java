package com.example.qfilm.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;


import androidx.recyclerview.widget.RecyclerView;

import com.example.qfilm.BR;
import com.example.qfilm.R;
import java.util.ArrayList;
import java.util.List;


/**
 * This base adapter is used to provide common functionality for many other adapters in the application that
 * has a List of objects.
 *
 * It is binding the view to the data via a binding object. The classes that derives from this class
 * overrides 'getItemViewType' to provide which layout to use when the binding object is created.
 *
 * It also has a interface for a itemClickListener and actionButtonClickListener that is implemented
 * in fragments if needed.
 *
 * **/

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewAdapter<T>.BaseViewHolder> {

    private static final String TAG = "BaseRecyclerViewAdapter";

    @Nullable
    private OnClickListItemListener<T> onClickListItemListener;

    @Nullable
    private OnClickActionButtonListener<T> onClickActionButtonListener;

    protected List<T> data;

    public BaseRecyclerViewAdapter(@Nullable OnClickListItemListener<T> onClickListItemListener,
                                   OnClickActionButtonListener<T> onClickActionButtonListener) {

        this.onClickListItemListener = onClickListItemListener;

        this.onClickActionButtonListener = onClickActionButtonListener;

        this.data = new ArrayList<>();

    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Different ViewHolders for progressbar and data

        if(viewType == R.layout.recyclerview_item_progress_bar) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_progress_bar,
                    parent, false);

            return new BaseProgressBarViewHolder(view);
        }else {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    viewType, parent, false);


            return new BaseDataViewHolder(binding, this.onClickListItemListener, this.onClickActionButtonListener);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerViewAdapter.BaseViewHolder holder, int position) {

        if (holder.getClass() == BaseDataViewHolder.class) {

            T item = data.get(position);

            BaseDataViewHolder baseDataViewHolder = (BaseDataViewHolder) holder;

            baseDataViewHolder.bind(item);

        }


    }

    // this is so subclasses can bind additional properties if needed

    protected void bindAdditionalProperties(ViewDataBinding binding){

    }

    /**
     *     VIEW HOLDER CLASSES
     **/


    public class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    class BaseDataViewHolder extends BaseViewHolder {

        protected final ViewDataBinding binding;

        private OnClickListItemListener<T> itemListener;

        private OnClickActionButtonListener<T> btnListener;


        public BaseDataViewHolder(ViewDataBinding binding, OnClickListItemListener<T> onListItemListener, OnClickActionButtonListener<T>
                                  onClickActionButtonListener) {

            super(binding.getRoot());

            this.binding = binding;

            this.itemListener = onListItemListener;

            this.btnListener = onClickActionButtonListener;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(itemListener != null) {

                        itemListener.onListItemClick(data.get(getAbsoluteAdapterPosition()));
                    }
                }
            });

            if(itemView.findViewById(R.id.btn_action) != null){

                itemView.findViewById(R.id.btn_action).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        btnListener.onActionButtonClick(data.get(getAbsoluteAdapterPosition()));
                    }
                });
            }

        }

        public void bind(Object obj) {

            binding.setVariable(BR.obj, obj);

            bindAdditionalProperties(binding);

            binding.executePendingBindings();
        }

    }


     class BaseProgressBarViewHolder extends BaseViewHolder {

       public BaseProgressBarViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }


    /**
     *    methods that modifies data
    **/

    @Override
    public int getItemCount() {

        return data.size();
    }


    public void clear(){
        data.clear();
    }


    public void setData(List<T> newData){

        data = newData;

        notifyDataSetChanged();
    }


    public void appendData(List<T> newData){

        int size = data.size();

        data.addAll(newData);

        notifyItemRangeInserted(size, newData.size());

    }


    public void appendNullItem(){

        data.add(null);

        notifyItemInserted(data.size() - 1);
    }


    public void removeLast(){
        data.remove(data.size() - 1);

        notifyItemRemoved(data.size());
    }


    public void remove(T object){

        int idx = data.indexOf(object);

        data.remove(object);

        notifyItemRemoved(idx);
    }


    public void add(T object){

        data.add(object);

        notifyItemInserted(data.size());
    }


    public void update(T object, T newObject){

        int idx = data.indexOf(object);

        data.set(idx, newObject);

        notifyItemChanged(idx);
    }


    public List<T> getData(){
        return data;
    }


    public interface OnClickListItemListener<T>{

        void onListItemClick(T item);

    }


    public interface OnClickActionButtonListener<T>{

        void onActionButtonClick(T item);

    }


}

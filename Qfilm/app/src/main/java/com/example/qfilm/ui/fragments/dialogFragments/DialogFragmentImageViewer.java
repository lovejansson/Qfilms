package com.example.qfilm.ui.fragments.dialogFragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;


import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.Image;
import com.example.qfilm.data.models.typeconverters.TypeConverters;
import com.example.qfilm.ui.adapters.ImagesRecyclerViewAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


public class DialogFragmentImageViewer extends DialogFragment {

    private static final String TAG = "DialogFragmentImageView";

    private List<Image> images;
    private ImageView ivCloseDialog;
    private ViewPager2 viewPager;
    private Integer startIndex;
    private ImageButton buttonNextLeft;
    private ImageButton buttonNextRight;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){

            images = new TypeConverters().toImageList(getArguments().getString("images"));

            startIndex = getArguments().getInt("startIndex");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.dialog_fragment_image_viewer, container, false);

        ivCloseDialog = view.findViewById(R.id.iv_close_dialog);

        viewPager = view.findViewById(R.id.viewpager);

        buttonNextLeft = view.findViewById(R.id.left_nav);

        buttonNextRight = view.findViewById(R.id.right_nav);

        setupOnClickListeners();

        setupAdapter();

        updateArrowsVisibility(startIndex, ((ImagesRecyclerViewAdapter) viewPager.getAdapter()).getData().size() - 1);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return view;

    }


    private void setupAdapter() {

        ImagesRecyclerViewAdapter rvAdapter = new ImagesRecyclerViewAdapter(null, null,
                R.layout.image_item_big);

        viewPager.setAdapter(rvAdapter);

        rvAdapter.setData(images);

        viewPager.setCurrentItem(startIndex);

    }


    private void setupOnClickListeners(){

        buttonNextRight.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                int currentIndex = viewPager.getCurrentItem();

                int lastIndex = ((ImagesRecyclerViewAdapter) viewPager.getAdapter()).getData().size() - 1;

                if (currentIndex <  lastIndex) {
                    viewPager.setCurrentItem(++currentIndex);
                    updateArrowsVisibility(currentIndex, lastIndex);

                }

            }
        });

        buttonNextLeft.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                int currentIndex = viewPager.getCurrentItem();
                int lastIndex = ((ImagesRecyclerViewAdapter) viewPager.getAdapter()).getData().size() - 1;

                if (currentIndex > 0) {
                    viewPager.setCurrentItem(--currentIndex);
                }

                updateArrowsVisibility(currentIndex, lastIndex);

            }
        });

        ivCloseDialog.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }


    private void updateArrowsVisibility(int index, int lastIndex){

        int nextLastIndex = lastIndex - 1;

        if(index == 0)
            buttonNextLeft.setVisibility(View.GONE);
        else if(index == 1)
            buttonNextLeft.setVisibility(View.VISIBLE);


        if(index == nextLastIndex)
            buttonNextRight.setVisibility(View.VISIBLE);
        if(index == lastIndex)
            buttonNextRight.setVisibility(View.GONE);

    }


    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

}

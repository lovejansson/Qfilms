package com.example.qfilm.utils;

import android.util.Log;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.qfilm.R;

public class TextColorBindingAdapter {

    private static final String TAG = "TextColorBindingAdapter";

    @BindingAdapter("textColor")
    public static void setTextColor(TextView textView, Boolean isSelected){

        textView.setTextColor(textView.getContext().getResources().getColorStateList(R.color.icon_selector, null));

        if(isSelected){

            textView.setSelected(true);

        }else{

            textView.setSelected(false);
        }

    }

}

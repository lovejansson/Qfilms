package com.example.qfilm.ui.adapters;
import android.widget.ImageView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.qfilm.R;


/**
 * Binds image to ImageView where in layout: app:imageUrl
 *
 * **/

public class GlideBindingAdapter {

    private static final String TAG = "GlideBindingAdapter";

    @BindingAdapter("imageUrl")

    public static void glide(ImageView view, String url) {


        Glide.with(view.getContext())
                .load("https://image.tmdb.org/t/p/w780" + url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_sharp_local_movies_24)
                .into(view);

    }

}

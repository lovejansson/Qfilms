package com.example.qfilm.ui.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.MediaType;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.ui.adapters.MyFragmentPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;
import java.util.ListIterator;


public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private ListingFragment moviesFragment;

    private ListingFragment seriesFragment;

    public HomeFragment(ListingFragment moviesFragment, ListingFragment seriesFragment) {

        this.moviesFragment = moviesFragment;

        this.seriesFragment = seriesFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


            View view = inflater.inflate(R.layout.fragment_home, container, false);

            ViewPager2 viewPager = view.findViewById(R.id.container_tab_fragment);

            MyFragmentPagerAdapter adapter =  new MyFragmentPagerAdapter(this);

            adapter.addFragment(moviesFragment);

            adapter.addFragment(seriesFragment);

            viewPager.setAdapter(adapter);

            TabLayout tabLayout = view.findViewById(R.id.tabs);

            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {

                switch (position){

                    case 0:
                        tab.setText(getResources().getString(R.string.tab_movies));

                        break;

                    case 1:
                        tab.setText(getResources().getString(R.string.tab_series));
                        break;

                }}).attach();

        return view;
    }

}
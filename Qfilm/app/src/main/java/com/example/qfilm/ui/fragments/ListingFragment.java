package com.example.qfilm.ui.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.data.models.entities.MediaType;
import com.example.qfilm.data.models.entities.Result;

import com.example.qfilm.repositories.utils.DataResource;

import com.example.qfilm.databinding.FragmentListingBinding;

import com.example.qfilm.ui.adapters.BaseRecyclerViewAdapter;
import com.example.qfilm.ui.fragments.dialogFragments.DialogFragmentGenres;
import com.example.qfilm.ui.utils.ColumnSpacingDecoration;
import com.example.qfilm.ui.utils.MyFragmentFactory;
import com.example.qfilm.ui.utils.UiUtil;
import com.example.qfilm.ui.utils.navigation.NavigationInterface;
import com.example.qfilm.ui.adapters.ResultRecyclerViewAdapter;

import com.example.qfilm.viewmodels.utils.MyViewModelFactory;
import com.example.qfilm.viewmodels.ResultWithGenreViewModel;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import java.util.List;
import utils.MyIdlingResource;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

/**
 *
 * uses ResultWithGenreViewModel to get movies or series of different genres
 *
 * **/

public class ListingFragment extends Fragment implements BaseRecyclerViewAdapter.OnClickListItemListener, DialogFragmentGenres.SelectGenreInterface {

    private static final String TAG = "ListingFragment";

    private NavigationInterface navigationInterface;

    private FragmentListingBinding binding;

    private Boolean isNextPageQuery;

    private Boolean dialogShowing;

    private Genre currentGenre;

    private String currentLanguage;

    private MediaType mediaType;

    private ResultRecyclerViewAdapter rvAdapter;

    private MyViewModelFactory myViewModelFactory;

    private ResultWithGenreViewModel viewModel;

    private Observer<DataResource<List<Result>>> observerResults;

    private LiveData<DataResource<List<Result>>> observableResults;

    private View view;

    private Trace traceNewPageFetch;

    private Trace traceNewGenreFetch;

    // used to determine how to retry a fetch when error happens.
    private enum Operation{
        NEW_GENRE,
        NEXT_PAGE
    }

    private Operation previousOperation;


    public ListingFragment(MediaType mediaType, MyViewModelFactory myViewModelFactory) {

        this.mediaType = mediaType;

        this.myViewModelFactory = myViewModelFactory;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        navigationInterface = (NavigationInterface) requireActivity();

        viewModel = new ViewModelProvider(this, myViewModelFactory).get(ResultWithGenreViewModel.class);

        String language = requireActivity().getSharedPreferences("com.example.qfilm", MODE_PRIVATE).getString("language", "English");

        currentLanguage = language.substring(0, 2).toLowerCase();

        observerResults = new Observer<DataResource<List<Result>>>() {

            @Override
            public void onChanged(DataResource<List<Result>> result) {

                if (result != null) {
                    handleResponse(result.getStatus(), result.getData());
                }
            }
        };

        observableResults = viewModel.getResultsObservable();

        isNextPageQuery = false;

        dialogShowing = false;

        MyIdlingResource.increment(); // testing

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // save view state for when user navigates back

       if(view == null){

           binding = DataBindingUtil.inflate(inflater, R.layout.fragment_listing, container, false);

           view = binding.getRoot();

           binding.errorMessage.findViewById(R.id.btn_try_again).setOnClickListener(new View.OnClickListener(){

               @Override
               public void onClick(View view) {
                   retryFetch();
               }
           });

           setupRecyclerView();

           setupGenreSelection();

           setInitialVisibilities();

           setupFirebasePerformanceTraces();

           viewModel.fetchResultsWithGenre(mediaType, currentGenre, currentLanguage);

           observableResults.observeForever(observerResults);


       }

        return view;

    }


    private void setupFirebasePerformanceTraces() {

        traceNewPageFetch = FirebasePerformance.getInstance().newTrace("listing_fragment_new_page_fetch");

        traceNewGenreFetch = FirebasePerformance.getInstance().newTrace("listing_fragment_new_genre_fetch");
    }


    private void setupGenreSelection() {

        currentGenre = new Genre(-1, getResources().getString(R.string.all_genres));

        binding.setSelectedGenre(currentGenre.getName());

        binding.btnSelectGenre.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(!dialogShowing) {

                    dialogShowing = true;

                    MyFragmentFactory fragmentFactory =
                            (MyFragmentFactory) requireActivity().getSupportFragmentManager().getFragmentFactory();

                    fragmentFactory.setMediaType(mediaType);

                    fragmentFactory.setGenre(currentGenre);

                    DialogFragmentGenres dialogFragmentGenres = (DialogFragmentGenres) fragmentFactory.instantiate(ClassLoader.getSystemClassLoader(),
                            DialogFragmentGenres.class.getSimpleName());

                    dialogFragmentGenres.show(getChildFragmentManager(), null);

                }
            }
        });

    }


    private void setInitialVisibilities() {

        binding.progressBar.setVisibility(View.GONE);

        binding.errorMessage.setVisibility(GONE);

        binding.rvResults.setVisibility(GONE);

    }


    private void setupRecyclerView() {

        // the user can choose to show titles in the original language or english

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.example.qfilm",
                MODE_PRIVATE);

        Boolean showOriginalTitles = sharedPreferences.getBoolean("titles", false);

        rvAdapter = new ResultRecyclerViewAdapter(this, null,
                R.layout.recyclerview_item_result, showOriginalTitles);

        binding.rvResults.setAdapter(rvAdapter);


        // setting up scroll listener for pagination

        binding.rvResults.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);

                if(!binding.rvResults.canScrollVertically(1)){

                    if(!isNextPageQuery){

                        isNextPageQuery = true;

                        getNextPage();

                    }

                }
            }
        });


        // movies/series are shown in two columns when the device is tablet

        if(requireActivity().getResources().getBoolean(R.bool.is_tablet)){

            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);

            binding.rvResults.setLayoutManager(layoutManager);

            int space = (int) ((getResources().getDimensionPixelSize(R.dimen.margin_outer_small)) /
                    (getResources().getDisplayMetrics().density));

            binding.rvResults.addItemDecoration(new ColumnSpacingDecoration(space, 2));

            // progressbar takes upp all columns

            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {

                    int layout = rvAdapter.getItemViewType(position);

                    if (layout == R.layout.recyclerview_item_progress_bar) {
                        return 2;
                    } else {
                        return 1;
                    }
                }
            });


        }else{

            binding.rvResults.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        }

    }


    private void getNextPage() {
        
        if(!viewModel.getReachedEndOfList()){

            // testing
            MyIdlingResource.increment();

            // for progressbar
            rvAdapter.appendNullItem();

            binding.rvResults.smoothScrollToPosition(rvAdapter.getItemCount() - 1);

            traceNewPageFetch.start(); // firebase performance

            viewModel.fetchNextPage(mediaType, currentGenre, currentLanguage);


        }else{
            isNextPageQuery = false;
        }

    }


    private void retryFetch() {

        binding.errorMessage.setVisibility(GONE);

        if(previousOperation == Operation.NEW_GENRE) {

            binding.progressBar.setVisibility(View.VISIBLE);

            viewModel.fetchResultsWithGenre(mediaType, currentGenre, currentLanguage);
        }

        else{

            viewModel.setCurrentPage(viewModel.getCurrentPage() - 1);

            if(!isNextPageQuery){

                isNextPageQuery = true;

                binding.rvResults.setVisibility(View.VISIBLE);

                getNextPage();
            }

        }

    }


    private void handleResponse(DataResource.Status status, List<Result> data) {

        /**
        * Updating UI according data response (empty or got data) and data status (success, loading, error)
        **/

        switch(status){

            case SUCCESS:

                if(isNextPageQuery){

                    rvAdapter.removeLast();

                    if(!data.isEmpty()){

                        rvAdapter.appendData(data);

                    }

                    isNextPageQuery = false;

                    traceNewPageFetch.stop(); // firebase performance

                }else{

                    binding.rvResults.setVisibility(View.VISIBLE);

                    binding.progressBar.setVisibility(GONE);

                    rvAdapter.setData(data);

                    traceNewGenreFetch.stop(); // firebase performance


                }


                break;

            case LOADING:

                if(!isNextPageQuery){

                    binding.progressBar.setVisibility(View.VISIBLE);

                }

                break;

            case ERROR:

                if(isNextPageQuery){

                    traceNewPageFetch.stop(); // firebase performance

                    rvAdapter.removeLast();
                    
                    if(data != null){

                        rvAdapter.appendData(data);

                        binding.rvResults.setVisibility(View.VISIBLE);


                    }else{

                        UiUtil.displayErrorMessage(requireContext(), binding.errorMessage);

                        binding.rvResults.setVisibility(GONE);

                        previousOperation = Operation.NEXT_PAGE;

                    }

                    isNextPageQuery = false;

                }else {

                    traceNewGenreFetch.stop(); // firebase performance

                    if (data != null) {

                        rvAdapter.setData(data);

                        binding.rvResults.setVisibility(View.VISIBLE);

                    } else {

                        previousOperation = Operation.NEW_GENRE;

                        UiUtil.displayErrorMessage(requireContext(), binding.errorMessage);
                    }
                }

                binding.progressBar.setVisibility(GONE);

                break;
        }

        MyIdlingResource.decrement(); // testing

    }


    // implemented interface from DialogFragmentGenres

    @Override
    public void selectGenre(@Nullable Genre genre) {

        dialogShowing = false;

        if(genre != null && genre != currentGenre) {

            currentGenre = genre;

            binding.errorMessage.setVisibility(GONE);

            binding.setSelectedGenre(currentGenre.getName());

            binding.rvResults.scrollToPosition(0);

            binding.rvResults.setVisibility(GONE);

            viewModel.fetchResultsWithGenre(mediaType, currentGenre, currentLanguage);

            traceNewGenreFetch.start(); // firebase performance

        }else{
            MyIdlingResource.decrement(); // testing
        }

    }


    @Override
    public void onListItemClick(Object item) {

        navigationInterface.navigateToDetailPageFragment((Result) item);

    }


    @Override
    public void onDestroy() {

        observableResults.removeObserver(observerResults);

        super.onDestroy();

    }
}



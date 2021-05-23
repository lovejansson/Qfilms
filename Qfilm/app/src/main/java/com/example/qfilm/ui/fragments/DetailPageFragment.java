package com.example.qfilm.ui.fragments;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;


import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.Collection;
import com.example.qfilm.data.models.entities.Image;
import com.example.qfilm.data.models.entities.Images;
import com.example.qfilm.data.models.entities.MediaType;
import com.example.qfilm.data.models.entities.MovieDetails;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.data.models.entities.SeriesDetails;
import com.example.qfilm.data.models.typeconverters.TypeConverters;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.databinding.FragmentDetailsPageBinding;

import com.example.qfilm.ui.adapters.BaseRecyclerViewAdapter;
import com.example.qfilm.ui.fragments.dialogFragments.DialogFragmentAddToCollection;
import com.example.qfilm.ui.fragments.dialogFragments.DialogFragmentImageViewer;
import com.example.qfilm.ui.utils.UiUtil;
import com.example.qfilm.ui.utils.navigation.NavigationInterface;
import com.example.qfilm.ui.adapters.ImagesRecyclerViewAdapter;

import com.example.qfilm.viewmodels.DetailsViewModel;
import com.example.qfilm.viewmodels.FireStoreViewModel;
import com.example.qfilm.viewmodels.utils.MyViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import java.util.List;

import utils.MyIdlingResource;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

/**
 * uses DetailsViewModel to get detail info about movie series
 * **/

public class DetailPageFragment extends DialogFragment implements RecyclerView.OnScrollChangeListener,
        BaseRecyclerViewAdapter.OnClickListItemListener, DialogFragmentAddToCollection.AddToCollectionInterface {

    private static final String TAG = "DetailPageFragment";

    private Result result;

    private NavigationInterface navigationInterface;

    private FragmentDetailsPageBinding binding;

    private DetailsViewModel detailsViewModel;

    private FireStoreViewModel fireStoreViewModel;

    private Boolean overviewCollapsed;
    
    private Observer<DataResource<FireStoreViewModel.FireStoreEdit>> observerFireStoreEdits;

    private View view;

    private Collection addedToCollection;

    private FirebaseAuth firebaseAuth;

    private MyViewModelFactory myViewModelFactory;

    private Trace traceDetailsFetch;

    private String currentLanguage;

    private ImagesRecyclerViewAdapter rvAdapterBackdrops;

    private ImagesRecyclerViewAdapter rvAdapterPosters;

    public DetailPageFragment(Result result, FirebaseAuth firebaseAuth, MyViewModelFactory myViewModelFactory) {

        this.result = result;
        this.firebaseAuth = firebaseAuth;
        this.myViewModelFactory = myViewModelFactory;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        detailsViewModel = new ViewModelProvider(this, myViewModelFactory).get(DetailsViewModel.class);

        fireStoreViewModel = new ViewModelProvider(this, myViewModelFactory).get(FireStoreViewModel.class);

        navigationInterface = (NavigationInterface) requireActivity();

        overviewCollapsed = true;
        String language = requireActivity().getSharedPreferences("com.example.qfilm", MODE_PRIVATE).getString("language", "English");

        currentLanguage = language.substring(0, 2).toLowerCase();
        
        observerFireStoreEdits = new Observer<DataResource<FireStoreViewModel.FireStoreEdit>>() {
            @Override
            public void onChanged(DataResource<FireStoreViewModel.FireStoreEdit> dataResourceLiveData) {
                
                handleResponseFireStoreEdit(dataResourceLiveData);

                fireStoreViewModel.getFireStoreEditsObservable().removeObserver(observerFireStoreEdits);
            }
        };


        traceDetailsFetch = FirebasePerformance.getInstance().newTrace("trace_details_fetch"); // firebase performance

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(view == null) {

            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details_page, container, false);

            view = binding.getRoot();

            // The user can choose to show original title or title in english

            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.example.qfilm",
                    Context.MODE_PRIVATE);

            Boolean showOriginalTitles = sharedPreferences.getBoolean("titles", false);

            binding.setShowOriginalTitles(showOriginalTitles);

            setBackDropImageViewSize();

            setOnClickListeners();

            observeDetailsResult();

            binding.progressBar.setVisibility(View.VISIBLE);

            binding.errorMessage.setVisibility(GONE);

            binding.rvPosters.setOnScrollChangeListener(this);

            binding.rvImages.setOnScrollChangeListener(this);

        }

        return view;

    }


    @Override
    public void onStart()
    {
        super.onStart();

        // shown as dialog on tablets that dont cover full page

        if(getResources().getBoolean(R.bool.is_tablet)) {

            Dialog dialog = getDialog();

            if (dialog != null) {

                UiUtil.setDialogFragmentDimensions(dialog);

                dialog.getWindow().setWindowAnimations(R.style.Window_DialogAnimation);
            }
        }
    }


    private void setOnClickListeners(){

        binding.btnAddTo.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(UiUtil.isSignedIn(firebaseAuth)){

                    DialogFragmentAddToCollection dialogFragmentAddToCollection = new DialogFragmentAddToCollection();

                    dialogFragmentAddToCollection.show(getChildFragmentManager(), null);

                }else{

                    UiUtil.showSnackBarWithLabel(getString(R.string.add_to_collection_not_logged_in),
                            requireView(), 5000);
                }

            }

        });


        binding.btnTrailer.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                navigationInterface.navigateToTrailerFragment(binding.getDetails().getTrailer());
            }
        });


        binding.ivCloseDetailPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(getResources().getBoolean(R.bool.is_tablet)){

                    dismiss();
                }else {

                    navigationInterface.onBackPressed();
                }
            }
        });


        binding.errorMessage.findViewById(R.id.btn_try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(result.getMediaType() == MediaType.SERIES){
                    detailsViewModel.getSeriesDetails(result.getResultId(), currentLanguage);

                }else{

                    detailsViewModel.getMovieDetails(result.getResultId(), currentLanguage);
                }

            }
        });

    }


    private void setBackDropImageViewSize() {

       /*
        setting width of ImageView for backdrop image to be as wide as the window and calculating height based on it.
        this is because i want the image to take up the appropriate area before fully loaded so that page don't jump
        */

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                binding.ivBackdropImage.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        (int) (binding.fragmentDetailsPage.getWidth() / 1.777778)));

            }
        });

    }


    private void handleResponseFireStoreEdit(DataResource<FireStoreViewModel.FireStoreEdit> dataResourceLiveData) {

        String message = "";

        switch(dataResourceLiveData.getStatus()){

            case SUCCESS:

                // added to existing collection or to new collection

                message = getString(R.string.add_item_to_collection_success, result.getTitle(), addedToCollection.getName());

                break;

            case ERROR:

                switch(dataResourceLiveData.getData()){

                    // failed to either add item to existing collection, to a new collection or to create new collection.

                    case ADD_ITEM:

                        if(dataResourceLiveData.getMessage().equals("already added")){
                            message = getString(R.string.item_already_added, result.getTitle(),
                                    addedToCollection.getName());
                        }else{
                            message = getString(R.string.add_item_to_collection_failed, result.getTitle(), addedToCollection.getName());
                        }

                        break;

                    case ADD_COLLECTION_WITH_ITEM:
                        message = getString(R.string.new_collection_with_item_failed, addedToCollection.getName(), result.getTitle());
                        break;

                    case ADD_EMPTY_COLLECTION:
                        message = getString(R.string.create_empty_collection_failed, addedToCollection.getName());
                        break;
                }

                break;
        }

        UiUtil.showSnackBarWithLabel(message, requireView(), 2000);

    }


    private void observeDetailsResult() {

        if(result.getMediaType() == MediaType.SERIES){

            observeSeriesDetailsResult();

            detailsViewModel.getSeriesDetails(result.getResultId(), currentLanguage);

        }else{

            observeMovieDetailsResult();

            detailsViewModel.getMovieDetails(result.getResultId(), currentLanguage);

        }

        traceDetailsFetch.start();
    }


    private void observeMovieDetailsResult() {

        detailsViewModel.getMovieDetailsLiveData().observe(getViewLifecycleOwner(), new Observer<DataResource<MovieDetails>>() {
            @Override
            public void onChanged(DataResource<MovieDetails> movieDetailsDataResource) {

                if(movieDetailsDataResource != null){

                    MovieDetails movieDetails = movieDetailsDataResource.getData();

                    switch(movieDetailsDataResource.getStatus()){

                        case SUCCESS:

                            setupExpandableOverview(movieDetails.getOverview());

                            setupImagesRecyclerViews(movieDetails.getImages());

                            binding.setDetails(movieDetails);

                            binding.executePendingBindings();

                            binding.progressBar.setVisibility(GONE);

                            binding.content.setVisibility(View.VISIBLE);

                            binding.errorMessage.setVisibility(GONE);

                            traceDetailsFetch.stop();

                            break;

                        case LOADING:

                            binding.progressBar.setVisibility(View.VISIBLE);

                            binding.errorMessage.setVisibility(GONE);

                            break;

                        case ERROR:

                            binding.progressBar.setVisibility(GONE);

                            // showing old data if present

                            if(movieDetails != null) {

                                setupImagesRecyclerViews(movieDetails.getImages());

                                setupExpandableOverview(movieDetails.getOverview());

                                binding.setDetails(movieDetails);

                                binding.content.setVisibility(View.VISIBLE);

                                traceDetailsFetch.stop();


                            }else{

                                UiUtil.displayErrorMessage(requireContext(), binding.errorMessage);
                            }

                            break;
                    }


                    MyIdlingResource.decrement(); // testing

                }
            }

        });
    }


    private void observeSeriesDetailsResult() {

        detailsViewModel.getSeriesDetailsLiveData().observe(getViewLifecycleOwner(), new Observer<DataResource<SeriesDetails>>() {
            @Override
            public void onChanged(DataResource<SeriesDetails> seriesDetailsDataResource) {

                if(seriesDetailsDataResource != null){

                    SeriesDetails seriesDetails = seriesDetailsDataResource.getData();

                    switch(seriesDetailsDataResource.getStatus()){

                        case SUCCESS:

                            binding.setDetails(seriesDetails);

                            setupImagesRecyclerViews(seriesDetails.getImages());

                            setupExpandableOverview(seriesDetails.getOverview());

                            binding.progressBar.setVisibility(GONE);

                            binding.content.setVisibility(View.VISIBLE);

                            traceDetailsFetch.stop();

                            break;

                        case LOADING:

                            binding.content.setVisibility(GONE);

                            break;

                        case ERROR:


                            traceDetailsFetch.stop();

                            binding.progressBar.setVisibility(GONE);

                            if(seriesDetails != null) {

                                binding.setDetails(seriesDetails);

                                setupImagesRecyclerViews(seriesDetails.getImages());

                                setupExpandableOverview(seriesDetails.getOverview());

                                binding.content.setVisibility(View.VISIBLE);


                            }else{

                               UiUtil.displayErrorMessage(requireContext(), binding.errorMessage);

                            }

                            break;
                    }
                }
            }
        });
    }


    private void setupExpandableOverview(String overview) {

        if(overview.length() < 250){

            binding.ivIconExpandTextView.setVisibility(GONE);
        }else{

            binding.tvOverview.setMaxLines(4);

            binding.ivIconExpandTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    ObjectAnimator animator;

                    if (overviewCollapsed) {

                        animator = ObjectAnimator.ofInt(binding.tvOverview, "maxLines",
                                binding.tvOverview.getLineCount());

                        binding.ivIconExpandTextView.setRotation(90);


                    } else {

                        animator = ObjectAnimator.ofInt(binding.tvOverview, "maxLines",
                                4);
                        binding.ivIconExpandTextView.setRotation(270);


                    }

                    animator.setDuration(250);

                    animator.start();

                    overviewCollapsed = !overviewCollapsed;
                }

            });
        }

    }


    private void setupImagesRecyclerViews(Images images){

        if(images != null) {

             rvAdapterBackdrops =
                    createImageAdapter(images.getBackdrops(), R.layout.image_item);

            binding.rvImages.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));

            binding.rvImages.setAdapter(rvAdapterBackdrops);


            if(images.getPosters().size() > 0) {

                 rvAdapterPosters =
                        createImageAdapter(images.getPosters(), R.layout.recyclerview_item_poster);

                binding.rvPosters.setLayoutManager(new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL, false));

                binding.rvPosters.setAdapter(rvAdapterPosters);

            }else{
                binding.rvPosters.setVisibility(GONE);
            }

        }
    }


    private ImagesRecyclerViewAdapter createImageAdapter(List<Image> images, int layout) {

        ImagesRecyclerViewAdapter rvAdapterImages = new ImagesRecyclerViewAdapter(this, null,
                layout);

        rvAdapterImages.setData(images);

        return rvAdapterImages;

    }


    @Override
    public void onScrollChange(View view, int i, int i1, int i2, int i3) {

        if(!binding.rvImages.canScrollHorizontally(1)){

            binding.rvImages.setPadding(0, 0, 24, 0);

        }else{

            binding.rvImages.setPadding(0, 0, 0, 0);
        }


        if(!binding.rvPosters.canScrollHorizontally(1)){

            binding.rvPosters.setPadding(0, 0, 24, 0);

        }else{

            binding.rvPosters.setPadding(0, 0, 0, 0);
        }
    }


    @Override
    public void onListItemClick(Object item) {

        // can show images in bigger format when on phone

        if(!getResources().getBoolean(R.bool.is_tablet)) {

            Image image = (Image) item;

            DialogFragmentImageViewer dialogFragmentImageViewer = new DialogFragmentImageViewer();

            Bundle bundle = new Bundle();


            if (rvAdapterBackdrops.getData().contains(image)) {

                bundle.putString("images",
                        new TypeConverters().toImageString(((ImagesRecyclerViewAdapter) binding.rvImages.getAdapter()).getData()));

                bundle.putInt("startIndex", ((ImagesRecyclerViewAdapter) binding.rvImages.getAdapter()).getData().indexOf(image));
            } else {
                bundle.putString("images",
                        new TypeConverters().toImageString(((ImagesRecyclerViewAdapter) binding.rvPosters.getAdapter()).getData()));

                bundle.putInt("startIndex", ((ImagesRecyclerViewAdapter) binding.rvPosters.getAdapter()).getData().indexOf(image));

            }

            dialogFragmentImageViewer.setArguments(bundle);

            dialogFragmentImageViewer.show(getChildFragmentManager(), null);

        }


    }


    // implemented interface from DialogFragmentAddToCollection

    @Override
    public void AddToCollection(Collection collection) {

        addedToCollection = collection;

        fireStoreViewModel.saveToCollection(collection, result);

        fireStoreViewModel.getFireStoreEditsObservable().observe(getViewLifecycleOwner(), observerFireStoreEdits);

    }


    @Override
    public void AddToNewCollection(String collectionName) {

       addedToCollection = fireStoreViewModel.createNewCollection(collectionName, result);

       fireStoreViewModel.addNewCollection(addedToCollection, result);

       fireStoreViewModel.getFireStoreEditsObservable().observe(getViewLifecycleOwner(), observerFireStoreEdits);

    }

}


package com.example.qfilm.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.Collection;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.databinding.FragmentCollectionBinding;
import com.example.qfilm.ui.adapters.BaseRecyclerViewAdapter;
import com.example.qfilm.ui.adapters.ResultRecyclerViewAdapter;
import com.example.qfilm.ui.fragments.dialogFragments.DialogFragmentConfirmation;
import com.example.qfilm.ui.utils.UiUtil;
import com.example.qfilm.ui.utils.navigation.NavigationInterface;
import com.example.qfilm.viewmodels.FireStoreViewModel;
import com.example.qfilm.viewmodels.utils.MyViewModelFactory;

import java.util.List;

import utils.MyIdlingResource;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;


/**
 *
 * this is the page where a user's collection is shown. It fetches data via FireStoreViewModel and
 * is listening to changes made to the collection.
 *
 * **/
public class CollectionFragment extends Fragment implements BaseRecyclerViewAdapter.OnClickListItemListener,
        BaseRecyclerViewAdapter.OnClickActionButtonListener,
        DialogFragmentConfirmation.ConfirmActionInterface{

    private static final String TAG = "CollectionFragment";

    private NavigationInterface navigationInterface;

    private FireStoreViewModel viewModel;

    private ResultRecyclerViewAdapter rvAdapter;

    private FragmentCollectionBinding binding;

    private Collection collection;

    private Boolean isNextPageQuery;

    private Result clickedResultItem;

    private View view;

    private Boolean showOriginalTitles;

    private Observer<DataResource<FireStoreViewModel.FireStoreEdit>> observerFireStoreEdit;

    private Observer<DataResource<List<Result>>> observerCollection;

    private LiveData<DataResource<FireStoreViewModel.FireStoreEdit>> observableFireStoreEdit;

    private LiveData<DataResource<List<Result>>> observableCollection;

    private MyViewModelFactory myViewModelFactory;

    private String currentLanguage;


    public CollectionFragment(Collection collection, MyViewModelFactory myViewModelFactory) {

        this.collection = collection;

        this.myViewModelFactory = myViewModelFactory;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String language = requireActivity().getSharedPreferences("com.example.qfilm", MODE_PRIVATE).getString("language", "English");

        currentLanguage = language.substring(0, 2).toLowerCase();

        navigationInterface = (NavigationInterface) requireActivity();

        isNextPageQuery = false;

        viewModel = new ViewModelProvider(this, myViewModelFactory).get(FireStoreViewModel.class);

        observableFireStoreEdit = viewModel.getFireStoreEditsObservable();

        observableCollection = viewModel.getCollectionItemsObservable();

        observerCollection = new Observer<DataResource<List<Result>>>() {
            @Override
            public void onChanged(DataResource<List<Result>> listDataResource) {

                handleResponseCollectionItems(listDataResource);
            }
        };


        observerFireStoreEdit = new Observer<DataResource<FireStoreViewModel.FireStoreEdit>>() {
            @Override
            public void onChanged(DataResource<FireStoreViewModel.FireStoreEdit> firebaseEditDataResource) {
                handleResponseFireStoreEdit(firebaseEditDataResource);
            }
        };

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(view == null) {

            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_collection, container, false);

            view = binding.getRoot();

            setupRecyclerView();

            setInitialVisibilities();

            setupOnClickListeners();

            MyIdlingResource.increment(); // testing

            viewModel.fetchCollectionItems(collection.getDocumentId(), currentLanguage);

            observableCollection.observeForever(observerCollection);

            observableFireStoreEdit.observeForever(observerFireStoreEdit);

        }

        return view;

    }


    private void setupOnClickListeners(){

        binding.errorMessage.findViewById(R.id.btn_try_again).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                viewModel.fetchCollectionItems(collection.getDocumentId(), currentLanguage);
            }
        });

        binding.arrowBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                navigationInterface.onBackPressed();
            }
        });
    }


    private void setInitialVisibilities() {

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.rvResults.setVisibility(GONE);
        binding.errorMessage.setVisibility(GONE);
        binding.llEmptyCollection.setVisibility(View.GONE);

    }


    private void setupRecyclerView() {

        // the user can choose to show titles in the original language or english

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.example.qfilm",
                Context.MODE_PRIVATE);

        showOriginalTitles = sharedPreferences.getBoolean("titles", false);

        rvAdapter = new ResultRecyclerViewAdapter(this, this, R.layout.recyclerview_item_result_action_button,
                showOriginalTitles);

        binding.rvResults.setAdapter(rvAdapter);

        binding.rvResults.setItemAnimator(null);


        // setting up scroll listener for pagination

        binding.rvResults.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);

                if(!binding.rvResults.canScrollVertically(1)){

                    if(!viewModel.getReachedEndOfList() && !isNextPageQuery) {

                        isNextPageQuery = true;

                        rvAdapter.appendNullItem(); // progressbar

                        binding.rvResults.smoothScrollToPosition(rvAdapter.getItemCount() - 1);

                        MyIdlingResource.increment(); // testing

                        viewModel.fetchCollectionItems(collection.getDocumentId(), currentLanguage);

                    }

                }
            }
        });


        // movies/series are shown in two columns when the device is tablet

        if(requireActivity().getResources().getBoolean(R.bool.is_tablet)){
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);

            binding.rvResults.setLayoutManager(layoutManager);

        }else{

            binding.rvResults.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        }

    }


    private void handleResponseFireStoreEdit(DataResource<FireStoreViewModel.FireStoreEdit> firebaseEditDataResource) {

        switch(firebaseEditDataResource.getStatus()){

            case SUCCESS:

                // item was deleted, adjusts poster path of oldest item if the item is the first in list

                if(rvAdapter.getData().indexOf(clickedResultItem) == 0){

                    if(rvAdapter.getData().size() == 1){
                        viewModel.updatePosterPathOfOldestItem(collection, null);
                    }else{
                        viewModel.updatePosterPathOfOldestItem(collection, rvAdapter.getData().get(1).getPosterPath());
                    }

                    // also set visiblity of recyclerview and information about empty collection

                    binding.llEmptyCollection.setVisibility(View.VISIBLE);
                    binding.rvResults.setVisibility(GONE);

                }

                rvAdapter.remove(clickedResultItem);


                break;

            case ERROR:

                UiUtil.showSnackBarWithLabel(getString(R.string.delete_collection_failed, clickedResultItem.getTitle()), requireView(), 3000);

                break;
        }

    }


    private void handleResponseCollectionItems(DataResource<List<Result>> listDataResource) {

        switch(listDataResource.getStatus()){

            case SUCCESS:

                if(isNextPageQuery){

                    isNextPageQuery = false;

                    rvAdapter.removeLast(); // progressbar

                    if(!listDataResource.getData().isEmpty()) {

                        rvAdapter.appendData(listDataResource.getData());

                    }

                }else {

                    binding.setCollectionName(collection.getName());

                    if (!listDataResource.getData().isEmpty()) {

                        rvAdapter.setData(listDataResource.getData());

                        binding.rvResults.setVisibility(View.VISIBLE);
                        binding.llEmptyCollection.setVisibility(View.GONE);
                        binding.errorMessage.setVisibility(GONE);

                    }else{
                        binding.llEmptyCollection.setVisibility(View.VISIBLE);
                    }

                    binding.progressBar.setVisibility(GONE);

                }


                break;

            case ERROR:

                binding.progressBar.setVisibility(GONE);

                UiUtil.displayErrorMessage(requireContext(), binding.errorMessage);

                break;

        }

        MyIdlingResource.decrement(); // testing
    }



    @Override
    public void onListItemClick(Object item) {

        navigationInterface.navigateToDetailPageFragment((Result) item);
    }


    // the action a user can perform for now is deleting the item

    @Override
    public void onActionButtonClick(Object item) {

        clickedResultItem = (Result) item;

        String message = getString(R.string.dialog_message_delete_from_collection,
                showOriginalTitles ? clickedResultItem.getOriginalTitle() : clickedResultItem.getTitle());

        DialogFragmentConfirmation dialogFragmentSimple = DialogFragmentConfirmation.newInstance(message);

        dialogFragmentSimple.show(getChildFragmentManager(), null);


    }


    @Override
    public void doAction() {

        viewModel.deleteFromCollection(collection, clickedResultItem);

    }


    @Override
    public void onDestroy() {

        observableCollection.removeObserver(observerCollection);

        observableFireStoreEdit.removeObserver(observerFireStoreEdit);

        super.onDestroy();
    }
}

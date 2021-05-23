package com.example.qfilm.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.Collection;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.ui.adapters.BaseRecyclerViewAdapter;
import com.example.qfilm.ui.adapters.CollectionsRecyclerViewAdapter;
import com.example.qfilm.ui.utils.UiUtil;
import com.example.qfilm.viewmodels.FireStoreViewModel;
import com.example.qfilm.viewmodels.utils.MyViewModelFactory;

import java.util.List;

import utils.MyIdlingResource;

import static android.view.View.GONE;

/**
 *
 * this is the page where a user's list of collections is shown. It fetches data via FireStoreViewModel and
 * is listening to changes made to the collections.
 *
 * **/

public class CollectionsFragment extends Fragment {

    private static final String TAG = "CollectionsListFragment";

    private FireStoreViewModel viewModel;

    private CollectionsRecyclerViewAdapter rvAdapter;

    private View view;

    private ProgressBar progressBar;

    private LinearLayout errorLayout;

    private RecyclerView rvCollections;

    private LinearLayout llNoSavedCollections;

    private Observer<DataResource<FireStoreViewModel.FireStoreEdit>> observerFireStoreEdit;

    private LiveData<DataResource<FireStoreViewModel.FireStoreEdit>> observableFireStoreEdit;

    private LiveData<DataResource<List<Collection>>> observableCollections;

    private Observer<DataResource<List<Collection>>> observerCollections;

    private String collectionNameEdit;

    private Collection collectionEdit;

    public MyViewModelFactory myViewModelFactory;


    public CollectionsFragment(MyViewModelFactory myViewModelFactory) {

        this.myViewModelFactory = myViewModelFactory;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this, myViewModelFactory).get(FireStoreViewModel.class);

        observableFireStoreEdit = viewModel.getFireStoreEditsObservable();

        observerFireStoreEdit = new Observer<DataResource<FireStoreViewModel.FireStoreEdit>>() {
            @Override
            public void onChanged(DataResource<FireStoreViewModel.FireStoreEdit> firestoreEditDataResource) {
                handleResponseFireStoreEdit(firestoreEditDataResource);
            }
        };

        observerCollections = new Observer<DataResource<List<Collection>>>() {
            @Override
            public void onChanged(DataResource<List<Collection>> listDataResource) {


                handleResponseCollections(listDataResource.getData(), listDataResource.getStatus());
            }
        };

        observableCollections = viewModel.getCollectionsObservable();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(view == null) {

            view = inflater.inflate(R.layout.fragment_collections, container, false);

            progressBar = view.findViewById(R.id.progress_bar);

            errorLayout = view.findViewById(R.id.error_message);

            rvCollections = view.findViewById(R.id.rv_collections);

            llNoSavedCollections = view.findViewById(R.id.ll_no_saved_collections);

            view.findViewById(R.id.btn_try_again).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    retryFetch();
                }
            });

            setInitialVisibilities();

            setUpRecyclerView();

            observableCollections.observeForever(observerCollections);

            observableFireStoreEdit.observeForever(observerFireStoreEdit);

        }

        MyIdlingResource.increment(); // for testing

        viewModel.fetchCollections();

        return view;

    }


    private void setInitialVisibilities() {

        progressBar.setVisibility(View.VISIBLE);

        errorLayout.setVisibility(GONE);

        rvCollections.setVisibility(View.GONE);

        llNoSavedCollections.setVisibility(View.GONE);

    }


    private void setUpRecyclerView() {


        // ProfileFragment and DialogFragmentAddToCollection uses this fragment and in ProfileFragment
        // there is option to edit collection
        if(getParentFragment() instanceof ProfileFragment) {

            rvAdapter = new CollectionsRecyclerViewAdapter((BaseRecyclerViewAdapter.OnClickListItemListener) getParentFragment(),
                    (BaseRecyclerViewAdapter.OnClickActionButtonListener)getParentFragment(), R.layout.recyclerview_item_collection_action_button);
        }else{
            rvAdapter = new CollectionsRecyclerViewAdapter((BaseRecyclerViewAdapter.OnClickListItemListener) getParentFragment(),
                    null, R.layout.recyclerview_item_collection);
        }


        rvAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);

        rvCollections.setAdapter(rvAdapter);

        rvCollections.setItemAnimator(null);

        rvCollections.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

    }



    public void changeCollectionName(Collection collection, String newName){

        collectionNameEdit = newName;

        collectionEdit = collection;

        viewModel.changeCollectionName(collection, newName);

    }


    public void deleteCollection(Collection collection){

        MyIdlingResource.increment();

        collectionEdit = collection;

        viewModel.deleteCollection(collection);
    }


    public void createNewCollection(String collectionName, Result result){

        collectionEdit = viewModel.createNewCollection(collectionName, result);

        viewModel.addNewCollection(collectionEdit, result);

    }


    private void handleResponseFireStoreEdit(DataResource<FireStoreViewModel.FireStoreEdit> firestoreEditDataResource) {


        switch(firestoreEditDataResource.getStatus()){

            case SUCCESS:

                switch(firestoreEditDataResource.getData()){

                    case DELETE_COLLECTION:

                        rvAdapter.remove(collectionEdit);

                        if(rvAdapter.getData().size() == 0){

                            rvCollections.setVisibility(GONE);
                            llNoSavedCollections.setVisibility(View.VISIBLE);
                        }

                        break;

                    case UPDATE_COLLECTION_NAME:

                        Collection newCollection = new Collection(collectionEdit.getName(),
                                collectionEdit.getCollectionCount(), collectionEdit.getOldestItemPosterPath(),
                                collectionEdit.getDocumentId(), collectionEdit.getTimeStamp());

                        newCollection.setName(collectionNameEdit);

                        rvAdapter.update(collectionEdit, newCollection);

                        break;

                    case ADD_EMPTY_COLLECTION:

                        rvAdapter.add(collectionEdit);

                        if(rvAdapter.getData().size() == 1) {
                            rvCollections.setVisibility(View.VISIBLE);
                            llNoSavedCollections.setVisibility(GONE);
                        }

                        break;
                }

                MyIdlingResource.decrement();

                break;

            case ERROR:

                String errorMessage = "";

                switch(firestoreEditDataResource.getData()){

                    case DELETE_COLLECTION:

                        errorMessage = getString(R.string.delete_collection_failed, collectionEdit.getName());

                        break;

                    case UPDATE_COLLECTION_NAME:

                            errorMessage = getString(R.string.update_collection_name_failed);

                        break;

                    case ADD_EMPTY_COLLECTION:

                        errorMessage = getString(R.string.create_empty_collection_failed, collectionEdit.getName());

                        break;


                }

                UiUtil.showSnackBarWithLabel(errorMessage,
                        requireView(), 3000);

                MyIdlingResource.decrement();
                break;
        }

    }


    private void handleResponseCollections(List<Collection> data, DataResource.Status status){

        progressBar.setVisibility(GONE);

        switch(status){

            case SUCCESS:

                if(!data.isEmpty()) {

                    rvAdapter.setData(data);

                    rvCollections.setVisibility(View.VISIBLE);

                    llNoSavedCollections.setVisibility(View.GONE);


                }else{

                    rvAdapter.clear();

                    rvCollections.setVisibility(GONE);

                    errorLayout.setVisibility(View.GONE);

                    llNoSavedCollections.setVisibility(View.VISIBLE);
                }

                break;

            case ERROR:

                UiUtil.displayErrorMessage(requireContext(), errorLayout);

                break;
        }

        MyIdlingResource.decrement(); // testing
    }


    private void retryFetch() {

        errorLayout.setVisibility(GONE);

        viewModel.fetchCollections();

    }


    @Override
    public void onDestroy() {

        observableFireStoreEdit.removeObserver(observerFireStoreEdit);

        observableCollections.removeObserver(observerCollections);

        super.onDestroy();
    }
}

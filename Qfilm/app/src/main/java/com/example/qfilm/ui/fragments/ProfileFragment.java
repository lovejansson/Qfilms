package com.example.qfilm.ui.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.Collection;
import com.example.qfilm.databinding.FragmentProfileBinding;
import com.example.qfilm.ui.adapters.BaseRecyclerViewAdapter;
import com.example.qfilm.ui.fragments.dialogFragments.DialogFragmentBottomMenu;
import com.example.qfilm.ui.fragments.dialogFragments.DialogFragmentEditCollectionName;
import com.example.qfilm.ui.fragments.dialogFragments.DialogFragmentNewCollection;
import com.example.qfilm.ui.fragments.dialogFragments.DialogFragmentConfirmation;
import com.example.qfilm.ui.utils.navigation.NavigationInterface;
import com.google.firebase.auth.FirebaseAuth;

/**
 * ProfileFragment has a CollectionsFragment as a child which it communicates with when user wants
 * to edit collections. CollectionsFragment is handling the actual communication with the FireStoreViewModel.
 *
 * **/

public class ProfileFragment extends Fragment implements
        DialogFragmentNewCollection.CreateNewCollectionInterface, DialogFragmentBottomMenu.EditCollectionBottomMenu,
        DialogFragmentEditCollectionName.EditCollectionNameInterface,
        DialogFragmentConfirmation.ConfirmActionInterface,
        BaseRecyclerViewAdapter.OnClickActionButtonListener, BaseRecyclerViewAdapter.OnClickListItemListener {


    private static final String TAG = "ProfileFragment";

    private FragmentProfileBinding binding;

    private NavigationInterface navigationInterface;

    private View view;

    private Collection clickedCollection;

    private CollectionsFragment collectionsFragment;

    private FirebaseAuth firebaseAuth;


    public ProfileFragment(FirebaseAuth firebaseAuth, CollectionsFragment collectionsFragment) {

        this.firebaseAuth = firebaseAuth;

        this.collectionsFragment = collectionsFragment;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        navigationInterface = (NavigationInterface) requireActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(view == null) {

            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

            view = binding.getRoot();
            
            displayCollectionsFragment();

            setOnClickListeners();

        }

        return view;

    }


    @Override
    public void onResume() {

        super.onResume();

        // check to see if user is still signed in (can change in SettingsActivity)
        if(firebaseAuth.getCurrentUser() == null){

            navigationInterface.navigateToUserFragment();

        }else{

            // setting username here to account for changes in SettingsActivity

            binding.setUsername(firebaseAuth.getCurrentUser().getDisplayName());

            binding.executePendingBindings();
        }

    }


    private void displayCollectionsFragment() {

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        
        fragmentTransaction.replace(R.id.fragment_container, collectionsFragment);
        
        fragmentTransaction.commit();
        
    }


    private void setOnClickListeners() {

        binding.btnSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                navigationInterface.navigateToSettingsActivity();
            }

        });
        
        binding.btnNewCollection.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                DialogFragmentNewCollection dialogFragmentNewCollection = new DialogFragmentNewCollection();

                dialogFragmentNewCollection.show(getChildFragmentManager(), null);

            }
        });

    }


    // implemented interface from DialogFragmentNewCollection
    @Override
    public void createNewCollection(String collectionName) {

        collectionsFragment.createNewCollection(collectionName, null);

    }


    // implemented interface from DialogFragmentBottomMenu
    @Override
    public void onMenuItemSelected(View view) {

        switch(view.getId()){

            case R.id.tv_btn_delete_collection:

                DialogFragmentConfirmation dialogFragmentSimple = DialogFragmentConfirmation.newInstance(getString(R.string.dialog_fragment_simple_delete_collection));

                dialogFragmentSimple.show(getChildFragmentManager(), null);

                break;

            case R.id.tv_btn_edit_collection_name:

                DialogFragmentEditCollectionName dialogFragmentEditCollectionName = new DialogFragmentEditCollectionName();

                dialogFragmentEditCollectionName.show(getChildFragmentManager(), null);

                break;
        }
    }



    // implemented interface from DialogFragmentEditCollectionName
    @Override
    public void editCollectionName(String collectionName) {

        collectionsFragment.changeCollectionName(clickedCollection, collectionName);

    }

    /*
    * implemented click listeners for collection item (leads to collection fragment) +
    * action button (opens bottom menu for editing collection).
    *
    * */

    @Override
    public void onListItemClick(Object item) {

        navigationInterface.navigateToCollectionFragment((Collection) item);
    }


    @Override
    public void onActionButtonClick(Object item) {

        clickedCollection = (Collection) item;

        DialogFragmentBottomMenu dialogFragmentBottomMenu = new DialogFragmentBottomMenu();

        dialogFragmentBottomMenu.show(getChildFragmentManager(), null);

    }

    // implemented interface for DialogFragmentConfirmation (confirms that action should be taken, for now
    // only that a collection should be deleted.

    @Override
    public void doAction() {

        collectionsFragment.deleteCollection(clickedCollection);

    }


}

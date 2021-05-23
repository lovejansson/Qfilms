package com.example.qfilm.ui.fragments.dialogFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.Collection;
import com.example.qfilm.ui.adapters.BaseRecyclerViewAdapter;
import com.example.qfilm.ui.fragments.CollectionsFragment;
import com.example.qfilm.ui.utils.UiUtil;


public class DialogFragmentAddToCollection extends DialogFragment  implements BaseRecyclerViewAdapter.OnClickListItemListener,
        DialogFragmentNewCollection.CreateNewCollectionInterface{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_fragment_add_to_collection, container, false);

        setupOnclickListeners(view);

        displayCollectionsFragment();

        return view;

    }


    private void displayCollectionsFragment() {

        CollectionsFragment collectionsFragment = (CollectionsFragment) requireActivity().getSupportFragmentManager()
                .getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), CollectionsFragment.class.getSimpleName());

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, collectionsFragment);

        fragmentTransaction.commit();

    }


    @Override
    public void onStart()
    {
        super.onStart();

        Dialog dialog = getDialog();

        if (dialog != null)
        {

            // dialog has different size depending on tablet mode

            if(getResources().getBoolean(R.bool.is_tablet)){

                UiUtil.setDialogFragmentDimensions(dialog);

            }else {

                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }

            dialog.getWindow().setWindowAnimations(R.style.Window_DialogAnimation);
        }

    }


    private void setupOnclickListeners(View view) {

        view.findViewById(R.id.btn_new_collection).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                DialogFragmentNewCollection dialogFragmentNewCollection = new DialogFragmentNewCollection();

                dialogFragmentNewCollection.show(getChildFragmentManager(), null);

            }
        });


        view.findViewById(R.id.btn_close_dialog).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

               dismiss();

            }
        });

    }


    @Override
    public void createNewCollection(String collectionName) {

        ((DialogFragmentAddToCollection.AddToCollectionInterface)getParentFragment()).AddToNewCollection(collectionName);

        dismiss();
    }


    @Override
    public void onListItemClick(Object item) {

        ((DialogFragmentAddToCollection.AddToCollectionInterface)getParentFragment()).AddToCollection(((Collection) item));

        dismiss();
    }


    public interface AddToCollectionInterface {

        void AddToCollection(Collection collection);

        void AddToNewCollection(String collectionName);
    }

}

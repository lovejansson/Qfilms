package com.example.qfilm.ui.fragments.dialogFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qfilm.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DialogFragmentBottomMenu extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String TAG = "DialogFragmentBottomMen";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       View view =  inflater.inflate(R.layout.dialog_fragment_bottom_menu, container, false);

       view.findViewById(R.id.tv_btn_edit_collection_name).setOnClickListener(this);

       view.findViewById(R.id.tv_btn_delete_collection).setOnClickListener(this);

       return view;

    }


    @Override
    public void onClick(View view) {

        ((EditCollectionBottomMenu) getParentFragment()).onMenuItemSelected(view);

        dismiss();
    }


    public interface EditCollectionBottomMenu{

        void onMenuItemSelected(View view);
    }
}

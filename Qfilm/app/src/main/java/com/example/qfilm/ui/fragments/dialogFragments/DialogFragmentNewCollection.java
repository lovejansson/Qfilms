package com.example.qfilm.ui.fragments.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.qfilm.R;
import com.google.android.material.textfield.TextInputEditText;

public class DialogFragmentNewCollection extends DialogFragment {

    private TextInputEditText etCollectionName;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_fragment_edit_collection, null);

        etCollectionName = view.findViewById(R.id.et_collection_name);

        ((TextView) view.findViewById(R.id.tv_heading_edit_collection_dialog)).setText(R.string.dialog_fragment_new_collection);

        setupOnClickListeners(view);

        builder.setView(view);

        return builder.create();

    }


    private void setupOnClickListeners(View view){

        view.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                ((DialogFragmentNewCollection.CreateNewCollectionInterface) getParentFragment())
                        .createNewCollection(etCollectionName.getText().toString());
                dismiss();
            }
        });


        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    public interface CreateNewCollectionInterface{

       void createNewCollection(String collectionName);

    }

}

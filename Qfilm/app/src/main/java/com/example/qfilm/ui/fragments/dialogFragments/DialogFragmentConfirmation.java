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


// user has to confirm that actions should be made

public class DialogFragmentConfirmation extends DialogFragment {

    private static final String ARG_MESSAGE = "message";

    private String message;

    public static DialogFragmentConfirmation newInstance(String message) {

        Bundle bundle = new Bundle();

        DialogFragmentConfirmation fragment = new DialogFragmentConfirmation();

        bundle.putString(ARG_MESSAGE, message);

        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){

            message = getArguments().getString(ARG_MESSAGE);
        }

    }


    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_fragment_simple, null);

        ((TextView)view.findViewById(R.id.tv_message)).setText(message);

        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                ((DialogFragmentConfirmation.ConfirmActionInterface) getParentFragment())
                        .doAction();

                dismiss();
            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        builder.setView(view);

        return builder.create();

    }


    public interface ConfirmActionInterface{

        void doAction();

    }
}

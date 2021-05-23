package com.example.qfilm.ui.fragments.dialogFragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.data.models.entities.MediaType;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.ui.adapters.BaseRecyclerViewAdapter;
import com.example.qfilm.ui.adapters.GenreRecyclerViewAdapter;
import com.example.qfilm.ui.utils.UiUtil;
import com.example.qfilm.viewmodels.utils.MyViewModelFactory;
import com.example.qfilm.viewmodels.ResultWithGenreViewModel;
import java.util.List;

import utils.MyIdlingResource;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;


public class DialogFragmentGenres extends DialogFragment implements BaseRecyclerViewAdapter.OnClickListItemListener {

    private static final String TAG = "DialogFragmentGenres";

    private MediaType mediaType; // movie or series

    private Genre currentGenre;

    private ResultWithGenreViewModel viewModel;

    private ImageButton btnCloseDialog;

    private RecyclerView rvGenres;

    private ProgressBar progressBar;

    private LinearLayout errorLayout;

    private MyViewModelFactory myViewModelFactory;

    private String currentLanguage;

    private GenreRecyclerViewAdapter rvAdapter;


    public DialogFragmentGenres(Genre genre, MediaType mediaType, MyViewModelFactory myViewModelFactory){

        this.currentGenre = genre;
        this.mediaType = mediaType;
        this.myViewModelFactory = myViewModelFactory;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String language = requireActivity().getSharedPreferences("com.example.qfilm", MODE_PRIVATE).getString("language", "English");

        currentLanguage = language.substring(0, 2).toLowerCase();

        viewModel = new ViewModelProvider(this, myViewModelFactory).get(ResultWithGenreViewModel.class);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_fragment_genres, container, false);

        btnCloseDialog = view.findViewById(R.id.btn_close_dialog);

        rvGenres = view.findViewById(R.id.rv_genres);

        errorLayout = view.findViewById(R.id.error_message);

        progressBar = view.findViewById(R.id.progress_bar);

        setInitialVisibilities();

        setupOnClickListeners();

        setupRecyclerView();

        MyIdlingResource.increment();

        observeGenres();

        viewModel.fetchGenres(mediaType, currentLanguage);


        return view;

    }


    @Override
    public void onStart()
    {
        super.onStart();

        Dialog dialog = getDialog();

        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;

            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog.getWindow().setWindowAnimations(R.style.Window_DialogAnimation);

            dialog.getWindow().setLayout(width, height);
        }
    }


    private void setupRecyclerView() {

        rvAdapter = new GenreRecyclerViewAdapter(this, null,
                R.layout.recyclerview_item_genre, currentGenre);

        rvGenres.setAdapter(rvAdapter);

        rvGenres.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        rvGenres.setItemAnimator(null);

    }


    private void setInitialVisibilities() {

        progressBar.setVisibility(GONE);

        rvGenres.setVisibility(GONE);

        errorLayout.setVisibility(GONE);
    }


    private void setupOnClickListeners() {

        btnCloseDialog.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                MyIdlingResource.increment();

                ((SelectGenreInterface) getParentFragment()).selectGenre(null);

                dismiss();
            }
        });


        errorLayout.findViewById(R.id.btn_try_again).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                retryFetch();
            }
        });

    }


    private void observeGenres() {

      viewModel.getGenresObservable().observe(getViewLifecycleOwner(), new Observer<DataResource<List<Genre>>>() {
          @Override
          public void onChanged(DataResource<List<Genre>> genres) {

              if (genres != null) {
                  handleResponseGenres(genres.getStatus(), genres.getData(), genres.getMessage());
              }
          }
      });

    }


    private void handleResponseGenres(DataResource.Status status, List<Genre> data, String message){

        switch(status){

            case SUCCESS:

                if(data != null && !data.isEmpty()) {

                    data.add(0, new Genre(-1, getString(R.string.all_genres)));

                    setSelectedGenre(data);

                    rvGenres.setVisibility(View.VISIBLE);

                    rvAdapter.setData(data);

                }

                progressBar.setVisibility(GONE);

                break;

            case LOADING:

                progressBar.setVisibility(View.VISIBLE);

                break;


            case ERROR:

                progressBar.setVisibility(GONE);

                if (data == null) {

                    UiUtil.displayErrorMessage(requireContext(), errorLayout);

                }else{
                    
                    data.add(0, new Genre(-1, getString(R.string.all_genres)));

                    setSelectedGenre(data);

                    rvAdapter.setData(data);

                    rvGenres.setVisibility(View.VISIBLE);

                }

                break;
        }

        MyIdlingResource.decrement();

    }


    // shown in white
    private void setSelectedGenre(List<Genre> data) {

        int idx = data.indexOf(currentGenre);

        if(idx != -1) {

            data.get(idx).setSelected(true);
        }
    }


    private void retryFetch() {

        MyIdlingResource.increment();

        errorLayout.setVisibility(GONE);

        viewModel.fetchGenres(mediaType, currentLanguage);

    }


    @Override
    public void onListItemClick(Object item) {

        Genre newGenre = (Genre) item;

        MyIdlingResource.increment();

        ((SelectGenreInterface) getParentFragment()).selectGenre(newGenre);

        dismiss();

    }


    public interface SelectGenreInterface {
        void selectGenre(@Nullable Genre genre);
    }
}

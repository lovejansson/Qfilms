package com.example.qfilm.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qfilm.R;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.ui.adapters.BaseRecyclerViewAdapter;
import com.example.qfilm.ui.adapters.ResultRecyclerViewAdapter;
import com.example.qfilm.ui.utils.ColumnSpacingDecoration;
import com.example.qfilm.ui.utils.UiUtil;
import com.example.qfilm.ui.utils.navigation.NavigationInterface;
import com.example.qfilm.viewmodels.AlgoliaSearchViewModel;
import com.example.qfilm.viewmodels.utils.MyViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import utils.MyIdlingResource;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;


/**
 *
 * uses AlgoliaViewModel to search for movies and series
 *
 * **/

public class SearchFragment extends Fragment implements BaseRecyclerViewAdapter.OnClickListItemListener{

    private static final String TAG = "SearchFragment";

    private MyViewModelFactory myViewModelFactory;

    private AlgoliaSearchViewModel algoliaSearchViewModel;

    private TextInputEditText etSearch;

    private String currentLanguage;

    private RecyclerView rvResults;

    private ResultRecyclerViewAdapter rvAdapter;

    public NavigationInterface navigationInterface;

    private LinearLayout errorLayout;

    private Boolean tabletMode;

    private View view;

    private String latestSearchText;

    private Observer<DataResource<List<Result>>> observerSearchResult;


    public SearchFragment(MyViewModelFactory myViewModelFactory) {

        this.myViewModelFactory = myViewModelFactory;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        navigationInterface = (NavigationInterface) requireActivity();

        tabletMode = requireActivity().getResources().getBoolean(R.bool.is_tablet);

        algoliaSearchViewModel = new ViewModelProvider(this, myViewModelFactory).get(AlgoliaSearchViewModel.class);

        observerSearchResult = new Observer<DataResource<List<Result>>>() {
            @Override
            public void onChanged(DataResource<List<Result>> listDataResource) {

                handleResponse(listDataResource);

            }
        };

        String language = requireActivity().getSharedPreferences("com.example.qfilm",
                MODE_PRIVATE).getString("language", "English");

        currentLanguage = language.substring(0, 2).toLowerCase();


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // saving view to save view state
        if(view == null) {

            view = inflater.inflate(R.layout.fragment_search, container, false);

            errorLayout = view.findViewById(R.id.error_message);

            setupRecyclerView();

            setInitialVisibilities();

            setupOnClickListeners();

            setupTextWatcher();

            algoliaSearchViewModel.getQueryResult().observeForever(observerSearchResult);

        }

        return view;
    }

    private void setupOnClickListeners() {

        // when user clicks outside of edit text the keyboard should hide
        view.findViewById(R.id.fragment_search).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (!(view instanceof EditText)) {

                    etSearch.clearFocus();

                    UiUtil.hideKeyboard(view);

                }
            }
        });


        view.findViewById(R.id.btn_try_again).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                retryFetch(latestSearchText);

            }
        });

    }


    private void setInitialVisibilities(){

        errorLayout.setVisibility(GONE);
        rvResults.setVisibility(GONE);
    }


    private void setupTextWatcher() {

        etSearch = view.findViewById(R.id.et_search);

        etSearch.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                latestSearchText = s.toString();

                if(!s.toString().isEmpty()){

                    MyIdlingResource.increment(); // testing

                }

                algoliaSearchViewModel.search(s.toString(), currentLanguage);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );
    }


    private void setupRecyclerView() {

        // the user can choose to show titles in the original language or english

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.example.qfilm",
                MODE_PRIVATE);

        Boolean showOriginalTitles = sharedPreferences.getBoolean("titles", false);

        rvAdapter = new ResultRecyclerViewAdapter(this, null,
                R.layout.recyclerview_item_result_search, showOriginalTitles);

        rvResults = view.findViewById(R.id.rv_results);

        rvResults.setAdapter(rvAdapter);

        // setting up columns

        int numberOfColumns = tabletMode ? 4 : 3;

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), numberOfColumns);

        rvResults.setLayoutManager(layoutManager);

        int space = (int) ((getResources().getDimensionPixelSize(R.dimen.margin_outer_small)) /
                (getResources().getDisplayMetrics().density));

        rvResults.addItemDecoration(new ColumnSpacingDecoration(space, numberOfColumns));

        // progressbar takes up all columns

        int finalNumberOfColumns = numberOfColumns;
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                int layout = rvAdapter.getItemViewType(position);

                if (layout == R.layout.recyclerview_item_progress_bar) {
                    return finalNumberOfColumns;
                } else {
                    return 1;
                }
            }
        });

    }


    private void retryFetch(String latestSearchText) {

        MyIdlingResource.increment(); // testing

        algoliaSearchViewModel.search(latestSearchText, currentLanguage);

    }


    private void handleResponse(DataResource<List<Result>> listDataResource) {

        switch(listDataResource.getStatus()){

            case SUCCESS:

                rvAdapter.setData(listDataResource.getData());

                rvResults.setVisibility(View.VISIBLE);

                errorLayout.setVisibility(GONE);

                break;

            case ERROR:

                rvResults.setVisibility(View.GONE);

                UiUtil.displayErrorMessage(requireContext(), errorLayout);

                break;
        }

        MyIdlingResource.decrement(); // testing

    }


    @Override
    public void onListItemClick(Object item) {

        navigationInterface.navigateToDetailPageFragment((Result) item);

    }


    @Override
    public void onDestroy() {

        algoliaSearchViewModel.getQueryResult().removeObserver(observerSearchResult);

        super.onDestroy();

    }
}

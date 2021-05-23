package utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.qfilm.viewmodels.AlgoliaSearchViewModel;
import com.example.qfilm.viewmodels.DetailsViewModel;
import com.example.qfilm.viewmodels.FireStoreViewModel;
import com.example.qfilm.viewmodels.FirebaseAuthViewModel;
import com.example.qfilm.viewmodels.ResultWithGenreViewModel;
import com.example.qfilm.viewmodels.utils.MyViewModelFactory;

import utils.Mocks;

public class MyTestViewModelFactory extends MyViewModelFactory {


    public MyTestViewModelFactory(){

    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if(modelClass.isAssignableFrom(FireStoreViewModel.class)){

            return (T) Mocks.fireStoreViewModelMock;

        }else if(modelClass.isAssignableFrom(DetailsViewModel.class)){

            return (T) Mocks.detailsViewModelMock;

        }else if(modelClass.isAssignableFrom(ResultWithGenreViewModel.class)){

            return (T) Mocks.resultWithGenreViewModelMock;

        }else if(modelClass.isAssignableFrom(FirebaseAuthViewModel.class)){

            return (T) Mocks.firebaseAuthViewModelMock;

        }else if(modelClass.isAssignableFrom(AlgoliaSearchViewModel.class)){

            return (T)Mocks.algoliaSearchViewModelMock;
        }

        throw new IllegalArgumentException("ViewModel not found");

    }
}

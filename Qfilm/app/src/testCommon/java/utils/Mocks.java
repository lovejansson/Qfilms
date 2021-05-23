package utils;

import com.algolia.search.saas.Client;
import com.example.qfilm.repositories.MoviesRepository;
import com.example.qfilm.viewmodels.AlgoliaSearchViewModel;
import com.example.qfilm.viewmodels.DetailsViewModel;
import com.example.qfilm.viewmodels.FireStoreViewModel;
import com.example.qfilm.viewmodels.FirebaseAuthViewModel;
import com.example.qfilm.viewmodels.ResultWithGenreViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import org.mockito.Mockito;

public class Mocks {

    public static FirebaseAuth firebaseAuthMock = Mockito.mock(FirebaseAuth.class);

    public static MoviesRepository moviesRepositoryMock = Mockito.mock(MoviesRepository.class);

    public static FirebaseUser firebaseUserMock = Mockito.mock(FirebaseUser.class);

    public static Task taskMock = Mockito.mock(Task.class);

    public static FirebaseFirestore firestoreMock = Mockito.mock(FirebaseFirestore.class);

    public static FireStoreViewModel fireStoreViewModelMock = Mockito.mock(FireStoreViewModel.class);

    public static ResultWithGenreViewModel resultWithGenreViewModelMock = Mockito.mock(ResultWithGenreViewModel.class);

    public static DetailsViewModel detailsViewModelMock = Mockito.mock(DetailsViewModel.class);

    public static FirebaseAuthViewModel firebaseAuthViewModelMock = Mockito.mock(FirebaseAuthViewModel.class);

    public static AlgoliaSearchViewModel algoliaSearchViewModelMock = Mockito.mock(AlgoliaSearchViewModel.class);

    public static FirebaseFunctions firebaseFunctionsMock = Mockito.mock(FirebaseFunctions.class);

    public static Client client = Mockito.mock(Client.class);

    public static void reset(){

        firebaseAuthMock = Mockito.mock(FirebaseAuth.class);
        moviesRepositoryMock = Mockito.mock(MoviesRepository.class);
        firebaseUserMock = Mockito.mock(FirebaseUser.class);
        taskMock = Mockito.mock(Task.class);
        firestoreMock = Mockito.mock(FirebaseFirestore.class);
        detailsViewModelMock = Mockito.mock(DetailsViewModel.class);
        resultWithGenreViewModelMock = Mockito.mock(ResultWithGenreViewModel.class);
        fireStoreViewModelMock = Mockito.mock(FireStoreViewModel.class);
        firebaseAuthViewModelMock = Mockito.mock(FirebaseAuthViewModel.class);
        algoliaSearchViewModelMock = Mockito.mock(AlgoliaSearchViewModel.class);
        firebaseFunctionsMock = Mockito.mock(FirebaseFunctions.class);
        client = Mockito.mock(Client.class);
    }

}

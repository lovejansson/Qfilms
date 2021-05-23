package com.example.qfilm.viewmodels;

import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.qfilm.data.models.entities.Collection;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.viewmodels.FireStoreViewModel.FireStoreEdit;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.HttpsCallableReference;
import com.google.firebase.functions.HttpsCallableResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.Mocks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static utils.Mocks.firebaseAuthMock;
import static utils.Mocks.firebaseUserMock;
import static utils.Mocks.firestoreMock;
import static utils.Mocks.taskMock;
import static utils.TestDummyData.COLLECTION;
import static utils.TestDummyData.RESULT_ONE;


public class FireStoreViewModelTest {

    private static final String TAG = "FireStoreViewModelTest";

    // tested
    FireStoreViewModel fireStoreViewModel;

    // mocked collection/document/query
    CollectionReference usersMock;
    DocumentReference userMock;
    CollectionReference collectionsMock;
    DocumentReference collectionMock;
    CollectionReference itemsMock;
    Query queryMock;
    QuerySnapshot querySnapshotMock;
    DocumentSnapshot documentSnapshotMock;
    List<DocumentSnapshot> queryDocumentsMock;


    /**
     A JUnit Test Rule that swaps the background executor used by the Architecture Components
     with a different one which executes each task synchronously. Used because you can't invoke
     observeForever on a background thread **/


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule =
            new InstantTaskExecutorRule();


    @Before
    public void setUp(){

        when(firebaseAuthMock.getCurrentUser()).thenReturn(firebaseUserMock);

        fireStoreViewModel = new FireStoreViewModel(Mocks.firebaseAuthMock, Mocks.firestoreMock, Mocks.firebaseFunctionsMock);

        usersMock = Mockito.mock(CollectionReference.class);
        userMock = Mockito.mock(DocumentReference.class);
        collectionsMock = Mockito.mock(CollectionReference.class);
        collectionMock = Mockito.mock(DocumentReference.class);
        itemsMock = Mockito.mock(CollectionReference.class);
        queryMock = Mockito.mock(Query.class);
        querySnapshotMock = Mockito.mock(QuerySnapshot.class);
        documentSnapshotMock = Mockito.mock(QueryDocumentSnapshot.class);
        queryDocumentsMock = Mockito.mock(List.class);

        when(Mocks.firestoreMock.collection("users")).thenReturn(usersMock);

        when(Mocks.firebaseUserMock.getUid()).thenReturn("4");

        when(usersMock.document("4")).thenReturn(userMock);

        when(userMock.collection("collections")).thenReturn(collectionsMock);

        when(collectionsMock.orderBy("timeStamp", Query.Direction.ASCENDING)).thenReturn(queryMock);

        when(collectionsMock.document(COLLECTION.getDocumentId())).thenReturn(collectionMock);

        when(collectionMock.collection("items")).thenReturn(itemsMock);

        when(itemsMock.orderBy("timeStamp", Query.Direction.DESCENDING)).thenReturn(queryMock);

        when(queryMock.limit(20)).thenReturn(queryMock);

        when(queryMock.get()).thenReturn(Mocks.taskMock);

        when(taskMock.addOnCompleteListener(any(OnCompleteListener.class))).thenAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) {
                Log.d(TAG, "answer: returning task");
                ((OnCompleteListener) invocation.getArgument(0)).onComplete(taskMock);

                return null;
            }
        });

    }


    @Test
    public void testFetchCollectionItems_successResponse(){

        //setup

        // answer mocks

        HttpsCallableReference httpsCallableReferenceMock = Mockito.mock(HttpsCallableReference.class);
        HttpsCallableResult httpsCallableResultMock = Mockito.mock(HttpsCallableResult.class);

        when(Mocks.firebaseFunctionsMock.getHttpsCallable("fetchCollectionItems"))
                .thenReturn(httpsCallableReferenceMock);

        when(httpsCallableReferenceMock.call(any(Map.class))).thenReturn(taskMock);

        when(taskMock.continueWith(any(Continuation.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                ((Continuation) invocation.getArgument(0)).then(taskMock);

                return null;
            }
        });


        when(taskMock.getResult()).thenReturn(httpsCallableResultMock);

        // answer data from callable function

        List<Map<String, Object>> res = new ArrayList<>();

        Map<String, Object> result = new HashMap<>();

        Map<String, Object> lastTimeStamp = new HashMap<>();

        Map<String, Object> lastPage = new HashMap<>();

        result.put("resultId", RESULT_ONE.getResultId());
        result.put("title", RESULT_ONE.getTitle());
        result.put("originalTitle", RESULT_ONE.getOriginalTitle());
        result.put("popularity", RESULT_ONE.getPopularity());
        result.put("posterPath", RESULT_ONE.getPosterPath());
        result.put("overview", RESULT_ONE.getOverview());
        result.put("mediaType", RESULT_ONE.getMediaType());

        lastPage.put("lastPage", false);

        lastTimeStamp.put("lastTimeStamp", (long)4000);

        res.add(result);
        res.add(lastPage);
        res.add(lastTimeStamp);

        when(httpsCallableResultMock.getData()).thenReturn(res);

        // act

        LiveData<DataResource<List<Result>>> collectionItemsObservable = fireStoreViewModel.getCollectionItemsObservable();

        Observer<DataResource<List<Result>>> observer = Mockito.mock(Observer.class);

        collectionItemsObservable.observeForever(observer);

        fireStoreViewModel.fetchCollectionItems("4", "en");

        // verify

        verify(observer).onChanged(DataResource.success(Collections.singletonList(RESULT_ONE)));

    }


    @Test
    public void testFetchCollectionItems_successResponse_empty(){

        //setup

        // answer mocks

        HttpsCallableReference httpsCallableReferenceMock = Mockito.mock(HttpsCallableReference.class);
        HttpsCallableResult httpsCallableResultMock = Mockito.mock(HttpsCallableResult.class);

        when(Mocks.firebaseFunctionsMock.getHttpsCallable("fetchCollectionItems"))
                .thenReturn(httpsCallableReferenceMock);

        when(httpsCallableReferenceMock.call(any(Map.class))).thenReturn(taskMock);

        when(taskMock.continueWith(any(Continuation.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                ((Continuation) invocation.getArgument(0)).then(taskMock);

                return null;
            }
        });


        when(taskMock.getResult()).thenReturn(httpsCallableResultMock);

        // this is answer data from callable function

        List<Map<String, Object>> res = new ArrayList<>();

        when(httpsCallableResultMock.getData()).thenReturn(res);

        // act

        LiveData<DataResource<List<Result>>> collectionItemsObservable = fireStoreViewModel.getCollectionItemsObservable();

        Observer<DataResource<List<Result>>> observer = Mockito.mock(Observer.class);

        collectionItemsObservable.observeForever(observer);

        fireStoreViewModel.fetchCollectionItems("4", "en");

        // verify

        verify(observer).onChanged(DataResource.success(new ArrayList<>()));

    }

    @Test
    public void testFetchCollectionItems_exception(){

        //setup

        // answer mocks

        HttpsCallableReference httpsCallableReferenceMock = Mockito.mock(HttpsCallableReference.class);
        HttpsCallableResult httpsCallableResultMock = Mockito.mock(HttpsCallableResult.class);

        when(Mocks.firebaseFunctionsMock.getHttpsCallable("fetchCollectionItems"))
                .thenReturn(httpsCallableReferenceMock);

        when(httpsCallableReferenceMock.call(any(Map.class))).thenReturn(taskMock);

        when(taskMock.continueWith(any(Continuation.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                ((Continuation) invocation.getArgument(0)).then(taskMock);

                return null;
            }
        });


        when(taskMock.getResult()).thenReturn(httpsCallableResultMock);


        when(httpsCallableResultMock.getData()).thenReturn(new Exception());

        // act

        LiveData<DataResource<List<Result>>> collectionItemsObservable = fireStoreViewModel.getCollectionItemsObservable();

        Observer<DataResource<List<Result>>> observer = Mockito.mock(Observer.class);

        collectionItemsObservable.observeForever(observer);

        fireStoreViewModel.fetchCollectionItems("4", "en");

        // verify

        verify(observer).onChanged(DataResource.error(null, null));

    }


    @Test
    public void testFetchCollections_successResponse(){

        // setup
        when(taskMock.isSuccessful()).thenReturn(true);
        when(taskMock.getResult()).thenReturn(querySnapshotMock);
        when(querySnapshotMock.toObjects(Collection.class)).thenReturn(Collections.singletonList(COLLECTION));

        // act

        LiveData<DataResource<List<Collection>>> collectionsObservable = fireStoreViewModel.getCollectionsObservable();

        Observer<DataResource<List<Collection>>> observer = Mockito.mock(Observer.class);

        collectionsObservable.observeForever(observer);

        fireStoreViewModel.fetchCollections();

        // verify

        observer.onChanged(DataResource.success(Collections.singletonList(COLLECTION)));

    }


    @Test
    public void testFetchCollections_error(){

        // setup
        when(taskMock.isSuccessful()).thenReturn(false);
        when(taskMock.getException()).thenReturn(Mockito.mock(Exception.class));


        // act

        LiveData<DataResource<List<Collection>>> collectionsObservable = fireStoreViewModel.getCollectionsObservable();

        Observer<DataResource<List<Collection>>> observer = Mockito.mock(Observer.class);

        collectionsObservable.observeForever(observer);

        fireStoreViewModel.fetchCollections();

        // verify

        verify(observer).onChanged(DataResource.error(null, "Unknown error"));

    }


    @Test
    public void testSaveToCollection_alreadyExists(){

        // setup

        DocumentReference resultDocumentReference = Mockito.mock(DocumentReference.class);

        when(itemsMock.document(RESULT_ONE.getResultId().toString())).thenReturn(resultDocumentReference);

        when(resultDocumentReference.get()).thenReturn(taskMock);

        when(taskMock.isSuccessful()).thenReturn(true);

        when(taskMock.getResult()).thenReturn(documentSnapshotMock);

        when(documentSnapshotMock.exists()).thenReturn(true);

        // act

        LiveData<DataResource<FireStoreEdit>> fireStoreEditsObservable = fireStoreViewModel.getFireStoreEditsObservable();

        Observer<DataResource<FireStoreEdit>> observer = Mockito.mock(Observer.class);

        fireStoreEditsObservable.observeForever(observer);

        fireStoreViewModel.saveToCollection(COLLECTION, RESULT_ONE);


        // verify

        verify(observer).onChanged(DataResource.error(FireStoreEdit.ADD_ITEM, null));


    }


    @Test
    public void testSaveToCollection_success(){

        // setup

        DocumentReference documentReferenceMock = Mockito.mock(DocumentReference.class);

        when(itemsMock.document(RESULT_ONE.getResultId().toString())).thenReturn(documentReferenceMock);

        when(documentReferenceMock.get()).thenReturn(taskMock);

        when(taskMock.isSuccessful()).thenReturn(true);

        when(taskMock.getResult()).thenReturn(documentSnapshotMock);

        when(documentSnapshotMock.exists()).thenReturn(false);

        // code will now create document references to store in firestore and HashMap

        when(Mocks.firestoreMock.collection("movies_en")).thenReturn(itemsMock);

        when(firestoreMock.collection("movies_es")).thenReturn(itemsMock);

        when(documentReferenceMock.set(any(Map.class))).thenReturn(taskMock);

        // act

        LiveData<DataResource<FireStoreEdit>> fireStoreEditsObservable = fireStoreViewModel.getFireStoreEditsObservable();

        Observer<DataResource<FireStoreEdit>> observer = Mockito.mock(Observer.class);

        fireStoreEditsObservable.observeForever(observer);

        fireStoreViewModel.saveToCollection(COLLECTION, RESULT_ONE);

        // verify

        verify(observer).onChanged(DataResource.success(FireStoreEdit.ADD_ITEM));


    }


    @Test
    public void testSaveToCollection_errorWhenSetResult(){

        // setup

        DocumentReference documentReferenceMock = Mockito.mock(DocumentReference.class);

        when(itemsMock.document(RESULT_ONE.getResultId().toString())).thenReturn(documentReferenceMock );

        when(documentReferenceMock .get()).thenReturn(taskMock);

        when(taskMock.isSuccessful()).thenReturn(true, false);

        when(taskMock.getResult()).thenReturn(documentSnapshotMock);

        when(documentSnapshotMock.exists()).thenReturn(false);

        // code will now create document references to store in firestore and HashMap

        when(Mocks.firestoreMock.collection("movies_en")).thenReturn(itemsMock);

        when(firestoreMock.collection("movies_es")).thenReturn(itemsMock);

        when(documentReferenceMock.set(any(Map.class))).thenReturn(taskMock);


        // act

        LiveData<DataResource<FireStoreEdit>> fireStoreEditsObservable = fireStoreViewModel.getFireStoreEditsObservable();

        Observer<DataResource<FireStoreEdit>> observer = Mockito.mock(Observer.class);

        fireStoreEditsObservable.observeForever(observer);

        fireStoreViewModel.saveToCollection(COLLECTION, RESULT_ONE);

        // verify

        verify(observer).onChanged(DataResource.error(FireStoreEdit.ADD_ITEM, null));


    }


    @Test
    public void testSaveToCollection_errorWhenGetCollection(){

        // setup

        DocumentReference resultDocumentReference = Mockito.mock(DocumentReference.class);

        when(itemsMock.document(RESULT_ONE.getResultId().toString())).thenReturn(resultDocumentReference);

        when(resultDocumentReference.get()).thenReturn(taskMock);

        when(taskMock.isSuccessful()).thenReturn(false);

        // act

        LiveData<DataResource<FireStoreEdit>> fireStoreEditsObservable = fireStoreViewModel.getFireStoreEditsObservable();

        Observer<DataResource<FireStoreEdit>> observer = Mockito.mock(Observer.class);

        fireStoreEditsObservable.observeForever(observer);

        fireStoreViewModel.saveToCollection(COLLECTION, RESULT_ONE);


        // verify

        verify(observer).onChanged(DataResource.error(FireStoreEdit.ADD_ITEM, null));


    }


    @Test
    public void testDeleteFromCollection_success(){

        // setup

        DocumentReference resultDocumentMock = Mockito.mock(DocumentReference.class);

        when(itemsMock.document(RESULT_ONE.getResultId().toString())).thenReturn(resultDocumentMock);

        when(resultDocumentMock.delete()).thenReturn(taskMock);

        when(taskMock.isSuccessful()).thenReturn(true);

        // act

        LiveData<DataResource<FireStoreEdit>> fireStoreEditsObservable = fireStoreViewModel.getFireStoreEditsObservable();

        Observer<DataResource<FireStoreEdit>> observer = Mockito.mock(Observer.class);

        fireStoreEditsObservable.observeForever(observer);

        fireStoreViewModel.deleteFromCollection(COLLECTION, RESULT_ONE);
        // verify
        verify(observer).onChanged(DataResource.success(FireStoreEdit.DELETE_ITEM));
    }


    @Test
    public void testDeleteFromCollection_error(){

        // setup

        DocumentReference resultDocumentMock = Mockito.mock(DocumentReference.class);

        when(itemsMock.document(RESULT_ONE.getResultId().toString())).thenReturn(resultDocumentMock);

        when(resultDocumentMock.delete()).thenReturn(taskMock);

        when(taskMock.isSuccessful()).thenReturn(false);

        // act
        LiveData<DataResource<FireStoreEdit>> fireStoreEditsObservable = fireStoreViewModel.getFireStoreEditsObservable();

        Observer<DataResource<FireStoreEdit>> observer = Mockito.mock(Observer.class);

        fireStoreEditsObservable.observeForever(observer);

        fireStoreViewModel.deleteFromCollection(COLLECTION, RESULT_ONE);

        // verify
        verify(observer).onChanged(DataResource.error(FireStoreEdit.DELETE_ITEM, null));


    }


    @Test
    public void testChangeCollectionName_success(){

        // setup

        when(collectionMock.update("name", "new name")).thenReturn(taskMock);

        when(taskMock.isSuccessful()).thenReturn(true);

        // act

        LiveData<DataResource<FireStoreEdit>> fireStoreEditsObservable = fireStoreViewModel.getFireStoreEditsObservable();

        Observer<DataResource<FireStoreEdit>> observer = Mockito.mock(Observer.class);

        fireStoreEditsObservable.observeForever(observer);

        fireStoreViewModel.changeCollectionName(COLLECTION, "new name");

        // verify

        verify(observer).onChanged(DataResource.success(FireStoreEdit.UPDATE_COLLECTION_NAME));
    }


    @Test
    public void testChangeCollectionName_error(){

        // setup

        when(collectionMock.update("name", "new name")).thenReturn(taskMock);

        when(taskMock.isSuccessful()).thenReturn(false);

        // act

        LiveData<DataResource<FireStoreEdit>> fireStoreEditsObservable = fireStoreViewModel.getFireStoreEditsObservable();

        Observer<DataResource<FireStoreEdit>> observer = Mockito.mock(Observer.class);

        fireStoreEditsObservable.observeForever(observer);

        fireStoreViewModel.changeCollectionName(COLLECTION, "new name");

        //verify

        verify(observer).onChanged(DataResource.error(FireStoreEdit.UPDATE_COLLECTION_NAME, null));


    }

    @Test
    public void testDeleteCollection_success(){

        // setup

        when(collectionMock.delete()).thenReturn(taskMock);

        when(taskMock.isSuccessful()).thenReturn(true);

        // act

        LiveData<DataResource<FireStoreEdit>> fireStoreEditsObservable = fireStoreViewModel.getFireStoreEditsObservable();

        Observer<DataResource<FireStoreEdit>> observer = Mockito.mock(Observer.class);

        fireStoreEditsObservable.observeForever(observer);

        fireStoreViewModel.deleteCollection(COLLECTION);

        // verify

        verify(observer).onChanged(DataResource.success(FireStoreEdit.DELETE_COLLECTION));
    }


    @Test
    public void testDeleteCollection_error(){

        // setup

        when(collectionMock.delete()).thenReturn(taskMock);

        when(taskMock.isSuccessful()).thenReturn(false);

        // act
        LiveData<DataResource<FireStoreEdit>> fireStoreEditsObservable = fireStoreViewModel.getFireStoreEditsObservable();

        Observer<DataResource<FireStoreEdit>> observer = Mockito.mock(Observer.class);

        fireStoreEditsObservable.observeForever(observer);

        fireStoreViewModel.deleteCollection(COLLECTION);

        // verify

        verify(observer).onChanged(DataResource.error(FireStoreEdit.DELETE_COLLECTION, null));


    }


    @Test
    public void testCreateNewCollection_success_resultAdded(){

        // setup

        DocumentReference documentReferenceMock = Mockito.mock(DocumentReference.class);

        when(collectionsMock.document()).thenReturn(documentReferenceMock);

        when(documentReferenceMock.getId()).thenReturn("44");

        when(collectionsMock.document("44")).thenReturn(collectionMock);

        when(collectionMock.set(any(Collection.class))).thenReturn(taskMock);

        when(taskMock.isSuccessful()).thenReturn(true);

        when(itemsMock.document(RESULT_ONE.getResultId().toString())).thenReturn(documentReferenceMock);


        // code will now create document references to store in firestore and HashMap

        when(Mocks.firestoreMock.collection("movies_en")).thenReturn(itemsMock);

        when(firestoreMock.collection("movies_es")).thenReturn(itemsMock);

        when(documentReferenceMock.set(any(Map.class))).thenReturn(taskMock);


        // act
        LiveData<DataResource<FireStoreEdit>> fireStoreEditsObservable = fireStoreViewModel.getFireStoreEditsObservable();

        Observer<DataResource<FireStoreEdit>> observer = Mockito.mock(Observer.class);

        fireStoreEditsObservable.observeForever(observer);

        Collection collection = fireStoreViewModel.createNewCollection(COLLECTION.getName(), RESULT_ONE);

        fireStoreViewModel.addNewCollection(collection, RESULT_ONE);

        // verify

        verify(observer).onChanged(DataResource.success(FireStoreEdit.ADD_COLLECTION_WITH_ITEM));

    }


    @Test
    public void testCreateNewCollection_success_resultNotAdded(){

        // setup

        DocumentReference documentReferenceMock = Mockito.mock(DocumentReference.class);

        when(collectionsMock.document()).thenReturn(documentReferenceMock);

        when(documentReferenceMock.getId()).thenReturn("44");

        when(collectionsMock.document("44")).thenReturn(collectionMock);

        when(collectionMock.set(any(Collection.class))).thenReturn(taskMock);

        when(taskMock.isSuccessful()).thenReturn(true).thenReturn(false);

        when(itemsMock.document(RESULT_ONE.getResultId().toString())).thenReturn(documentReferenceMock);

        // code will now create document references to store in firestore and HashMap

        when(Mocks.firestoreMock.collection("movies_en")).thenReturn(itemsMock);

        when(firestoreMock.collection("movies_es")).thenReturn(itemsMock);

        when(documentReferenceMock.set(any(Map.class))).thenReturn(taskMock);

        // act

        LiveData<DataResource<FireStoreEdit>> fireStoreEditsObservable = fireStoreViewModel.getFireStoreEditsObservable();

        Observer<DataResource<FireStoreEdit>> observer = Mockito.mock(Observer.class);

        fireStoreEditsObservable.observeForever(observer);

        Collection collection = fireStoreViewModel.createNewCollection(COLLECTION.getName(), RESULT_ONE);

        fireStoreViewModel.addNewCollection(collection, RESULT_ONE);


        // verify

        verify(observer).onChanged(DataResource.error(FireStoreEdit.ADD_COLLECTION_WITH_ITEM, null));

    }


    @Test
    public void testCreateNewCollection_success_noResult(){

        // setup

        DocumentReference emptyDocumentReference = Mockito.mock(DocumentReference.class);

        when(collectionsMock.document()).thenReturn(emptyDocumentReference);

        when(emptyDocumentReference.getId()).thenReturn("44");

        when(collectionsMock.document("44")).thenReturn(collectionMock);

        when(collectionMock.set(any(Collection.class))).thenReturn(taskMock);

        when(taskMock.isSuccessful()).thenReturn(true);

        // act

        LiveData<DataResource<FireStoreEdit>> fireStoreEditsObservable = fireStoreViewModel.getFireStoreEditsObservable();

        Observer<DataResource<FireStoreEdit>> observer = Mockito.mock(Observer.class);

        fireStoreEditsObservable.observeForever(observer);

       Collection collection = fireStoreViewModel.createNewCollection(COLLECTION.getName(), null);

       fireStoreViewModel.addNewCollection(collection, null);


        // verify

        verify(observer).onChanged(DataResource.success(FireStoreEdit.ADD_EMPTY_COLLECTION));

    }


    @Test
    public void testCreateNewCollection_error(){

        // setup

        DocumentReference emptyDocumentReference = Mockito.mock(DocumentReference.class);

        when(collectionsMock.document()).thenReturn(emptyDocumentReference);

        when(emptyDocumentReference.getId()).thenReturn("44");

        when(collectionsMock.document("44")).thenReturn(collectionMock);

        when(collectionMock.set(any(Collection.class))).thenReturn(taskMock);

        when(taskMock.isSuccessful()).thenReturn(false);

        // act

        LiveData<DataResource<FireStoreEdit>> fireStoreEditsObservable = fireStoreViewModel.getFireStoreEditsObservable();

        Observer<DataResource<FireStoreEdit>> observer = Mockito.mock(Observer.class);

        fireStoreEditsObservable.observeForever(observer);

        Collection collection = fireStoreViewModel.createNewCollection(COLLECTION.getName(), null);

        fireStoreViewModel.addNewCollection(collection, null);


        // verify

        verify(observer).onChanged(DataResource.error(FireStoreEdit.ADD_EMPTY_COLLECTION, null));

    }


    @After
    public void tearDown(){

        Mocks.reset();

    }


}




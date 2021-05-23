package com.example.qfilm.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.qfilm.data.models.entities.Collection;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.repositories.utils.DataResource;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.concurrent.Executors;

import static com.example.qfilm.data.models.entities.MediaType.MOVIE;
import static com.example.qfilm.data.models.entities.MediaType.SERIES;

public class FireStoreViewModel extends ViewModel {

    private static final String TAG = "FireStoreViewModel";

    private MutableLiveData<DataResource<List<Result>>> collectionItemsObservable;

    private MutableLiveData<DataResource<List<Collection>>> collectionsObservable;

    private Boolean reachedEndOfList;

    private MutableLiveData<DataResource<FireStoreEdit>> fireStoreEditsObservable;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseUser firebaseUser;

    private FirebaseFunctions firebaseFunctions;

    private Long lastTimeStamp;


    public enum FireStoreEdit{
        UPDATE_COLLECTION_NAME,
        DELETE_COLLECTION,
        DELETE_ITEM,
        ADD_ITEM,
        ADD_EMPTY_COLLECTION,
        ADD_COLLECTION_WITH_ITEM

    }


    public FireStoreViewModel(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore, FirebaseFunctions firebaseFunctions){

        this.firebaseFirestore = firebaseFirestore;

        this.firebaseUser = firebaseAuth.getCurrentUser();

        this.firebaseFunctions = firebaseFunctions;

        lastTimeStamp = null;

        collectionItemsObservable = new MutableLiveData<>();

        collectionsObservable = new MutableLiveData<>();

        fireStoreEditsObservable = new MutableLiveData<>();

        reachedEndOfList = false;

    }


    /**
     *
     * getters
     *
     * **/


    public Boolean getReachedEndOfList() {
        return reachedEndOfList;
    }


    public LiveData<DataResource<List<Collection>>> getCollectionsObservable() {

        return collectionsObservable;
    }


    public LiveData<DataResource<List<Result>>> getCollectionItemsObservable() {
        return collectionItemsObservable;
    }



    public LiveData<DataResource<FireStoreEdit>> getFireStoreEditsObservable() {
        return  fireStoreEditsObservable;
    }


    /**
     *
     * fetch methods for collections and collection items in firestore
     *
     * **/

    public Void fetchCollectionItems(String collectionId, String language) {

        /**
         * I've used a cloud function for this becuase every collection item is a reference
         * to a document in firestore and therefore multiple queries for each documents has
         * to be made. I thought it would be appropriate to let cloud function handle that
         * instead of client, but if not tell me!
         *
         * **/

        if(!reachedEndOfList) {

            // Create the arguments to the callable function.
            Map<String, Object> data = new HashMap<>();
            data.put("collectionId", collectionId);
            data.put("language", language);
            data.put("lastTimeStamp", lastTimeStamp);

            firebaseFunctions.getHttpsCallable("fetchCollectionItems")
                    .call(data)
                    .continueWith(new Continuation<HttpsCallableResult, Void>() {
                        @Override
                        public Void then(@NonNull Task<HttpsCallableResult> task) {

                            // cloud function returns a list of objects where the first objects are result objects
                            // and the last two are information about lastPage and lastTimeStamp

                            List<Map<String, Object>> res = new ArrayList<>();

                            try {

                                res = (List<Map<String, Object>>) task.getResult().getData();

                            } catch (Exception exception) {

                                Log.i(TAG, "then: exception when fetching collection items " + exception.getMessage());

                                collectionItemsObservable.setValue(DataResource.error(null, null));

                                return null;
                            }

                            if(res.isEmpty()){

                                collectionItemsObservable.setValue(DataResource.success(new ArrayList<>()));

                                return null;
                            }

                            // check if last page

                            if ((Boolean) res.get(res.size() - 2).get("lastPage")) {

                                reachedEndOfList = true;

                                lastTimeStamp = null;

                            }else{

                                lastTimeStamp = (Long) res.get(res.size() - 1).get("lastTimeStamp");
                            }

                            // convert to Result objects and update ui

                            List<Result> results = toResultsList(res);

                            collectionItemsObservable.setValue(DataResource.success(results));

                            return null;

                        }

                    });
        }

        return null;

    }


    private List<Result> toResultsList(List<Map<String, Object>> res){

        List<Result> results = new ArrayList<>();

        for(int i = 0; i < res.size() - 2; ++i){

            if(res.get(i) != null ) {

                Result result = new Result();

                result.setResultId((int) res.get(i).get("resultId"));

                result.setTitle((String) res.get(i).get("title"));

                result.setOriginalTitle((String) res.get(i).get("originalTitle"));

                result.setPopularity((double) res.get(i).get("popularity"));

                result.setPosterPath((String) res.get(i).get("posterPath"));

                result.setOverview((String) res.get(i).get("overview"));


                if(((String)res.get(i).get("mediaType")).equals("MOVIE")){
                    result.setMediaType(MOVIE);
                }else{
                    result.setMediaType(SERIES);
                }

                results.add(result);

            }

        }

        return results;

    }


    public Void fetchCollections(){

      CollectionReference collectionsReference = firebaseFirestore.collection("users")
                .document(firebaseUser.getUid()).collection("collections");

        Query query = collectionsReference.orderBy("timeStamp", Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    List<Collection> collections =  task.getResult().toObjects(Collection.class);

                    collectionsObservable.setValue(DataResource.success(collections));

                }else{

                    String errorMessage = task.getException().getMessage();

                    Log.e(TAG, "onEvent: " + errorMessage);

                    collectionsObservable.setValue(DataResource.error(null,
                            errorMessage != null ? errorMessage : "Unknown error"));
                }
            }
        });

        return null;

    }


    public void saveToCollection(Collection collection, Result result){

        /**
         *
         * Adds item to users collection if it doesn't already exist
         *
         * **/

        DocumentReference documentReference = firebaseFirestore.collection("users")
                .document(firebaseUser.getUid()).collection("collections")
                .document(collection.getDocumentId());

        // first checking if item exists

        documentReference.collection("items")
                .document(result.getResultId().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    if(task.getResult().exists()){

                        fireStoreEditsObservable.setValue(DataResource.error(FireStoreEdit.ADD_ITEM, "already added"));

                    }else{


                        // collection items are stored with doc refs to the object in firestore

                        String collectionName = result.getMediaType() == MOVIE ? "movies_" : "series_";

                        DocumentReference documentReferenceEn =
                                firebaseFirestore.collection(collectionName + "en")
                                        .document(String.valueOf(result.getResultId()));

                        System.out.println(documentReferenceEn);

                        DocumentReference documentReferenceEs =  firebaseFirestore.collection(collectionName + "es")
                                .document(String.valueOf(result.getResultId()));

                        Map<String, Object> collectionItem = new HashMap<>();

                        collectionItem.put("path_en", documentReferenceEn);
                        collectionItem.put("path_es", documentReferenceEs);
                        collectionItem.put("timeStamp", System.currentTimeMillis());


                        // add collection item to the collection

                        documentReference.collection("items")
                                .document(result.getResultId().toString()).set(collectionItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    // update path of poster if this is the first item to the collection
                                    if(collection.getCollectionCount() == 0){
                                        updatePosterPathOfOldestItem(collection, result.getPosterPath());
                                    }

                                    // updates collection count which is displayed to user
                                    documentReference.update("collectionCount", FieldValue.increment(1));

                                    fireStoreEditsObservable.setValue(DataResource.success(FireStoreEdit.ADD_ITEM));

                                }else{

                                    fireStoreEditsObservable.setValue(DataResource.error(FireStoreEdit.ADD_ITEM, null));
                                }
                            }
                        });
                    }
                }else{

                    fireStoreEditsObservable.setValue(DataResource.error( FireStoreEdit.ADD_ITEM, null));

                }
            }
        });
    }


    public void deleteFromCollection(Collection collection, Result result){

        DocumentReference documentReference = firebaseFirestore.collection("users")
                .document(firebaseUser.getUid()).collection("collections")
                .document(collection.getDocumentId());

        documentReference.collection("items")
                .document(result.getResultId().toString()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    fireStoreEditsObservable.setValue(
                            DataResource.success( FireStoreEdit.DELETE_ITEM));

                    // updates collection count which is displayed to user
                    documentReference.update("collectionCount", FieldValue.increment( -1 ));

                }else{

                    fireStoreEditsObservable.setValue(
                            DataResource.error(FireStoreEdit.DELETE_ITEM, null));
                }

            }
        });

    }


    public Void changeCollectionName(Collection collection, String newName){

        DocumentReference documentReference = firebaseFirestore.collection("users")
                .document(firebaseUser.getUid()).collection("collections")
                .document(collection.getDocumentId());

        documentReference.update("name", newName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    fireStoreEditsObservable.setValue(
                            DataResource.success(FireStoreEdit.UPDATE_COLLECTION_NAME));

                }else{

                    fireStoreEditsObservable.setValue(
                            DataResource.error(FireStoreEdit.UPDATE_COLLECTION_NAME,null));
                }
            }
        });

        return null;

    }



    public Void deleteCollection(Collection collection){

        DocumentReference documentReference = firebaseFirestore.collection("users")
                .document(firebaseUser.getUid()).collection("collections")
                .document(collection.getDocumentId());


        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    // if collection exists it is deleted and afterwards I delete the subcollection "items"
                    // on a background thread. I guess it could be appropriate to do this in cloud function so
                    // that it is done and is not interrupted by network failure or anything
                    // with client, but i did'nt have time in the end to fix it before hand in

                    fireStoreEditsObservable.setValue(
                            DataResource.success(FireStoreEdit.DELETE_COLLECTION));

                    Executors.newSingleThreadExecutor().submit(new Runnable() {
                        @Override
                        public void run() {
                            deleteCollectionItems(collection.getDocumentId(), "en");
                            deleteCollectionItems(collection.getDocumentId(), "es");
                        }
                    });

                }else{

                    fireStoreEditsObservable.setValue(
                            DataResource.error(FireStoreEdit.DELETE_COLLECTION, null));
                }

            }
        });

        return null;

    }


    private void deleteCollectionItems(String collectionId, String language) {

        final int[] tries = new int[1];

        firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("collections")
                .document(collectionId).collection("items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for(DocumentSnapshot doc : task.getResult()){
                        String docId = doc.getId();
                        deleteItem(docId, collectionId, language);
                    }
                  
                }else{

                    if(tries[0] < 5) {
                        ++tries[0];

                        deleteCollectionItems(collectionId, language);
                    }
                }
            }
        });
    }


    private void deleteItem(String docId, String collectionId, String language) {

        final int[] tries = new int[1];

        firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("collections")
                .document(collectionId).collection("items").document(docId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){

                    if(tries[0] < 5) {
                        ++tries[0];
                        deleteItem(docId, collectionId, language);
                    }
            }
        }});

    }


    public Collection createNewCollection(String collectionName, @Nullable Result result){

        CollectionReference collectionReference = firebaseFirestore.collection("users")
                .document(firebaseUser.getUid()).collection("collections");

        String documentId = collectionReference.document().getId();

        Collection collection = new Collection(collectionName, result == null ? 0 : 1,
                result == null ? null : result.getPosterPath(), documentId,
                (int) (System.currentTimeMillis() / 1000));

        return collection;


    }


    public Collection addNewCollection(Collection collection, @Nullable Result result) {

        /**
         *
         * Adds a new collection and a potential item to that collection.
         *
         * **/

        DocumentReference documentReference = firebaseFirestore.collection("users")
                .document(firebaseUser.getUid()).collection("collections").document(collection.getDocumentId());

        documentReference.set(collection)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            if(result != null) {

                                // collection items are stored with doc refs to the object in firestore

                                String collection = result.getMediaType() == MOVIE ? "movies_" : "series_";

                                DocumentReference documentReferenceEn =
                                        firebaseFirestore.collection(collection + "en")
                                                .document(String.valueOf(result.getResultId()));

                                DocumentReference documentReferenceEs =  firebaseFirestore.collection(collection + "es")
                                        .document(String.valueOf(result.getResultId()));

                                Map<String, Object> collectionItem = new HashMap<>();

                                collectionItem.put("path_en", documentReferenceEn);
                                collectionItem.put("path_es", documentReferenceEs);
                                collectionItem.put("timeStamp", System.currentTimeMillis());


                                documentReference.collection("items")
                                        .document(result.getResultId().toString())
                                        .set(collectionItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            fireStoreEditsObservable.setValue(
                                                    DataResource.success(FireStoreEdit.ADD_COLLECTION_WITH_ITEM));

                                        }else{

                                            fireStoreEditsObservable.setValue(
                                                    DataResource.error(FireStoreEdit.ADD_COLLECTION_WITH_ITEM, null));
                                        }
                                    }
                                });
                            }else{

                                fireStoreEditsObservable.setValue(DataResource.success(FireStoreEdit.ADD_EMPTY_COLLECTION));
                            }

                        }else{

                            fireStoreEditsObservable.setValue(DataResource.error(FireStoreEdit.ADD_EMPTY_COLLECTION, null));

                        }
                    }
                });

        return collection;
    }


    public void updatePosterPathOfOldestItem(Collection collection, String posterPath){

        DocumentReference documentReference = firebaseFirestore.collection("users")
                .document(firebaseUser.getUid()).collection("collections")
                .document(collection.getDocumentId());

        documentReference.update("oldestItemPosterPath", posterPath);

    }

}

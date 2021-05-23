package com.example.qfilm.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.IndexQuery;
import com.algolia.search.saas.Query;
import com.algolia.search.saas.Request;
import com.example.qfilm.data.models.entities.MediaType;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.repositories.utils.DataResource;
import com.example.qfilm.utils.Constants;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.qfilm.data.models.entities.MediaType.MOVIE;
import static com.example.qfilm.data.models.entities.MediaType.SERIES;



/**
 *
 * Algolia is used to be able to search in firestore by matching parts of titles since
 * firestore only has exact match queries. https://www.algolia.com/
 *
 * **/

public class AlgoliaSearchViewModel extends ViewModel {

    private static final String TAG = "AlgoliaSearchViewModel";

    private MutableLiveData<DataResource<List<Result>>> queryResult;

    private Client client;

    private Request onGoingQuery;

    public AlgoliaSearchViewModel(Client client){

        this.client = client;
        onGoingQuery = null;
        queryResult = new MutableLiveData<>();
    }


    public MutableLiveData<DataResource<List<Result>>> getQueryResult() {
        return queryResult;
    }


    public Void search(String searchText, String language){

        if(searchText.isEmpty()){

            queryResult.setValue(DataResource.success(new ArrayList<>()));
        }else{

        if(onGoingQuery != null){

            onGoingQuery.cancel();

        }

        List<IndexQuery> queries = new ArrayList<>();

        queries.add(
                new IndexQuery(
                        "movies_" + language,
                        new Query(searchText)
                                .setHitsPerPage(Constants.PAGE_SIZE * 2)
                )
        );

        queries.add(
                new IndexQuery(
                        "series_" + language,
                        new Query(searchText)
                                .setHitsPerPage(Constants.PAGE_SIZE * 2)
                )
        );


       onGoingQuery = client.multipleQueriesAsync(queries, Client.MultipleQueriesStrategy.NONE, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {

                if(error == null) {

                    List<Result> resultsMovies = new ArrayList<>();

                    List<Result> resultsSeries = new ArrayList<>();

                    try {

                        JSONArray queryResults = content.getJSONArray("results");

                        JSONArray movies = queryResults.getJSONObject(0).getJSONArray("hits");

                        JSONArray series = queryResults.getJSONObject(1).getJSONArray("hits");

                        resultsMovies = toResultList(movies, MOVIE);

                        resultsSeries = toResultList(series, SERIES);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (resultsMovies.size() > 0 || resultsSeries.size() > 0) {
                        resultsMovies.addAll(resultsSeries);

                        queryResult.setValue(DataResource.success(resultsMovies));
                    }
                }else{

                    queryResult.setValue(DataResource.error(null, null));
                }

                onGoingQuery = null;

            }
        });

       }

        return null;

    }


    private List<Result> toResultList(JSONArray jsonArray, MediaType mediaType) throws JSONException {

        List<Result> results = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); ++i){

            Result result = new Result();

            JSONObject data = ((JSONObject) jsonArray.get(i)).getJSONObject("data");

            result.setTitle(data.getString("title"));
            result.setOriginalTitle(data.getString("originalTitle"));
            result.setResultId(data.getInt("resultId"));
            result.setOverview(data.getString("overview"));
            result.setPosterPath(data.getString("posterPath"));
            result.setMediaType(mediaType);

            results.add(result);
        }

        return results;
    }


}

package com.example.qfilm.viewmodels;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.repositories.utils.DataResource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.List;

import utils.Mocks;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static utils.TestDummyData.RESULT_ONE;

public class AlgoliaSearchViewModelTest {


    // tested
    AlgoliaSearchViewModel algoliaSearchViewModel;

    // mocked json response

    JSONArray jsonArrayMock = Mockito.mock(JSONArray.class);

    JSONObject jsonObjectMock = Mockito.mock(JSONObject.class);

    AlgoliaException exceptionMock = Mockito.mock(AlgoliaException.class);

    // liveData and observer

    LiveData<DataResource<List<Result>>> resultsLiveData;

    Observer observer;


    /**
     A JUnit Test Rule that swaps the background executor used by the Architecture Components
     with a different one which executes each task synchronously. Used because you can't invoke
     observeForever on a background thread **/


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule =
            new InstantTaskExecutorRule();


    @Before
    public void setup(){

        algoliaSearchViewModel = new AlgoliaSearchViewModel(Mocks.client);

        resultsLiveData = algoliaSearchViewModel.getQueryResult();

        observer = Mockito.mock(Observer.class);

        resultsLiveData.observeForever(observer);

    }


    @After
    public void teardown(){

        Mocks.reset();
    }


    @Test
    public void search_success() throws JSONException {

        // setup

        when(Mocks.client.multipleQueriesAsync(any(List.class),
                eq(Client.MultipleQueriesStrategy.NONE), any(CompletionHandler.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        ((CompletionHandler) invocation.getArgument(2)).requestCompleted(jsonObjectMock,
                                null);

                        return null;
                    }
                });

        // Response from Algolia, one result in the movies index

        JSONObject resultObject = new JSONObject();

        JSONObject dataObject = new JSONObject();

        resultObject.put("title", RESULT_ONE.getTitle());

        resultObject.put("originalTitle", RESULT_ONE.getOriginalTitle());

        resultObject.put("resultId", RESULT_ONE.getResultId());

        resultObject.put("overview", RESULT_ONE.getOverview());

        resultObject.put("posterPath", RESULT_ONE.getPosterPath());

        dataObject.put("data", resultObject);

        JSONArray jsonArray = new JSONArray();

        jsonArray.put(dataObject);


        when(jsonObjectMock.getJSONArray("results")).thenReturn(jsonArrayMock);
        when(jsonArrayMock.getJSONObject(0)).thenReturn(jsonObjectMock);
        when(jsonArrayMock.getJSONObject(1)).thenReturn(jsonObjectMock);
        when(jsonObjectMock.getJSONArray("hits")).thenReturn(jsonArray)
                .thenReturn(jsonArrayMock);


        // act

        algoliaSearchViewModel.search("search this", "en");

        // verify

        verify(observer).onChanged(DataResource.success(Collections.singletonList(RESULT_ONE)));

    }


    @Test
    public void search_error(){

        // setup

        when(Mocks.client.multipleQueriesAsync(any(List.class),
                eq(Client.MultipleQueriesStrategy.NONE), any(CompletionHandler.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        ((CompletionHandler) invocation.getArgument(2)).requestCompleted(null,
                                exceptionMock);

                        return null;
                    }
                });

        // act

       algoliaSearchViewModel.search("search this", "en");


       // verify

        verify(observer).onChanged(DataResource.error(null, null));

    }


    @Test
    public void search_empty() throws JSONException {

        // setup

        when(Mocks.client.multipleQueriesAsync(any(List.class),
                eq(Client.MultipleQueriesStrategy.NONE), any(CompletionHandler.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {

                        ((CompletionHandler) invocation.getArgument(2)).requestCompleted(jsonObjectMock,
                                null);

                        return null;
                    }
                });


        // no results in either index this time

        JSONArray jsonArray = new JSONArray();


        when(jsonObjectMock.getJSONArray("results")).thenReturn(jsonArrayMock);
        when(jsonArrayMock.getJSONObject(0)).thenReturn(jsonObjectMock);
        when(jsonArrayMock.getJSONObject(1)).thenReturn(jsonObjectMock);
        when(jsonObjectMock.getJSONArray("hits")).thenReturn(jsonArray)
                .thenReturn(jsonArrayMock);


        // act

        algoliaSearchViewModel.search("search this", "en");

        // verify

        verifyZeroInteractions(observer);

    }
}

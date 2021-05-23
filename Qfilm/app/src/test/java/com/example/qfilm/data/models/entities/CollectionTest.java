package com.example.qfilm.data.models.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.TestDummyData.COLLECTION;

public class CollectionTest {


    @Test
    void gettersAndSetters_allProperties_returnCorrectValues(){

        Collection correctCollection = COLLECTION;

        Collection collection = new Collection();

        collection.setName(correctCollection.getName());

        collection.setCollectionCount(correctCollection.getCollectionCount());

        collection.setDocumentId(correctCollection.getDocumentId());

        collection.setOldestItemPosterPath(correctCollection.getOldestItemPosterPath());

        collection.setTimeStamp(correctCollection.getTimeStamp());


        assertEquals(correctCollection, collection);

    }

}

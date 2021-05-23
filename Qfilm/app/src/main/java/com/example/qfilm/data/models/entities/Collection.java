package com.example.qfilm.data.models.entities;


import androidx.annotation.Nullable;


/**
 * Data class for Collection in Firestore
 * **/

public class Collection {

    private String documentId;

    private String name;

    private Integer collectionCount;

    private String oldestItemPosterPath;

    private Integer timeStamp; // used for sorting collections


    public Collection(String name, Integer collectionCount, String oldestItemPosterPath, String documentId,
    Integer timeStamp) {
        this.name = name;
        this.collectionCount = collectionCount;
        this.oldestItemPosterPath = oldestItemPosterPath;
        this.documentId = documentId;
        this.timeStamp = timeStamp;
    }


    public Collection()
    {

    }


    /**
     * getters and setters
     * **/

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCollectionCount() {
        return collectionCount;
    }

    public void setCollectionCount(int collectionCount) {
        this.collectionCount = collectionCount;
    }

    public String getOldestItemPosterPath() {
        return oldestItemPosterPath;
    }

    public void setOldestItemPosterPath(String oldestItemPosterPath) {
        this.oldestItemPosterPath = oldestItemPosterPath;
    }

    public Integer getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Integer fireStoreTimeStamp) {
        this.timeStamp = fireStoreTimeStamp;
    }


    /**
     * Override equals to be able to compare collection by values
     * **/

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null){
            return false;
        }

        if(obj.getClass() != getClass()){
            return false;
        }

        Collection other = (Collection) obj;

        return name.equals(other.getName()) && documentId.equals(other.getDocumentId());
    }


}

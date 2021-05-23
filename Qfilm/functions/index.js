// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');

const updateFirestoreAccordingToChanges = require("./fetch").updateFirestoreAccordingToChanges;

const { default: algoliasearch } = require('algoliasearch');



let baseUrlMovieItem = "https://api.themoviedb.org/3/movie/";

let baseUrlSeriesItem = "https://api.themoviedb.org/3/tv/";

const apiKeyAlgolia = "b317021f332275c02d51993c631e5a0e";

const appIdAlgolia = "F2JPNKMMX3";

const algoliaClient = algoliasearch(appIdAlgolia, apiKeyAlgolia);

const algoliaIndexMoviesEs = algoliaClient.initIndex("movies_es");
const algoliaIndexMoviesEn = algoliaClient.initIndex("movies_en");
const algoliaIndexSeriesEs = algoliaClient.initIndex("series_es");
const algoliaIndexSeriesEn = algoliaClient.initIndex("series_en")


/**
 * This function will trigger once per 24 hours and update firestore collections 
 * of movies and series according to changes made to TMDB Api. 
 * 
 * I chose to store the movies and series of the app in Firestore mainly to be 
 * able to provide a search function. TMDB:s search functionality don't have 
 * filtering options (which I needed since the app only displays movies and 
 * series with keyword "lgbt"), so using that would not have resulted in a good search experience.
 * 
 * It is also used for when users saves items in firestore. Since the app has two languages I 
 * needed to be able to get their collections in different languages, 
 * and this way I can store documentReferences to different languages.
 * 
 * **/

exports.scheduleMoviesAndSeriesChanges = functions.region("europe-west2").pubsub.schedule('0 0 * * *').onRun((context) => {

    return Promise.all([updateFirestoreAccordingToChanges(baseUrlMovieItem, 1),
        updateFirestoreAccordingToChanges(baseUrlSeriesItem, 1)
    ]).then(done => {
        console.log("updated database according to changes");
        return null;
    }).catch(error => {
        console.error(error);
        return null;
    })

});


// these functions trigger when writes are made to firestore
// to update Algolia indexes (search engine platform)

exports.updateMovieEsIndex = functions.region("europe-west2").firestore.document("movies_es/{id}")
    .onWrite((change, context) => {

        const objectID = context.params.id;

        if (change.after.exists) {

            const data = change.after.data();

            return algoliaIndexMoviesEs.saveObject({ data, objectID });

        } else {

            return algoliaIndexSeriesEn.deleteObject(objectID);

        }

    });


exports.updateSeriesEsIndex = functions.region("europe-west2").firestore.document("series_es/{id}")
    .onWrite((change, context) => {

        const objectID = context.params.id;

        if (change.after.exists) {

            const data = change.after.data();

            return algoliaIndexSeriesEs.saveObject({ data, objectID });

        } else {

            return algoliaIndexSeriesEn.deleteObject(objectID);
        }

    });


exports.updateMovieEnIndex = functions.region("europe-west2").firestore.document("movies_en/{id}")
    .onWrite((change, context) => {

        const objectID = context.params.id;

        if (change.after.exists) {

            const data = change.after.data();

            return algoliaIndexMoviesEn.saveObject({ data, objectID });

        } else {

            return algoliaIndexSeriesEn.deleteObject(objectID);

        }

    });


exports.updateSeriesEnIndex = functions.region("europe-west2").firestore.document("series_en/{id}")
    .onWrite((change, context) => {

        const objectID = context.params.id;

        if (change.after.exists) {

            const data = change.after.data();

            return algoliaIndexSeriesEn.saveObject({ data, objectID });

        } else {

            return algoliaIndexSeriesEn.deleteObject(objectID);
        }

    });




exports.fetchCollectionItems = functions.region("europe-west2").https.onCall(async(data, context) => {

    /**
     * retrieves user's collection items and then for each item fetches the actual 
     * object (movie or series). 
     * 
     * parameter 'data' contains fields: language and collectionId 
     * 
     * **/

    const pageSize = 20;

    // Checking that the user is authenticated.
    if (!context.auth) {
        // Throwing an HttpsError so that the client gets the error details.
        throw new functions.https.HttpsError('failed-precondition', 'The function must be called ' +
            'while authenticated.');
    }

    // Getting the collections 'items' collection

    let itemsRef = admin.firestore().collection("users").doc(context.auth.uid)
        .collection("collections").doc(data.collectionId).collection("items");

    let itemsQuery;

    if (data.lastTimeStamp === null) {

        itemsQuery = itemsRef.orderBy("timeStamp", "desc").limit(pageSize + 1);

    } else {

        itemsQuery = itemsRef.orderBy("timeStamp", "desc").limit(pageSize + 1)
            .startAfter(data.lastTimeStamp);
    }

    let items = await itemsQuery.get();

    // can return immediatly if the collection is empty

    if (items.docs.length === 0) {

        return new Array();
    }

    // checking if this was the last page 

    let lastPage;

    if (items.docs.length <= pageSize) {

        lastPage = true;

    } else {

        items.docs.pop();

        lastPage = false;
    }

    // now fetch documents in items collection

    let results = new Array();

    results = await Promise.all(items.docs.map(async item => {

        let result;

        let docRef;

        if (data.language === "en") {

            docRef = item.data().path_en;

        } else {

            docRef = item.data().path_es;
        }

        result = await docRef.get();

        // trying english result if spanish is not available

        if (result.data() == null) {

            if (data.language === "es") {

                docRef = item.data().path_en;

                result = await docRef.get();
            }

        }


        return result.data();


    }));


    let lastTimeStamp = items.docs[items.docs.length - 1].data().timeStamp;

    results.push({ lastPage }); // lastPage is sent back to client to know if it can call for more items

    results.push({ lastTimeStamp }); // lastTimeStamp is sent back to calling client to store where to begin in the next fetch for collection items

    return results;

});
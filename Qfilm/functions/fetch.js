/*

This file contains functions that are called in order to update firestore 
according to changes made to TMDB Api so that it is reflected in the app.

*/


const axios = require('axios');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');

admin.initializeApp();

const fireStoreDb = admin.firestore();

let baseUrlMovieItem = "https://api.themoviedb.org/3/movie/";

let baseUrlSeriesItem = "https://api.themoviedb.org/3/tv/";

const apiKey = "2cb1658a1165dde7479be49601eb1658";



async function updateFirestoreAccordingToChanges(baseUrl, page) {


    /**
     * 1. fetches a list of changes (movie and series id:s) that has been made the past 24 hours
     * 2. iterates through changes and:
     * 3. queries for the movie's/series keywords
     * 4. checks to se if keyword 'lgbt' is present
     * 5. fetches the movie/series if the keyword is present
     * 6. deletes or sets the updated object in firestore collection
     * 
     * **/


    let response = await fetchChanges(baseUrl, page);

    if (response !== null && response.data.results.length > 0) {

        await Promise.all(response.data.results.map(async changedObject => {

            let collectionEs;

            let collectionEn;

            if (baseUrl === baseUrlMovieItem) {
                collectionEs = "movies_es"
                collectionEn = "movies_en";
            } else {
                collectionEs = "series_es";
                collectionEn = "series_en";
            }

            let objectHasKeyword = await hasKeyword(changedObject, baseUrl);

            if (objectHasKeyword instanceof ItemDeleted) {

                await Promise.all([deleteFromFireStore(collectionEs, changedObject.id),
                    deleteFromFireStore(collectionEn, changedObject.id)
                ]);

            } else if (objectHasKeyword === true) {

                let responseEn = await fetchObject(changedObject.id, baseUrl, "en");

                await updateFirestore(responseEn, collectionEn);

                let responseEs = await fetchObject(changedObject.id, baseUrl, "es");

                await updateFirestore(responseEs, collectionEs)

            }

        }));

        return updateFirestoreAccordingToChanges(baseUrl, ++page);


    } else {

        return null;
    }

}


async function fetchChanges(baseUrl, page) {

    let url = baseUrl + `changes?api_key=${apiKey}&page=${page}`;

    let response;

    try {

        response = await axios.get(url);

    } catch (error) {

        console.log(error);

        return null;
    }


    return response;


}


async function hasKeyword(object, baseUrl) {

    lgbtKeywordId = 158718;

    url = baseUrl + `${object.id}/keywords?api_key=2cb1658a1165dde7479be49601eb1658`;

    let response;


    try {

        response = await axios.get(url);

    } catch (error) {

        if (error.response.status === 404) {

            return new ItemDeleted(object.id);
        }

        console.log(error);
    }


    if (typeof response !== 'undefined') {

        let res = false;

        for (let i = 0; i < response.data.keywords.length; ++i) {

            if (lgbtKeywordId === response.data.keywords[i].id) {
                res = true;
                break;
            }
        }

        return res;

    } else {
        return false;
    }

}


async function fetchObject(id, baseUrl, language) {

    let url = baseUrl + `${id}?api_key=${apiKey}&language=${language}`;

    let response;

    try {
        response = await axios.get(url);

    } catch (error) {

        console.log(error);

        if (error.response.status === 404) {

            return new ItemDeleted(id);

        }


        return null;
    }

    return response;

}


async function updateFirestore(response, collection) {

    if (response instanceof ItemDeleted) {

        await deleteFromFireStore(collection, response.id);

    } else if (response !== null) {


        if (shouldSaveToFirestore(response.data)) {


            let fireStoreResult = {
                resultId: response.data.id,
                title: typeof response.data.title !== 'undefined' ? response.data.title : response.data.name,
                originalTitle: typeof response.data.original_title !== 'undefined' ? response.data.original_title : response.data.original_name,
                popularity: response.data.popularity,
                posterPath: response.data.poster_path,
                backdropPath: response.data.backdrop_path,
                overview: response.data.overview,
                genres: getGenreIdsArray(response.data.genres),
                mediaType: typeof response.data.title !== 'undefined' ? "MOVIE" : "SERIES"
            }

            await addToFirestore(collection, fireStoreResult);

        }

    }

    return null;

}


function getGenreIdsArray(genres) {

    let genreIds = new Array();

    for (let i = 0; i < genres.length; ++i) {

        genreIds.push(genres[i].id);

    }

    return genreIds;
}


async function deleteFromFireStore(collection, resultId) {
    try {

        let res = await fireStoreDb.collection(`${collection}`).doc(`${resultId}`).delete();

        console.log(`result ${resultId} deleted from ${collection}`);

    } catch (error) {

        console.log(error);
    }
}


async function addToFirestore(collection, result) {


    try {

        let res = await fireStoreDb.collection(`${collection}`).doc(`${result.resultId}`).set(result);

        console.log(`result ${result.resultId} set in collection ${collection}`);

        return true;

    } catch (error) {

        console.log(error);

        return false;
    }
}


function shouldSaveToFirestore(data) {

    return typeof data.backdrop_path !== 'undefined' &&
        data.backdrop_path !== null &&
        data.backdrop_path !== "" &&

        typeof data.poster_path !== 'undefined' &&
        data.poster_path !== null &&
        data.poster_path !== "" &&

        typeof data.overview !== 'undefined' &&
        data.overview !== null &&
        data.overview !== ""

}


class ItemDeleted {
    constructor(id) {
        this.id = id;
    }
}


module.exports = {
    shouldSaveToFirestore,
    fetchChanges,
    addToFirestore,
    deleteFromFireStore,
    hasKeyword,
    fetchObject,
    updateFirestore,
    updateFirestoreAccordingToChanges,
    ItemDeleted
}
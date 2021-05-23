/* 

I used this file in the beginning to populate firestore with items from TMDB

*/


const admin = require("firebase-admin");

const axios = require('axios');

admin.initializeApp({
    credential: admin.credential.applicationDefault(),
    databaseURL: "https://qfilm-4953f.firebaseio.com"
});

const fireStoreDb = admin.firestore();

const apiKey = "2cb1658a1165dde7479be49601eb1658";

let baseUrlMovies = `https://api.themoviedb.org/3/discover/movie?api_key=${apiKey}&with_keywords=158718&`;

let baseUrlSeries = `https://api.themoviedb.org/3/discover/tv?api_key=${apiKey}&with_keywords=158718&`;



async function main() {


    return Promise.all([fetchResults("en", 1, baseUrlMovies), fetchResults("es", 1, baseUrlMovies),
        fetchResults("en", 1, baseUrlSeries), fetchResults("es", 1, baseUrlSeries)
    ]).then(done => {
        console.log("updated database");
        return null;
    }).catch(error => {
        console.error(error);
        return null;
    });
}


async function fetchResults(language, page, baseUrl) {

    let url = baseUrl + `page=${page}&language=${language}`

    let response;

    try {

        response = await axios.get(url);

    } catch (error) {

        console.log(error);
    }

    if (typeof response !== 'undefined') {

        if (response.data.results.length > 0) {

            await Promise.all(response.data.results.map(async result => {

                if (shouldSaveToFirestore(result)) {

                    let fireStoreResult = {
                        resultId: result.id,
                        title: typeof result.title !== 'undefined' ? result.title : result.name,
                        originalTitle: typeof result.original_title !== 'undefined' ? result.original_title : result.original_name,
                        popularity: result.popularity,
                        posterPath: result.poster_path,
                        backdropPath: result.backdrop_path,
                        overview: result.overview,
                        genres: result.genre_ids,
                        mediaType: baseUrl === baseUrlMovies ? "MOVIE" : "SERIES"
                    }

                    if (baseUrl === baseUrlMovies) {
                        await addToFirestore(`movies_${language}`, fireStoreResult);
                    } else if (baseUrl === baseUrlSeries) {
                        await addToFirestore(`series_${language}`, fireStoreResult);
                    }

                }

            }));

            return fetchResults(language, ++page, baseUrl);

        }

    }

    return null;

}


async function addToFirestore(collection, result) {


    try {

        await fireStoreDb.collection(`${collection}`).doc(`${result.resultId}`).set(result);

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

main();
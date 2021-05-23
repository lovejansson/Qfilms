// functions to test from fetch.js

const hasKeyword = require("../fetch.js").hasKeyword;

const shouldSaveToFirestore = require("../fetch").shouldSaveToFirestore;

const fetchChanges = require("../fetch").fetchChanges;

const fetchObject = require("../fetch").fetchObject;

const updateFirestore = require("../fetch").updateFirestore;

const updateFirestoreAccordingToChanges = require("../fetch").updateFirestoreAccordingToChanges;

const ItemDeleted = require("../fetch").ItemDeleted;

// dependecies 

const admin = require('firebase-admin');


// msw is used to interfere with network request and send back the response I want
// instead of making real request. 

const rest = require("msw").rest;

const setupServer = require("msw/node").setupServer;


// urls and api key

let baseUrlMovies = `https://api.themoviedb.org/3/discover/movie?api_key=2cb1658a1165dde7479be49601eb1658&with_keywords=158718&`;

let baseUrlSeries = `https://api.themoviedb.org/3/discover/tv?api_key=2cb1658a1165dde7479be49601eb1658&with_keywords=158718&`;

let baseUrlMovieItem = "https://api.themoviedb.org/3/movie/";

let baseUrlSeriesItem = "https://api.themoviedb.org/3/tv/";

const apiKey = "2cb1658a1165dde7479be49601eb1658";

// mocks and fake data

const fakeData = require("./fakedata.js");

const set = jest.fn();

const doc = jest.fn(() => ({set, }));

const collection = jest.spyOn(admin.firestore(), 'collection').mockReturnValue({ doc });

const server = setupServer(

    rest.get(
        `${baseUrlMovieItem}:id/keywords?api_key=${apiKey}`, (req, res, ctx) => {

            switch (req.params.id) {
                case "1":

                    return res(
                        ctx.json(fakeData.responseWithLgbtKeyword)
                    )

                case "0":

                    return res(
                        ctx.json(fakeData.responseMissingLgbtKeyword)
                    )

                case "-1":

                    return res(
                        ctx.status(404)
                    )

                case "-4":

                    return res(
                        ctx.status(500)
                    )
            }


        }),


    rest.get(`${baseUrlMovieItem}changes?api_key=${apiKey}&page=1`, (req, res, ctx) => {

        return res(
            ctx.json(fakeData.responseChangesEmpty)
        )
    }),

    rest.get(`${baseUrlSeriesItem}changes?api_key=${apiKey}&page=1`, (req, res, ctx) => {

        return res(
            ctx.json(fakeData.responseChanges)
        )
    }),


    rest.get(`${baseUrlMovieItem}:id?api_key=${apiKey}&language=en`, (req, res, ctx) => {

        switch (req.params.id) {

            case '1':

                return res(
                    ctx.json(fakeData.responseMovieDetail.data)
                )

            case '0':
                return res(
                    ctx.status(505)
                )

            case '-1':
                return res(
                    ctx.status(404)
                )
        }

    }),

)

beforeAll(() => server.listen());

afterAll(() => server.close());

afterEach(() => {

        server.resetHandlers();

        jest.clearAllMocks();
    }

);


test("updateFirestoreAccordingToChanges - changes in one movie with keyword lgbt ", async() => {

    // server will return a list of changes with one id equal to 1 (which has keyword lgbt)

    server.use(

        rest.get(`${baseUrlMovieItem}changes?api_key=${apiKey}&page=1`, (req, res, ctx) => {

            return res.once(
                ctx.json(fakeData.responseChanges)
            )
        }),

    )

    // act

    let res = await updateFirestoreAccordingToChanges(baseUrlMovieItem, 1)

    // firestore collections of movies will be updated with the changed movie item

    expect(collection).toHaveBeenCalledWith("movies_en");

    expect(collection).toHaveBeenCalledWith("movies_es")

    expect(doc).toHaveBeenCalledWith("1");

    expect(set).toHaveBeenCalledWith(fakeData.result);

    expect(set.mock.calls.length).toBe(2);

    expect(doc.mock.calls.length).toBe(2);

    expect(res).toBe(null);
})



test("updateFirestoreAccordingToChanges - no changes", async() => {

    // server will return a list of changes with id:s of 0 (has no lgbt keyword)

    server.use(

        rest.get(`${baseUrlMovieItem}changes?api_key=${apiKey}&page=1`, (req, res, ctx) => {

            return res.once(
                ctx.json(fakeData.responseChangesNoLgbt)
            )
        }),

    )

    // act 

    let res = await updateFirestoreAccordingToChanges(baseUrlMovieItem, 1)

    // no innvocations with firestore collections or documents

    expect(collection.mock.calls.length).toBe(0);

    expect(doc.mock.calls.length).toBe(0);

    expect(set.mock.calls.length).toBe(0);

    expect(res).toBe(null);
})


test("updateFirestoreAccordingToChanges - deleted item", async() => {

    // server will return a list of changes with one id of -1 (delted item will return 404 when
    // trying to fetch keywords
    server.use(

        rest.get(`${baseUrlMovieItem}changes?api_key=${apiKey}&page=1`, (req, res, ctx) => {

            return res.once(
                ctx.json(fakeData.responseChangesDeletedItem)
            )
        }),

    )

    // act

    let res = await updateFirestoreAccordingToChanges(baseUrlMovieItem, 1)

    // the item will be deleted from two collections. 

    expect(collection).toHaveBeenCalledWith("movies_en");

    expect(collection).toHaveBeenCalledWith("movies_es")

    expect(doc).toHaveBeenCalledWith("-1");

    expect(set.mock.calls.length).toBe(0);

    expect(doc.mock.calls.length).toBe(2);

    expect(res).toBe(null);
})


test("fetchChanges - success return changes", async() => {

    let res = await fetchChanges(baseUrlSeriesItem, 1);

    expect(res.data).toEqual(fakeData.responseChanges);
})


test("fetchChanges - error return null", async() => {

    server.use(

        rest.get(`${baseUrlSeriesItem}changes?api_key=${apiKey}&page=1`, (req, res, ctx) => {

            return res.once(
                ctx.status(505)
            )
        }),

    )

    let res = await fetchChanges(baseUrlSeriesItem, 1);

    expect(res).toBe(null);
})


test("hasKeyword - yes return true", async() => {

    object = {

        id: 1
    }

    let res = await hasKeyword(object, baseUrlMovieItem);

    expect(res).toBe(true);

});


test("hasKeyword - no return false", async() => {

    object = {

        id: 0
    }

    let res = await hasKeyword(object, baseUrlMovieItem);

    expect(res).toBe(false);

});


test("hasKeyword - error 404 return ItemDeleted", async() => {

    object = {

        id: -1
    }

    let res = await hasKeyword(object, baseUrlMovieItem);

    expect(res).toEqual(new ItemDeleted(-1));

});


test("hasKeyword - other error return false", async() => {

    object = {

        id: -4
    }

    let res = await hasKeyword(object, baseUrlMovieItem);

    expect(res).toEqual(false);

});


test("fetchObject - success return response", async() => {

    let res = await fetchObject(1, baseUrlMovieItem, "en");

    expect(res.data).toEqual(fakeData.responseMovieDetail.data);
});


test("fetchObject - 404 return ItemDeleted object", async() => {

    let res = await fetchObject(-1, baseUrlMovieItem, "en");

    expect(res).toEqual(new ItemDeleted(-1));
});


test("fetchObject - other error return null", async() => {

    let res = await fetchObject(0, baseUrlMovieItem, "en");

    expect(res).toEqual(null);
});


test("updateFireStore - deleted item", async() => {

    let res = await updateFirestore(new ItemDeleted(1), "movies_en");

    expect(collection).toHaveBeenCalledWith("movies_en");

    expect(doc).toHaveBeenCalledWith("1");

    expect(res).toBe(null);

});


test("updateFireStore - null item", async() => {
    let res = await updateFirestore(null, "movies_en");

    expect(collection.mock.calls.length).toBe(0);

    expect(res).toBe(null);

});


test("updateFireStore - movie has changed and should be set in firestore", async() => {

    let res = await updateFirestore(fakeData.responseMovieDetail, "movies_en");

    expect(collection).toHaveBeenCalledWith("movies_en");

    expect(doc).toHaveBeenCalledWith("1");

    expect(set).toHaveBeenCalledWith(fakeData.result);

    expect(res).toBe(null);

});


test("updateFireStore - series has changed and should be set in firestore", async() => {

    let res = await updateFirestore(fakeData.responseSeriesDetail, "series_en");

    expect(collection).toHaveBeenCalledWith("series_en");

    expect(doc).toHaveBeenCalledWith("1");

    expect(set).toHaveBeenCalledWith(fakeData.resultSeries);

    expect(res).toBe(null);

});


test("shouldSaveToFireStore - null backdrop return false", async() => {

    data = {

        backdrop_path: null,
        poster_path: "poster path",
        overview: "overview"
    }

    expect(shouldSaveToFirestore(data)).toBe(false);

});


test("shouldSaveToFireStore - undefined backdrop return false", async() => {

    data = {

        backdrop_path: undefined,
        poster_path: "poster path",
        overview: "overview"
    }

    expect(shouldSaveToFirestore(data)).toBe(false);

});


test("shouldSaveToFireStore - null poster return false", async() => {

    data = {

        backdrop_path: "backdrop path",
        poster_path: null,
        overview: "overview"
    }

    expect(shouldSaveToFirestore(data)).toBe(false);

});


test("shouldSaveToFireStore - undefined poster return false", async() => {

    data = {


        backdrop_path: "backdrop path",
        poster_path: undefined,
        overview: "overview"
    }

    expect(shouldSaveToFirestore(data)).toBe(false);

});


test("shouldSaveToFireStore - empty overview return false", async() => {

    data = {

        backdrop_path: "backdrop path",
        poster_path: "poster path",
        overview: ""
    }

    expect(shouldSaveToFirestore(data)).toBe(false);

});


test("shouldSaveToFireStore - has all properties return true", async() => {

    data = {

        backdrop_path: "backdrop path",
        poster_path: "poster path",
        overview: "overview"
    }

    expect(shouldSaveToFirestore(data)).toBe(true);

});
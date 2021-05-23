const responseWithLgbtKeyword = {
    id: 84892,
    keywords: [{
            id: 818,
            name: "based on novel or book"
        },
        {
            id: 158718,
            name: "lgbt"
        }
    ]
};


const responseMissingLgbtKeyword = {
    id: 84892,
    keywords: [{
            id: 818,
            name: "based on novel or book"
        },
        {
            id: 4,
            name: "lgbt"
        }
    ]
}


const result = {
    resultId: 1,
    title: "title",
    originalTitle: "original title",
    popularity: 1,
    posterPath: "poster path",
    backdropPath: "backdrop path",
    overview: "overview",
    mediaType: "MOVIE",
    genres: [1, 2]
}

const resultSeries = {
    resultId: 1,
    title: "title",
    originalTitle: "original title",
    popularity: 1,
    posterPath: "poster path",
    backdropPath: "backdrop path",
    overview: "overview",
    mediaType: "SERIES",
    genres: [1, 2]
}


const responseMovieDetail = {

    data: {

        id: 1,
        title: "title",
        original_title: "original title",
        popularity: 1,
        poster_path: "poster path",
        backdrop_path: "backdrop path",
        overview: "overview",
        genres: [{ id: 1, name: "genre1" }, { id: 2, name: "genre2" }],

    }
}

const responseSeriesDetail = {

    data: {

        id: 1,
        name: "title",
        original_name: "original title",
        popularity: 1,
        poster_path: "poster path",
        backdrop_path: "backdrop path",
        overview: "overview",
        genres: [{ id: 1, name: "genre1" }, { id: 2, name: "genre2" }],
    }
}

const responseChanges = {
    "results": [{
            "id": 1,
            "adult": false
        },
        {
            "id": 0,
            "adult": false
        },

    ],
    "page": 1,
    "total_pages": 1,
    "total_results": 1
}

const responseChangesDeletedItem = {
    "results": [{
            "id": -1,
            "adult": false
        },
        {
            "id": 0,
            "adult": false
        },

    ],
    "page": 1,
    "total_pages": 1,
    "total_results": 1
}

const responseChangesNoLgbt = {

    "results": [{
            "id": 0,
            "adult": false
        },
        {
            "id": 0,
            "adult": false
        },

    ],
    "page": 1,
    "total_pages": 1,
    "total_results": 1

}

const responseChangesEmpty = {
    "results": [],
    "page": 1,
    "total_pages": 1,
    "total_results": 1
}


module.exports = {
    responseMissingLgbtKeyword,
    responseWithLgbtKeyword,
    result,
    resultSeries,
    responseChanges,
    responseChangesEmpty,
    responseMovieDetail,
    responseSeriesDetail,
    responseChangesNoLgbt,
    responseChangesDeletedItem
};
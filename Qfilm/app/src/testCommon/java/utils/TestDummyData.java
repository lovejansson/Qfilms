package utils;

import com.example.qfilm.data.models.entities.Collection;
import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.data.models.entities.Image;
import com.example.qfilm.data.models.entities.Images;
import com.example.qfilm.data.models.entities.MediaType;
import com.example.qfilm.data.models.entities.MovieDetails;
import com.example.qfilm.data.models.entities.Rating;
import com.example.qfilm.data.models.entities.Ratings;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.data.models.entities.SeriesDetails;
import com.example.qfilm.data.models.entities.Video;
import com.example.qfilm.data.models.entities.Videos;
import com.example.qfilm.data.models.entities.GenreListResponse;
import com.example.qfilm.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class TestDummyData {

    /**
     *
     *  Common properties in entities
     *
     * **/

    public  static final String TITLE_ONE = "Call Me by Your Name";
    public  static final String TITLE_TWO = "Elite";

    public  static final String ORG_TITLE_ONE = "Call Me by Your Name";
    public  static final String ORG_TITLE_TWO = "Élite";

    public  static final Integer ID_ONE = 398818;
    public  static final Integer ID_TWO = 76669;

    public static  final String IMDB_ID_ONE = "tt5726616";
    public static  final String IMDB_ID_TWO = "tt444444";

    public  static final String OVERVIEW_ONE = "In 1980s Italy, a relationship begins between " +
            "seventeen-year-old teenage Elio and the older adult man hired as his father's research assistant.";
    public  static final String OVERVIEW_TWO = "When three working class kids enroll in the most " +
            "exclusive school in Spain, the clash between the wealthy and the poor students leads to tragedy.";


    public static final Integer TIMESTAMP_ONE = 44444444;
    public static final Integer TIMESTAMP_TWO = 4444;

    public  static final String RELEASE_DATE_ONE = "2017-09-01";
    public  static final String RELEASE_DATE_TWO = "2018-10-05";

    public  static final Integer RUNTIME_ONE = 132;
    public  static final Integer RUNTIME_TWO = 60;

    public  static final Double POPULARITY_ONE = 88.88;
    public  static final Double POPULARITY_TWO = 243.254;

    public  static final int[] EPISODE_RUNTIME = {60};
    public static final Integer SEASONS = 4;


    /**
     *
     *  Image
     *
     * **/

    public static final String IMAGE_FILE_PATH_BACKDROP_ONE = "/hrMbAi9fPTmc6EtpyyAgDKznptu.jpg";

    public static final String IMAGE_FILE_PATH_POSTER_ONE =  "/a9cZC9mutwtS9F9uvIDqeoO8gSJ.jpg";

    public static final Image IMAGE_BACKDROP_ONE = new Image(IMAGE_FILE_PATH_BACKDROP_ONE, 1.78);

    public static final Image IMAGE_POSTER_ONE = new Image(IMAGE_FILE_PATH_POSTER_ONE, 0.6);

    public static final String IMAGE_FILE_PATH_BACKDROP_TWO = "/qKouUKk4oD6xWoo8OXsogY7rsPT.jpg";

    public static final String IMAGE_FILE_PATH_POSTER_TWO =  "/aZWogNfqTUMMMunDPyLkopHnW3z.jpg";

    public static final Image IMAGE_BACKDROP_TWO = new Image(IMAGE_FILE_PATH_BACKDROP_TWO, 1.78);

    public static final Image IMAGE_POSTER_TWO = new Image(IMAGE_FILE_PATH_POSTER_TWO, 0.6);


    /**
     *
     *  Images
     *
     * **/

    public static final Images IMAGES_ONE = new Images(Collections.singletonList(IMAGE_BACKDROP_ONE),
            Collections.singletonList(IMAGE_POSTER_ONE));

    public static final Images IMAGES_TWO = new Images(Collections.singletonList(IMAGE_BACKDROP_TWO),
            Collections.singletonList(IMAGE_POSTER_TWO));


     /**
     *
     *  Genre
     *
     * **/

    public static final String GENRE_NAME_DRAMA = "Drama";

    public static final String GENRE_NAME_COMEDY = "Comedy";

    public static final String GENRE_NAME_ACTION = "Action";

    public static final String GENRE_NAME_ROMANCE = "Romance";


    public static final int GENRE_ID_DRAMA = 18;

    public static final int GENRE_ID_COMEDY = 35;

    public static final int GENRE_ID_ACTION = 3;

    public static final int GENRE_ID_ROMANCE = 5;


    public static  final Genre DRAMA = new Genre(GENRE_ID_DRAMA, GENRE_NAME_DRAMA, TIMESTAMP_ONE,
            true, true);

    public static  final Genre COMEDY = new Genre(GENRE_ID_COMEDY, GENRE_NAME_COMEDY);

    public  static  final Genre ACTION = new Genre(GENRE_ID_ACTION, GENRE_NAME_ACTION);

    public  static  final Genre ROMANCE = new Genre(GENRE_ID_ROMANCE, GENRE_NAME_ROMANCE);

    public  static final List<Genre> GENRES_ONE = new ArrayList<>(Arrays.asList(COMEDY, DRAMA));

    public  static final List<Genre> GENRES_TWO = new ArrayList<>(Arrays.asList(ROMANCE,  ACTION));


    /**
     *
     *  Result
     *
     * **/

    public static final Result RESULT_ONE = new Result(ID_ONE, TITLE_ONE, ORG_TITLE_ONE, POPULARITY_ONE,
            IMAGE_FILE_PATH_POSTER_ONE, IMAGE_FILE_PATH_BACKDROP_ONE, OVERVIEW_ONE, TIMESTAMP_ONE, MediaType.MOVIE);

    public static final Result RESULT_TWO = new Result(ID_TWO, TITLE_TWO, ORG_TITLE_TWO, POPULARITY_TWO,
            IMAGE_FILE_PATH_POSTER_TWO, IMAGE_FILE_PATH_BACKDROP_TWO, OVERVIEW_TWO, TIMESTAMP_TWO, MediaType.MOVIE);


    /**
     *
     *  Video
     *
     * **/

    public static final String VIDEO_KEY_ONE = "Z9AYPxH5NTM";
    public static final String VIDEO_SITE_ONE = "Youtube";
    public static final String VIDEO_TYPE_ONE = "Trailer";

    public static final String VIDEO_KEY_TWO = "keyTwo";
    public static final String VIDEO_SITE_TWO = "VIMEO";
    public static final String VIDEO_TYPE_TWO = "Short";

    public static final  Video VIDEO_ONE = new Video(VIDEO_KEY_ONE, VIDEO_SITE_ONE,
            VIDEO_TYPE_ONE);

    public static final  Video VIDEO_TWO = new Video(VIDEO_KEY_TWO, VIDEO_SITE_TWO,
            VIDEO_TYPE_TWO);

    /**
     *
     *  Videos
     *
     * **/

    public static  final Videos VIDEOS_ONE = new Videos(Collections.singletonList(VIDEO_ONE));
    public static  final Videos VIDEOS_TWO = new Videos(Collections.singletonList(VIDEO_TWO));
    public static  final Videos VIDEOS_EMPTY = new Videos(new ArrayList<>());


    /**
     *
     *  Rating
     *
     * **/


    public static final String RATING_SOURCE_ONE = "Internet Movie Database";

    public static final String RATING_SOURCE_TWO = "Rotten Tomatoes";

    public static final String RATING_SOURCE_THREE = "Metacritic";

    public static final String RATING_VALUE_ONE = "7.9/10";

    public static final String RATING_VALUE_TWO = "94%";

    public static final String RATING_VALUE_THREE = "93/100";

    public static final Rating RATING_ONE = new Rating(RATING_SOURCE_ONE, RATING_VALUE_ONE);

    public static Rating RATING_TWO = new Rating(RATING_SOURCE_TWO, RATING_VALUE_TWO);

    public static final Rating RATING_THREE = new Rating(RATING_SOURCE_THREE, RATING_VALUE_THREE);

    public static  final Ratings RATINGS_EMPTY = new Ratings("title", new ArrayList<>());

    // Rating(s) entity[{"Source":"Internet Movie Database","Value":"7.9/10"},{"Source":"Rotten Tomatoes","Value":"94%"},{"Source":"Metacritic","Value":"93/100"}]

    public static Ratings RATINGS_ONE = new Ratings(TITLE_ONE, Collections.singletonList(RATING_ONE));
    public static Ratings RATINGS_TWO = new Ratings(TITLE_TWO, Collections.singletonList(RATING_TWO));


    /**
     *
     *  MovieDetails
     *
     * **/

    public static final MovieDetails MOVIE_DETAILS_ONE = new MovieDetails(ID_ONE, IMDB_ID_ONE, TITLE_ONE,
            ORG_TITLE_ONE, GENRES_ONE, OVERVIEW_ONE, IMAGE_FILE_PATH_POSTER_ONE,
            IMAGE_FILE_PATH_BACKDROP_ONE, RELEASE_DATE_ONE, RUNTIME_ONE, VIDEOS_ONE, IMAGES_ONE,
            Collections.singletonList(RATING_ONE), TIMESTAMP_ONE, "en");

    public static final MovieDetails MOVIE_DETAILS_TWO = new MovieDetails(ID_ONE, IMDB_ID_ONE, TITLE_ONE,
            ORG_TITLE_ONE, GENRES_ONE, OVERVIEW_ONE, IMAGE_FILE_PATH_POSTER_ONE,
            IMAGE_FILE_PATH_BACKDROP_ONE, RELEASE_DATE_ONE, RUNTIME_ONE, VIDEOS_ONE, IMAGES_ONE,
            Collections.singletonList(RATING_ONE), TIMESTAMP_ONE, "es");

    public static final MovieDetails MOVIE_DETAILS_EXPIRED_TIMESTAMP = new MovieDetails(ID_ONE, IMDB_ID_ONE, TITLE_ONE,
            ORG_TITLE_ONE, GENRES_ONE, OVERVIEW_ONE, IMAGE_FILE_PATH_POSTER_ONE,
            IMAGE_FILE_PATH_BACKDROP_ONE, RELEASE_DATE_ONE, RUNTIME_ONE, VIDEOS_ONE, IMAGES_ONE,
            Collections.singletonList(RATING_ONE),
            ((int) (System.currentTimeMillis() / 1000)) - Constants.TIME_LIMIT_DETAILS, "es");

    public static final MovieDetails MOVIE_DETAILS_FRESH_TIMESTAMP = new MovieDetails(ID_ONE, IMDB_ID_ONE, TITLE_ONE,
            ORG_TITLE_ONE, GENRES_ONE, OVERVIEW_ONE, IMAGE_FILE_PATH_POSTER_ONE,
            IMAGE_FILE_PATH_BACKDROP_ONE, RELEASE_DATE_ONE, RUNTIME_ONE, VIDEOS_ONE, IMAGES_ONE,
            Collections.singletonList(RATING_ONE), ((int) (System.currentTimeMillis() / 1000)), "en" );


    /**
     *
     *  SeriesDetails
     *
     * **/

    public static  final SeriesDetails SERIES_DETAILS = new SeriesDetails(ID_TWO, IMDB_ID_TWO, TITLE_TWO,
            ORG_TITLE_TWO, GENRES_TWO, OVERVIEW_TWO, IMAGE_FILE_PATH_POSTER_TWO,
            IMAGE_FILE_PATH_BACKDROP_TWO, RELEASE_DATE_TWO, RUNTIME_TWO, VIDEOS_TWO, IMAGES_TWO,
            Collections.singletonList(RATING_ONE), SEASONS, EPISODE_RUNTIME, "en");


    /**
     *  GenreListResponse
     *
     * **/

    public static final GenreListResponse GENRE_LIST_RESPONSE_ONE = new GenreListResponse(GENRES_ONE);

    public static final GenreListResponse GENRE_LIST_RESPONSE_TWO = new GenreListResponse(GENRES_TWO);

    /**
     *
     *  Collection
     *
     * **/

    public static  final Collection COLLECTION = new Collection("my collection", 0, "",
            "4", 6);


}

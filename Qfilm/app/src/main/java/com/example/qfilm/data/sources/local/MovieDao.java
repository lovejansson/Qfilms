package com.example.qfilm.data.sources.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.data.models.entities.MovieDetails;
import com.example.qfilm.data.models.entities.Ratings;
import com.example.qfilm.data.models.entities.Result;
import com.example.qfilm.data.models.entities.ResultMovie;
import com.example.qfilm.data.models.entities.ResultSeries;
import com.example.qfilm.data.models.relations.GenreMovieResultJoin;
import com.example.qfilm.data.models.relations.GenreSeriesResultJoin;
import com.example.qfilm.data.models.entities.SeriesDetails;

import java.util.List;


@Dao
public interface MovieDao {

    /**
     * Results
     *
     **/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovieResult(ResultMovie resultMovie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSeriesResult(ResultSeries resultSeries);

    @Query("SELECT * from results_movies WHERE resultId = :resultId")
    Result getMovieResult(Integer resultId);

    @Query("SELECT * from results_series WHERE resultId = :resultId")
    Result getSeriesResult(Integer resultId);


    /**
     * Genres
     *
     **/

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertGenre(Genre genre);

    @Query("DELETE from genres")
    void deleteAllGenres();


    @Query("UPDATE genres SET isSeriesGenre = :isSeriesGenre WHERE genreId = :id")
    void updateIsSeriesGenre(Boolean isSeriesGenre, Integer id);

    @Query("UPDATE genres SET isMovieGenre = :isMovieGenre  WHERE genreId = :id")
    void updateIsMovieGenre(Boolean isMovieGenre, Integer id);

    @Query("SELECT * from genres WHERE isMovieGenre= 1 order by name ASC ")
    LiveData<List<Genre>> getMovieGenres();

    @Query("SELECT * from genres WHERE isSeriesGenre = 1 order by name ASC ")
    LiveData<List<Genre>> getSeriesGenres();


    /**
     *
     * joins
     *
     * **/


    @Query("SELECT * from genre_movies_join WHERE genreId = :genreId ORDER BY popularity DESC LIMIT :pageSize + 1 OFFSET (:page - 1) * :pageSize")
    List<GenreMovieResultJoin> getGenreAndMovieJoins(Integer genreId, Integer page, Integer pageSize);

    @Query("SELECT * from genre_series_join WHERE genreId = :genreId ORDER BY popularity DESC LIMIT :pageSize + 1 OFFSET (:page - 1) * :pageSize")
    List<GenreSeriesResultJoin> getGenreAndSeriesJoins(Integer genreId, Integer page, Integer pageSize);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGenreAndMovieJoin(GenreMovieResultJoin genreMovieResultJoin);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGenreAndSeriesJoin(GenreSeriesResultJoin genreSeriesResultJoin);


    /**
     * Details tables
     *
     **/

    @Query("SELECT * from movie_details WHERE id = :id")
    LiveData<MovieDetails> getMovieDetails(Integer id);

    @Query("SELECT * from series_details WHERE id = :id")
    LiveData<SeriesDetails> getSeriesDetails(Integer id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovieDetails(MovieDetails movieDetails);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSeriesDetails(SeriesDetails seriesDetails);


    /**
     * Ratings
     *
     **/

    @Query("SELECT * from ratings WHERE title = :title")
    LiveData<Ratings> getRatings(String title);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRating(Ratings ratings);


}

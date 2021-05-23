package com.example.qfilm.data.sources.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.qfilm.data.models.entities.Genre;
import com.example.qfilm.data.models.entities.MovieDetails;
import com.example.qfilm.data.models.entities.Ratings;
import com.example.qfilm.data.models.entities.ResultMovie;
import com.example.qfilm.data.models.entities.ResultSeries;
import com.example.qfilm.data.models.relations.GenreMovieResultJoin;
import com.example.qfilm.data.models.relations.GenreSeriesResultJoin;
import com.example.qfilm.data.models.entities.SeriesDetails;

@Database(entities={ResultMovie.class, ResultSeries.class,  MovieDetails.class,  SeriesDetails.class,
        Genre.class, Ratings.class, GenreMovieResultJoin.class, GenreSeriesResultJoin.class},
        version=2, exportSchema = false)
@TypeConverters({com.example.qfilm.data.models.typeconverters.TypeConverters.class})

public abstract class MoviesRoomDatabase extends RoomDatabase {

    public abstract MovieDao movieDao();

}

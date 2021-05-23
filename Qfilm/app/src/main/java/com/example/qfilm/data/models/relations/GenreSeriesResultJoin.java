package com.example.qfilm.data.models.relations;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import org.jetbrains.annotations.NotNull;


@Entity(tableName = "genre_series_join")
public class GenreSeriesResultJoin extends GenreResultJoin {
    public GenreSeriesResultJoin(@NotNull Integer genreId, @NotNull Integer resultId,
                                 @NonNull Double popularity, @NonNull Long primaryKey) {
        super(genreId, resultId, popularity, primaryKey);
    }
}
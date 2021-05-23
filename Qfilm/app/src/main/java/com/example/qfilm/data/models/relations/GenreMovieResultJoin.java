package com.example.qfilm.data.models.relations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import org.jetbrains.annotations.NotNull;

@Entity(tableName = "genre_movies_join")
public class GenreMovieResultJoin extends GenreResultJoin {
    public GenreMovieResultJoin(@NotNull Integer genreId, @NotNull Integer resultId,
                                @NonNull Double popularity, @NonNull Long primaryKey) {
        super(genreId, resultId, popularity, primaryKey);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null){
            return false;
        }

        if(obj.getClass() != getClass()){
            return false;
        }

        GenreMovieResultJoin other = (GenreMovieResultJoin) obj;

        return getGenreId().equals(other.getGenreId()) && getResultId().equals(other.getResultId());

    }
}

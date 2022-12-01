package com.example.mvvmmovieapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mvvmmovieapp.apidata.trending.MovieItem

@Dao
interface SavedMoviesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(movie: MovieItem)

    @Delete
    suspend fun deleteMovie(movie: MovieItem)

    @Query("SELECT * FROM saved_movies")
    fun getSavedMovies(): LiveData<List<MovieItem>>
}
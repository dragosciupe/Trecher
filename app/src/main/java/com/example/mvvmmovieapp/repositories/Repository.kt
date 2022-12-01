package com.example.mvvmmovieapp.repositories

import com.example.mvvmmovieapp.api.RetrofitInstance
import com.example.mvvmmovieapp.apidata.trending.MovieItem
import com.example.mvvmmovieapp.database.MovieDatabase
import com.example.mvvmmovieapp.database.SavedMoviesDao
import javax.inject.Inject

class Repository @Inject constructor(private val movieDao: SavedMoviesDao) {

    fun getSavedMovies() = movieDao.getSavedMovies()

    suspend fun upsert(movie: MovieItem) = movieDao.upsert(movie)

    suspend fun deleteMovie(movie: MovieItem) = movieDao.deleteMovie(movie)

    suspend fun getTrendingMovies() = RetrofitInstance.api.getTrendingMovies()

    suspend fun getMovieCredits(movieId: Int) = RetrofitInstance.api.getMovieCredits(movieId)

    suspend fun getMovieDetails(movieId: Int) = RetrofitInstance.api.getMovieDetails(movieId)

    suspend fun searchMovie(movieName: String) = RetrofitInstance.api.searchMovie(movieName = movieName)
}
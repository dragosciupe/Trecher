package com.example.mvvmmovieapp.apidata.requests
import com.example.mvvmmovieapp.apidata.trending.MovieItem

data class FavoriteMovieRequest(
    val accountUsername: String,
    val movieEntity: MovieItem
)

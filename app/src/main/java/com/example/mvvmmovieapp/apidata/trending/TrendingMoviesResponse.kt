package com.example.mvvmmovieapp.apidata.trending

data class TrendingMoviesResponse(
    val page: Int,
    val results: List<MovieItem>,
    val total_pages: Int,
    val total_results: Int
)
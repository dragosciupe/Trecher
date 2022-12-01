package com.example.mvvmmovieapp.api

import com.example.mvvmmovieapp.apidata.cast.CastResponse
import com.example.mvvmmovieapp.apidata.moviedetails.DetailsResponse
import com.example.mvvmmovieapp.apidata.search.SearchResponse
import com.example.mvvmmovieapp.apidata.trending.TrendingMoviesResponse
import com.example.mvvmmovieapp.util.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {
   @GET("/3/trending/movie/week")
   suspend fun getTrendingMovies(
       @Query("api_key")
       api_key: String = API_KEY
   ): Response<TrendingMoviesResponse>

   @GET("/3/movie/{movie_id}/credits")
   suspend fun getMovieCredits(
       @Path("movie_id")
       movieId: Int,
       @Query("api_key")
       api_key: String = API_KEY,
   ): Response<CastResponse>

   @GET("/3/movie/{movie_id}")
   suspend fun getMovieDetails(
       @Path("movie_id")
       movieId: Int,
       @Query("api_key")
       api_key: String = API_KEY
   ): Response<DetailsResponse>

   @GET("/3/search/movie")
   suspend fun searchMovie(
       @Query("api_key")
       api_key: String = API_KEY,
       @Query("query")
       movieName: String
   ): Response<SearchResponse>
}
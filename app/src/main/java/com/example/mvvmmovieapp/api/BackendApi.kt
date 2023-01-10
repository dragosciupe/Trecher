package com.example.mvvmmovieapp.api

import com.example.mvvmmovieapp.apidata.reponses.BasicApiResponse
import com.example.mvvmmovieapp.apidata.reponses.ReviewResponse
import com.example.mvvmmovieapp.apidata.requests.AccountRequest
import com.example.mvvmmovieapp.apidata.requests.FavoriteMovieRequest
import com.example.mvvmmovieapp.apidata.requests.ReviewRequest
import com.example.mvvmmovieapp.apidata.trending.MovieItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BackendApi {

    @POST("/registerAccount")
    suspend fun registerAccount(
        @Body accountRequest: AccountRequest
    ): Response<BasicApiResponse>

    @POST("/loginAccount")
    suspend fun loginAccount(
        @Body accountRequest: AccountRequest
    ): Response<BasicApiResponse>

    @GET("/getFavoriteMovies")
    suspend fun getFavoriteMovies(
        @Query("accountUsername") accountUsername: String
    ): Response<List<MovieItem>>

    @POST("/addMovieToFavorites")
    suspend fun addMovieToFavorites(
        @Body favoriteMovieRequest: FavoriteMovieRequest
    ): Response<BasicApiResponse>

    @POST("/deleteMovieFromFavorites")
    suspend fun deleteMovieFromFavorites(
        @Body favoriteMovieRequest: FavoriteMovieRequest
    ): Response<BasicApiResponse>

    @POST("/addMovieReview")
    suspend fun addMovieReview(
        @Body reviewRequest: ReviewRequest
    ): Response<BasicApiResponse>

    @GET("/getAllReviewsForMovie")
    suspend fun getMovieReviews(
        @Query("movieId") movieId: String
    ): Response<List<ReviewResponse>>
}
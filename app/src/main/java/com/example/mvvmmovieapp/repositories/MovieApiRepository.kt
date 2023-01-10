package com.example.mvvmmovieapp.repositories

import com.example.mvvmmovieapp.api.BackendApi
import com.example.mvvmmovieapp.api.BackendApiRetrofitInstance
import com.example.mvvmmovieapp.api.MovieApiRetrofitInstance
import com.example.mvvmmovieapp.apidata.reponses.BasicApiResponse
import com.example.mvvmmovieapp.apidata.reponses.ReviewResponse
import com.example.mvvmmovieapp.apidata.requests.AccountRequest
import com.example.mvvmmovieapp.apidata.requests.FavoriteMovieRequest
import com.example.mvvmmovieapp.apidata.requests.ReviewRequest
import com.example.mvvmmovieapp.apidata.trending.MovieItem
import com.example.mvvmmovieapp.database.SavedMoviesDao
import com.example.mvvmmovieapp.util.Resource
import okhttp3.internal.concurrent.TaskRunner
import retrofit2.Response
import javax.inject.Inject

class MovieApiRepository @Inject constructor() {

    suspend fun getFavoriteMovies(accountUsername: String): Resource<List<MovieItem>> {
        return handleGeneralResponse { BackendApiRetrofitInstance.api.getFavoriteMovies(accountUsername)}
    }

    suspend fun addMovieToFavorites(favoriteMovieRequest: FavoriteMovieRequest): Resource<String> {
        return handleBasicApiResponse { BackendApiRetrofitInstance.api.addMovieToFavorites(favoriteMovieRequest)}
    }

    suspend fun deleteMovieFromFavorites(favoriteMovieRequest: FavoriteMovieRequest): Resource<String> {
        return handleBasicApiResponse { BackendApiRetrofitInstance.api.deleteMovieFromFavorites(favoriteMovieRequest)}
    }

    suspend fun loginAccount(accountRequest: AccountRequest) : Resource<String> {
        return handleBasicApiResponse { BackendApiRetrofitInstance.api.loginAccount(accountRequest) }
    }

    suspend fun registerAccount(accountRequest: AccountRequest) : Resource<String> {
        return handleBasicApiResponse { BackendApiRetrofitInstance.api.registerAccount(accountRequest) }
    }

    suspend fun addMovieReview(reviewRequest: ReviewRequest) : Resource<String> {
        return handleBasicApiResponse { BackendApiRetrofitInstance.api.addMovieReview(reviewRequest) }
    }

    suspend fun getAllReviews(movieId: String): Resource<List<ReviewResponse>> {
        return handleGeneralResponse { BackendApiRetrofitInstance.api.getMovieReviews(movieId) }
    }

    suspend fun getTrendingMovies() = MovieApiRetrofitInstance.api.getTrendingMovies()

    suspend fun getMovieCredits(movieId: Int) = MovieApiRetrofitInstance.api.getMovieCredits(movieId)

    suspend fun getMovieDetails(movieId: Int) = MovieApiRetrofitInstance.api.getMovieDetails(movieId)

    suspend fun searchMovie(movieName: String) = MovieApiRetrofitInstance.api.searchMovie(movieName = movieName)

    private suspend fun <T> handleGeneralResponse(
        httpRequest: suspend () -> Response<T>
    ): Resource<T> {
        return try {
            val networkCall = httpRequest()
            if(networkCall.isSuccessful) {
                Resource.Success(networkCall.body()!!)
            } else {
                Resource.Error(networkCall.message())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error("An unknown error occurred")
        }
    }

    private suspend fun handleBasicApiResponse(
        httpRequest: suspend () -> Response<BasicApiResponse>
    ): Resource<String> {
        return try {
            val networkCall = httpRequest()
            if(networkCall.isSuccessful && networkCall.body()!!.status) {
                Resource.Success(networkCall.body()!!.message)
            } else if(networkCall.isSuccessful) {
                Resource.Error(networkCall.body()?.message ?: networkCall.message())
            } else {
                Resource.Error(networkCall.message())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error("An unknown error occurred")
        }
    }
}
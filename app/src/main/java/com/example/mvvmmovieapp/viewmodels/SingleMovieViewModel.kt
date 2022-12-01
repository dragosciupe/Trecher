package com.example.mvvmmovieapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmmovieapp.apidata.cast.CastResponse
import com.example.mvvmmovieapp.apidata.moviedetails.DetailsResponse
import com.example.mvvmmovieapp.apidata.trending.MovieItem
import com.example.mvvmmovieapp.apidata.trending.TrendingMoviesResponse
import com.example.mvvmmovieapp.repositories.Repository
import com.example.mvvmmovieapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SingleMovieViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    var movieDetailsResponse: MutableLiveData<Resource<DetailsResponse>> = MutableLiveData()
    var movieCastResponse: MutableLiveData<Resource<CastResponse>> = MutableLiveData()


    fun saveMovie(movie: MovieItem) = viewModelScope.launch {
        repository.upsert(movie)
    }

    fun getMovieCredits(movieId: Int) = viewModelScope.launch {
        movieCastResponse.postValue(Resource.Loading())
        val response = repository.getMovieCredits(movieId)
        movieCastResponse.postValue(handleMovieCastResponse(response))
        Log.d("SearchMovie", "Credits called")
    }

    fun getMovieDetails(movieId: Int) = viewModelScope.launch {
        movieDetailsResponse.postValue(Resource.Loading())
        val response = repository.getMovieDetails(movieId)
        movieDetailsResponse.postValue(handleMovieDetailsResponse(response))
        Log.d("SearchMovie", "Details called")
    }

    private fun handleMovieCastResponse(
        response: Response<CastResponse>
    ): Resource<CastResponse> {
        if (response.isSuccessful) {
            response.body()?.let { castResponse ->
                return Resource.Success(castResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleMovieDetailsResponse(
        response: Response<DetailsResponse>
    ): Resource<DetailsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { detailsResponse ->
                return Resource.Success(detailsResponse)
            }
        }
        return Resource.Error(response.message())
    }

}
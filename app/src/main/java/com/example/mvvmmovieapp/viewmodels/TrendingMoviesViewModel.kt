package com.example.mvvmmovieapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmmovieapp.apidata.trending.MovieItem
import com.example.mvvmmovieapp.apidata.trending.TrendingMoviesResponse
import com.example.mvvmmovieapp.repositories.Repository
import com.example.mvvmmovieapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TrendingMoviesViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {
    var trendingMoviesResponse: MutableLiveData<Resource<TrendingMoviesResponse>> =
        MutableLiveData()

    fun getTrendingMovies() = viewModelScope.launch {
        trendingMoviesResponse.postValue(Resource.Loading())
        val response = repository.getTrendingMovies()
        trendingMoviesResponse.postValue(handleTrendingMoviesResponse(response))
    }

    private fun handleTrendingMoviesResponse(
        response: Response<TrendingMoviesResponse>
    ): Resource<TrendingMoviesResponse> {
        if (response.isSuccessful) {
            response.body()?.let { trendingResponse ->
                return Resource.Success(trendingResponse)
            }
        }
        return Resource.Error(response.message())
    }
}
package com.example.mvvmmovieapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmmovieapp.apidata.cast.CastResponse
import com.example.mvvmmovieapp.apidata.moviedetails.DetailsResponse
import com.example.mvvmmovieapp.apidata.search.SearchResponse
import com.example.mvvmmovieapp.apidata.trending.MovieItem
import com.example.mvvmmovieapp.apidata.trending.TrendingMoviesResponse
import com.example.mvvmmovieapp.repositories.Repository
import com.example.mvvmmovieapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchMoviesViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    var movieSearchResponse: MutableLiveData<Resource<SearchResponse>> = MutableLiveData()

    fun searchMovie(movieName: String) = viewModelScope.launch {
        movieSearchResponse.postValue(Resource.Loading())
        val response = repository.searchMovie(movieName)
        movieSearchResponse.postValue(handleMovieSearchResponse(response))
    }

    private fun handleMovieSearchResponse(
        response: Response<SearchResponse>
    ): Resource<SearchResponse> {
        if(response.isSuccessful) {
            response.body()?.let { searchResponse ->
                return Resource.Success(searchResponse)
            }
        }
        return Resource.Error(response.message())
    }
}
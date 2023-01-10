package com.example.mvvmmovieapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmmovieapp.apidata.cast.CastResponse
import com.example.mvvmmovieapp.apidata.moviedetails.DetailsResponse
import com.example.mvvmmovieapp.apidata.reponses.ReviewResponse
import com.example.mvvmmovieapp.apidata.requests.FavoriteMovieRequest
import com.example.mvvmmovieapp.apidata.requests.ReviewRequest
import com.example.mvvmmovieapp.apidata.trending.MovieItem
import com.example.mvvmmovieapp.repositories.MovieApiRepository
import com.example.mvvmmovieapp.util.DataStoreUtil
import com.example.mvvmmovieapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SingleMovieViewModel @Inject constructor(
    private val repository: MovieApiRepository,
    private val datastore: DataStoreUtil
) : ViewModel() {

    public lateinit var loggedInUsername: String

    init {
        viewModelScope.launch {
            loggedInUsername = datastore.getUsername()
        }
    }
    sealed class SingleMovieEvent {
        data class AddMovieResult(val data: String): SingleMovieEvent()
        data class AddReviewResult(val data: String): SingleMovieEvent()
        data class AddReviewSuccess(val review: ReviewResponse): SingleMovieEvent()
        data class GetReviewsSuccess(val reviews: List<ReviewResponse>): SingleMovieEvent()
        data class GetReviewsError(val message: String): SingleMovieEvent()
        object Loading: SingleMovieEvent()
    }

    var movieDetailsResponse: MutableLiveData<Resource<DetailsResponse>> = MutableLiveData()
    var movieCastResponse: MutableLiveData<Resource<CastResponse>> = MutableLiveData()

    private val _singleMovieEvent = MutableSharedFlow<SingleMovieEvent>()
    val singleMovieEvent: SharedFlow<SingleMovieEvent> = _singleMovieEvent

    private val _reviewsFlow = MutableSharedFlow<SingleMovieEvent>()
    val reviewsFlow: SharedFlow<SingleMovieEvent> = _reviewsFlow

    fun addMovieToFavorites(movie: MovieItem) = viewModelScope.launch {
        val favoriteMovieRequest = FavoriteMovieRequest(loggedInUsername, movie)
        _singleMovieEvent.emit(SingleMovieEvent.Loading)

        when(val response = repository.addMovieToFavorites(favoriteMovieRequest)) {
            is Resource.Success -> {
                _singleMovieEvent.emit(SingleMovieEvent.AddMovieResult(response.data ?: "Movie saved successfully"))
            }
            is Resource.Error -> {
                _singleMovieEvent.emit(SingleMovieEvent.AddMovieResult(response.message ?: "Unknown error occurred"))
            }
        }
    }

    fun addMovieReview(reviewRatingString: String, reviewText: String, movieId: Int) = viewModelScope.launch {
        if(reviewRatingString.isEmpty()) {
            _singleMovieEvent.emit(SingleMovieEvent.AddReviewResult("The rating must be between 1 and 5"))
            return@launch
        }
        val reviewRating = reviewRatingString.toDouble()
        if(reviewRating < 1 || reviewRating > 5) {
            _singleMovieEvent.emit(SingleMovieEvent.AddReviewResult("The rating must be between 1 and 5"))
            return@launch
        }

        val reviewRequest = ReviewRequest(
            loggedInUsername,
            movieId,
            reviewRating,
            reviewText
        )

        when(val response = repository.addMovieReview(reviewRequest)) {
            is Resource.Success -> {
                _singleMovieEvent.emit(SingleMovieEvent.AddReviewSuccess(
                    ReviewResponse(
                        loggedInUsername,
                        movieId,
                        reviewRating,
                        reviewText,
                        System.currentTimeMillis()
                    )
                ))
            }

            is Resource.Error -> {
                _singleMovieEvent.emit(SingleMovieEvent.AddReviewResult(response.message ?: "Unknown error occurred"))
            }
        }
    }

    fun getAllMovieReviews(movieId: Int) = viewModelScope.launch {
        delay(100L)
        when(val response = repository.getAllReviews(movieId.toString())) {
            is Resource.Success -> {
                _reviewsFlow.emit(SingleMovieEvent.GetReviewsSuccess(response.data?.sortedByDescending {
                    it.timestamp
                } ?: emptyList()))
                val reviewCount = response.data?.size ?: -1
                Log.d("Reviews", "retrieved in vm $reviewCount ${if(reviewCount == 1) "review" else "reviews"}")
            }

            is Resource.Error -> {
                _singleMovieEvent.emit(SingleMovieEvent.GetReviewsError("Unknown error occurred"))
            }
        }
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
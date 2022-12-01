package com.example.mvvmmovieapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmmovieapp.apidata.requests.FavoriteMovieRequest
import com.example.mvvmmovieapp.apidata.trending.MovieItem
import com.example.mvvmmovieapp.repositories.MovieApiRepository
import com.example.mvvmmovieapp.util.DataStoreUtil
import com.example.mvvmmovieapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedMoviesViewModel @Inject constructor(
    private val repository: MovieApiRepository,
    private val datastore: DataStoreUtil
): ViewModel() {

    sealed class SavedMoviesEvent {
        data class SavedMoviesError(val message: String): SavedMoviesEvent()
        object SavedMoviesLoading: SavedMoviesEvent()

        data class DeleteMovieResult(val message: String): SavedMoviesEvent()
    }

    private val _moviesFlow = MutableStateFlow<List<MovieItem>>(listOf())
    val moviesFlow: StateFlow<List<MovieItem>> = _moviesFlow

    private val _savedMoviesEvents = MutableSharedFlow<SavedMoviesEvent>()
    val savedMoviesEvent: SharedFlow<SavedMoviesEvent> = _savedMoviesEvents

    fun getFavoriteMovies() = viewModelScope.launch {
        _savedMoviesEvents.emit(SavedMoviesEvent.SavedMoviesLoading)
        when(val response = repository.getFavoriteMovies(datastore.getUsername())) {
            is Resource.Success -> {
                _moviesFlow.emit(response.data ?: listOf())
            }
            is Resource.Error -> {
                _savedMoviesEvents.emit(SavedMoviesEvent.SavedMoviesError(response.message ?: "Unknown error"))
            }
        }
    }

    fun deleteMovie(movie: MovieItem, curMovieList: List<MovieItem>) = viewModelScope.launch {
        _savedMoviesEvents.emit(SavedMoviesEvent.SavedMoviesLoading)
        var updatedMovieList = curMovieList.toMutableList()
        updatedMovieList.remove(movie)

        val favoriteMovieRequest = FavoriteMovieRequest(datastore.getUsername(), movie)
        when(val response = repository.deleteMovieFromFavorites(favoriteMovieRequest)) {
            is Resource.Success -> {
                _savedMoviesEvents.emit(SavedMoviesEvent.DeleteMovieResult(response.data ?: "Movie deleted successfully"))
                _moviesFlow.emit(updatedMovieList)
            }
            is Resource.Error -> {
                _savedMoviesEvents.emit(SavedMoviesEvent.SavedMoviesError(response.message ?: "Unknown error"))
            }
        }
    }
}
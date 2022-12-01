package com.example.mvvmmovieapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmmovieapp.apidata.trending.MovieItem
import com.example.mvvmmovieapp.repositories.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedMoviesViewModel @Inject constructor(val repository: Repository): ViewModel() {

    fun saveMovie(movie: MovieItem) = viewModelScope.launch {
        repository.upsert(movie)
    }

    fun deleteMovie(movie: MovieItem) = viewModelScope.launch {
        repository.deleteMovie(movie)
    }

    fun getSavedMovies() = repository.getSavedMovies()
}
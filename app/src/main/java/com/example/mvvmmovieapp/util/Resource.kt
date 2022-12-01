package com.example.mvvmmovieapp.util

import androidx.lifecycle.MutableLiveData

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T? = null): Resource<T>(data)
    class Error<T>(message: String? = null, ): Resource<T>(message = message)
    class Loading<T>: Resource<T>()
}
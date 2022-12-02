package com.example.mvvmmovieapp.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmmovieapp.apidata.requests.AccountRequest
import com.example.mvvmmovieapp.repositories.MovieApiRepository
import com.example.mvvmmovieapp.util.DataStoreUtil
import com.example.mvvmmovieapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterFragmentViewModel @Inject constructor(
    private val repository: MovieApiRepository
): ViewModel() {

    sealed class RegisterEvent {
        data class RegisterSuccess(val message: String): RegisterEvent()
        data class RegisterError(val message: String): RegisterEvent()
        object RegisterLoading: RegisterEvent()
    }

    private val _registerEvents = MutableSharedFlow<RegisterEvent>()
    val registerEvents: SharedFlow<RegisterEvent> = _registerEvents

    fun registerAccount(username: String, password: String, confirmPassword: String) = viewModelScope.launch {
        if(username.trim().isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _registerEvents.emit(RegisterEvent.RegisterError("No empty fields allowed"))
            return@launch
        }

        if(password != confirmPassword) {
            _registerEvents.emit(RegisterEvent.RegisterError("Passwords do not match"))
            return@launch
        }

        _registerEvents.emit(RegisterEvent.RegisterLoading)
        val accountRequest = AccountRequest(username, password)
        when(val response = repository.registerAccount(accountRequest)) {
            is Resource.Success -> {
                _registerEvents.emit(RegisterEvent.RegisterSuccess(response.data ?: "Account registered successfully"))
            }
            is Resource.Error -> {
                _registerEvents.emit(RegisterEvent.RegisterError(response.message ?: "Unknown error"))
            }
        }
    }
}
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
class LoginFragmentViewModel @Inject constructor(
    private val repository: MovieApiRepository,
    private val datastore: DataStoreUtil
): ViewModel() {

    sealed class LoginEvent {
        data class LoginSuccess(val message: String): LoginEvent()
        data class LoginError(val message: String): LoginEvent()
        object LoginLoading: LoginEvent()
    }

    private val _loginEvents = MutableSharedFlow<LoginEvent>()
    val loginEvents: SharedFlow<LoginEvent> = _loginEvents

    suspend fun isUserLoggedIn(): Boolean {
        return datastore.isUserLoggedIn()
    }

    fun loginUser(username: String, password: String) = viewModelScope.launch {
        if(username.trim().isEmpty() || password.isEmpty()) {
            _loginEvents.emit(LoginEvent.LoginError("No empty fields allowed"))
            return@launch
        }

        _loginEvents.emit(LoginEvent.LoginLoading)
        val accountRequest = AccountRequest(username, password)
        when(val response = repository.loginAccount(accountRequest)) {
            is Resource.Success -> {
                _loginEvents.emit(LoginEvent.LoginSuccess(response.data ?: "Logged in successfully"))
                datastore.storeCredentials(username, password)
            }
            is Resource.Error -> {
                _loginEvents.emit(LoginEvent.LoginError(response.message ?: "Unknown error"))
            }
        }
    }
}
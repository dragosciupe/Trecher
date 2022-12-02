package com.example.mvvmmovieapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.mvvmmovieapp.util.DataStoreUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(
    private val datastore: DataStoreUtil
) : ViewModel() {

    suspend fun logout() {
        datastore.logUserOut()
    }

    suspend fun getCurUser(): String {
        return datastore.getUsername()
    }
}
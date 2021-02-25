package com.example.firebasechat.ui.main

import androidx.lifecycle.*
import com.example.firebasechat.data.repository.AuthRepository
import com.example.firebasechat.data.sharedpref.UserSharedPreferenceLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userSharedPreferenceLiveData:
    UserSharedPreferenceLiveData,
    private val authRepo: AuthRepository
) : ViewModel() {
    fun getUserLiveData() = userSharedPreferenceLiveData

    @ExperimentalCoroutinesApi
    fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepo.signOut()
        }
    }
}
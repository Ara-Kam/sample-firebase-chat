package com.example.firebasechat.ui.authorization

import androidx.lifecycle.ViewModel
import com.example.firebasechat.data.sharedpref.UserSharedPreferenceLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    private val userSharedPreferenceLiveData:
    UserSharedPreferenceLiveData
) : ViewModel() {
    fun getUserLiveData() = userSharedPreferenceLiveData
}
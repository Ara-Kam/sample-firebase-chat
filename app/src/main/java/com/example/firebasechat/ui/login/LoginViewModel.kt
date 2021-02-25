package com.example.firebasechat.ui.login

import androidx.lifecycle.*
import com.example.firebasechat.data.repository.AuthRepository
import com.example.firebasechat.data.util.ResultDataWrapper
import com.example.firebasechat.util.isValidEmail
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@Suppress("CAST_NEVER_SUCCEEDS")
@HiltViewModel
class LoginViewModel @Inject constructor(val authRepo: AuthRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is login Fragment"
    }
    val text: LiveData<String> = _text

    var mEmail: String = ""
        set(value) {
            field = value
            _enableLoginButton.value = isLoginButtonEnabled()
        }
    var mPassword: String = ""
        set(value) {
            field = value
            _enableLoginButton.value = isLoginButtonEnabled()
        }

    private lateinit var loginResult: LiveData<ResultDataWrapper<FirebaseUser?>>
    private var _fireBaseUser = MutableLiveData<ResultDataWrapper<FirebaseUser?>>()

    @ExperimentalCoroutinesApi
    fun signInWithEmailPass() {
        loginResult = authRepo.signInWithEmailAndPassword(mEmail, mPassword)
        loginResult.observeForever { _fireBaseUser.value = it }
    }

    val fireBaseUser: LiveData<ResultDataWrapper<FirebaseUser?>>
        get() = _fireBaseUser

    private fun isLoginButtonEnabled(): Boolean {
        return mEmail.isNotBlank() &&
                isValidEmail(mEmail) &&
                mPassword.isNotBlank() &&
                mPassword.length >= 6
    }

    private var _enableLoginButton = MutableLiveData<Boolean>()
    val enableLoginButton: LiveData<Boolean> = _enableLoginButton

    override fun onCleared() {
        if (this::loginResult.isInitialized) {
            loginResult.removeObserver { _fireBaseUser }
        }
        super.onCleared()
    }
}
package com.example.firebasechat.ui.registration

import androidx.lifecycle.*
import com.example.firebasechat.data.repository.AuthRepository
import com.example.firebasechat.data.util.ResultDataWrapper
import com.example.firebasechat.util.isValidEmail
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


@HiltViewModel
class RegistrationViewModel @Inject constructor(val authRepo: AuthRepository) : ViewModel() {

    // Update enableRegButton at every input value change
    var mEmail: String = ""
        set(value) {
            field = value
            _enableRegButton.value = isRegButtonEnabled()
        }
    var mPassword: String = ""
        set(value) {
            field = value
            _enableRegButton.value = isRegButtonEnabled()
        }
    var mConfirmedPassword: String = ""
        set(value) {
            field = value
            _enableRegButton.value = isRegButtonEnabled()
        }

    private lateinit var signUpResult: LiveData<ResultDataWrapper<FirebaseUser?>>
    private var _fireBaseUser = MutableLiveData<ResultDataWrapper<FirebaseUser?>>()

    @ExperimentalCoroutinesApi
    fun signUpWithEmailPass() {
        signUpResult = authRepo.signUpWithEmailAndPassword(mEmail, mPassword)
        signUpResult.observeForever { _fireBaseUser.value = it }
    }

    val fireBaseUser: LiveData<ResultDataWrapper<FirebaseUser?>>
        get() = _fireBaseUser

    private fun isRegButtonEnabled(): Boolean {
        return mEmail.isNotBlank() &&
                isValidEmail(mEmail) &&
                mPassword.isNotBlank() &&
                mPassword.length >= 6 &&
                mPassword == mConfirmedPassword
    }

    private var _enableRegButton = MutableLiveData<Boolean>()
    val enableRegButton: LiveData<Boolean> = _enableRegButton

    override fun onCleared() {
        if (this::signUpResult.isInitialized) {
            signUpResult.removeObserver { _fireBaseUser }
        }
        super.onCleared()
    }
}
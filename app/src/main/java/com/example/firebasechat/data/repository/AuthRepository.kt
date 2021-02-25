package com.example.firebasechat.data.repository

import com.example.firebasechat.data.sharedpref.UserSharedPreferenceLiveData
import com.example.firebasechat.data.util.AuthStrategy
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class
AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userSharedPreferenceLiveData:
    UserSharedPreferenceLiveData
) {
    @ExperimentalCoroutinesApi
    fun signInWithEmailAndPassword(
        email: String,
        password: String
    ) = AuthStrategy.signInWithEmailAndPassword(
        email,
        password,
        firebaseAuth = firebaseAuth,
        userSharedPreferenceLiveData
    )

    @ExperimentalCoroutinesApi
    fun signUpWithEmailAndPassword(
        email: String,
        password: String
    ) = AuthStrategy.signUpWithEmailAndPassword(
        email,
        password,
        firebaseAuth = firebaseAuth,
        userSharedPreferenceLiveData
    )

    @ExperimentalCoroutinesApi
    fun signOut() = AuthStrategy.signOut(
        firebaseAuth = firebaseAuth,
        userSharedPreferenceLiveData
    )
}
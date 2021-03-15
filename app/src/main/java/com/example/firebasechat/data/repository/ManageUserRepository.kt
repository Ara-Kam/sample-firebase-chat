package com.example.firebasechat.data.repository

import com.example.firebasechat.data.entity.User
import com.example.firebasechat.data.sharedpref.UserSharedPreferenceLiveData
import com.example.firebasechat.data.util.ManageUserStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class ManageUserRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    private val userSharedPreferenceLiveData:
    UserSharedPreferenceLiveData
) {
    @ExperimentalCoroutinesApi
    fun updateUserField(user: User, editUserField: User.Field) = ManageUserStrategy.updateUserField(
        user, editUserField,
        firebaseAuth,
        firebaseStorage,
        userSharedPreferenceLiveData
    )
}
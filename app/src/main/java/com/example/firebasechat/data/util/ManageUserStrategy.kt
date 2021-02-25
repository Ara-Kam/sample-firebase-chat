package com.example.firebasechat.data.util

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.firebasechat.data.entity.User
import com.example.firebasechat.data.sharedpref.UserSharedPreferenceLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ManageUserStrategy {

    companion object {
        @ExperimentalCoroutinesApi
        fun updateUserField(
            user: User, editUserField: User.Field,
            firebaseAuth: FirebaseAuth,
            firebaseStorage: FirebaseStorage,
            userSharedPreferenceLiveData: UserSharedPreferenceLiveData
        ): LiveData<ResultDataWrapper<Void?>> = liveData(Dispatchers.IO) {
            try {
                emit(ResultDataWrapper.loading())

                val result =
                    when (editUserField) {
                        User.Field.PASSWORD -> firebaseAuth.currentUser?.updatePassword(user.password!!)
                            ?.await()
                        User.Field.NAME -> {
                            val userProfileNameChangeRequest = UserProfileChangeRequest.Builder()
                                .setDisplayName(user.name).build()
                            firebaseAuth.currentUser?.updateProfile(userProfileNameChangeRequest)
                                ?.await()
                        }
                        User.Field.AVATAR_GALLERY -> {
                            val storageReference: StorageReference = firebaseStorage.reference
                            // Create a reference to "avatar.jpg"
                            val avatarReference = storageReference.child("avatar.jpg")

                            avatarReference.putFile(Uri.parse(user.avatar)).await()
                        }
                        else -> {
                            val userProfileAvatarChangeRequest = UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(user.avatar)).build()
                            firebaseAuth.currentUser?.updateProfile(userProfileAvatarChangeRequest)
                                ?.await()
                        }
                    }

                when (result?.status) {
                    ResultDataWrapper.Status.SUCCESS -> {
                        userSharedPreferenceLiveData.setUser(user)
                        emit(ResultDataWrapper.success(null))
                    }
                    ResultDataWrapper.Status.ERROR -> {
                        emit(
                            ResultDataWrapper.error(
                                result.message ?: "Task was failed.", null
                            )
                        )
                    }
                    ResultDataWrapper.Status.CANCELED -> {
                        emit(
                            ResultDataWrapper.canceled(
                                result.message ?: "Task was cancelled normally.",
                                null
                            )
                        )
                    }
                    else -> emit(
                        ResultDataWrapper.error(
                            result?.message ?: "Task was failed.", null
                        )
                    )
                }
            } catch (exception: Exception) {
                emit(
                    ResultDataWrapper.error(
                        exception.localizedMessage ?: "Task was failed with exception.", null
                    )
                )
            }
        }
    }
}
package com.example.firebasechat.data.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.firebasechat.data.entity.User
import com.example.firebasechat.data.sharedpref.UserSharedPreferenceLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

class AuthStrategy {

    @ExperimentalCoroutinesApi
    companion object {
        fun signInWithEmailAndPassword(
            email: String,
            password: String,
            firebaseAuth: FirebaseAuth,
            userSharedPreferenceLiveData: UserSharedPreferenceLiveData
        ): LiveData<ResultDataWrapper<FirebaseUser?>> = liveData(Dispatchers.IO) {
            try {
                emit(ResultDataWrapper.loading())

                val resultAuth =
                    firebaseAuth.signInWithEmailAndPassword(email, password).await()

                when (resultAuth.status) {
                    ResultDataWrapper.Status.SUCCESS -> {
                        val firebaseUser = resultAuth.data?.user
                        // Save user data in sharedprefs
                        userSharedPreferenceLiveData.setUser(
                            User(
                                firebaseUser?.displayName,
                                firebaseUser?.email,
                                password,
                                firebaseUser?.photoUrl?.toString(),
                                firebaseUser?.uid
                            )
                        )
                        emit(ResultDataWrapper.success(firebaseUser))
                    }
                    ResultDataWrapper.Status.ERROR -> {
                        emit(
                            ResultDataWrapper.error(
                                resultAuth.message ?: "Task was failed.", null
                            )
                        )
                    }
                    ResultDataWrapper.Status.CANCELED -> {
                        emit(
                            ResultDataWrapper.canceled(
                                resultAuth.message ?: "Task was cancelled normally.",
                                null
                            )
                        )
                    }
                    else -> emit(
                        ResultDataWrapper.error(
                            resultAuth.message ?: "Task was failed.", null
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

        fun signUpWithEmailAndPassword(
            email: String,
            password: String,
            firebaseAuth: FirebaseAuth,
            userSharedPreferenceLiveData: UserSharedPreferenceLiveData
        ): LiveData<ResultDataWrapper<FirebaseUser?>> = liveData(Dispatchers.IO) {
            try {
                emit(ResultDataWrapper.loading())

                val resultAuth =
                    firebaseAuth.createUserWithEmailAndPassword(email, password).await()

                when (resultAuth.status) {
                    ResultDataWrapper.Status.SUCCESS -> {
                        val firebaseUser =
                            resultAuth.data?.user
                        // Save user data in sharedprefs
                        userSharedPreferenceLiveData.setUser(
                            User(
                                firebaseUser?.displayName,
                                firebaseUser?.email,
                                password,
                                firebaseUser?.photoUrl?.toString(),
                                firebaseUser?.uid
                            )
                        )
                        emit(ResultDataWrapper.success(firebaseUser))
                    }
                    ResultDataWrapper.Status.ERROR -> {
                        emit(
                            ResultDataWrapper.error(
                                resultAuth.message ?: "Task was failed.", null
                            )
                        )
                    }
                    ResultDataWrapper.Status.CANCELED -> {
                        emit(
                            ResultDataWrapper.canceled(
                                resultAuth.message ?: "Task was cancelled normally.",
                                null
                            )
                        )
                    }
                    else -> emit(
                        ResultDataWrapper.error(
                            resultAuth.message ?: "Task was failed.", null
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

        fun signOut(
            firebaseAuth: FirebaseAuth,
            userSharedPreferenceLiveData: UserSharedPreferenceLiveData
        ) {
            if (firebaseAuth.currentUser != null)
                firebaseAuth.signOut()

            // Clear user data from sharedprefs
            userSharedPreferenceLiveData.setUser(User())
        }
    }
}
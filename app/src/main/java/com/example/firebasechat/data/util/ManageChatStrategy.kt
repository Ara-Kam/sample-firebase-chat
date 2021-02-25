package com.example.firebasechat.data.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.firebasechat.data.entity.ChatMessage
import com.example.firebasechat.data.entity.User
import com.example.firebasechat.data.sharedpref.UserSharedPreferenceLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ManageChatStrategy {

    companion object {
        @ExperimentalCoroutinesApi
        fun sendMessage(
            message: ChatMessage?,
            firebaseDB: FirebaseDatabase
        ): LiveData<ResultDataWrapper<Void?>> = liveData(Dispatchers.IO) {
            try {
                emit(ResultDataWrapper.loading())

                // send message
                val result = firebaseDB.reference.push().setValue(message).await()
                when (result.status) {
                    ResultDataWrapper.Status.SUCCESS -> {
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
                            result.message ?: "Task was failed.", null
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
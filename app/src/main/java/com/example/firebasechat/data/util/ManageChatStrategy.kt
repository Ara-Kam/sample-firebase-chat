package com.example.firebasechat.data.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.firebasechat.data.entity.ChatMessage
import com.example.firebasechat.util.tempUserNameFromUid
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ManageChatStrategy {

    companion object {
        @ExperimentalCoroutinesApi
        fun sendMessage(
            message: ChatMessage?,
            firebaseFirestore: FirebaseFirestore
        ): LiveData<ResultDataWrapper<Void?>> = liveData(Dispatchers.IO) {
            try {
                emit(ResultDataWrapper.loading())

                // Send message:: save message in a new document
                val result = message?.let {
                    firebaseFirestore.collection("messages")
                        .document(tempUserNameFromUid(message.uid) + "_" + message.date)
                        .set(it).await()
                }

                when (result?.status) {
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
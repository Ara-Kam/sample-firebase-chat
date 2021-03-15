package com.example.firebasechat.data.repository

import com.example.firebasechat.data.entity.ChatMessage
import com.example.firebasechat.data.util.ManageChatStrategy
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
){
    @ExperimentalCoroutinesApi
    fun sendChat(
        message: ChatMessage?
    ) = ManageChatStrategy.sendMessage(
        message,
        firebaseFirestore
    )
}
package com.example.firebasechat.data.repository

import com.example.firebasechat.data.entity.ChatMessage
import com.example.firebasechat.data.sharedpref.UserSharedPreferenceLiveData
import com.example.firebasechat.data.util.AuthStrategy
import com.example.firebasechat.data.util.ManageChatStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val firebaseDB: FirebaseDatabase
){
    @ExperimentalCoroutinesApi
    fun sendChat(
        message: ChatMessage?
    ) = ManageChatStrategy.sendMessage(
        message,
        firebaseDB
    )
}
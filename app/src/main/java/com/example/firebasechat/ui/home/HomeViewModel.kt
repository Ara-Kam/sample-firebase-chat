package com.example.firebasechat.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebasechat.data.entity.ChatMessage
import com.example.firebasechat.data.repository.ChatRepository
import com.example.firebasechat.data.util.ResultDataWrapper
import com.example.firebasechat.util.tempUserNameFromUid
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val chatRepository: ChatRepository
) : ViewModel() {

    var messageText: String = ""
        set(value) {
            field = value
            _enableSendButton.value = isSendButtonEnabled()
        }

    private fun isSendButtonEnabled(): Boolean {
        return messageText.isNotBlank()
    }

    private var _enableSendButton = MutableLiveData<Boolean>()
    val enableSendButton: LiveData<Boolean> = _enableSendButton

    lateinit var result: LiveData<ResultDataWrapper<Void?>>

    @ExperimentalCoroutinesApi
    fun sendMessage() {
        // If displayName is empty send generated string from uid
        val message = firebaseAuth.currentUser?.let {
            val dspName = firebaseAuth.currentUser?.displayName
            val displayName: String = if (dspName == null || dspName.isBlank()) {
                tempUserNameFromUid(firebaseAuth.currentUser?.uid!!)
            } else {
                dspName
            }

            ChatMessage(
                it.uid,
                displayName,
                messageText,
                System.currentTimeMillis().toString()

            )
        }

        result = chatRepository.sendChat(message)
        result.observeForever { _sendSuccessfully.value = it }
    }

    private var _sendSuccessfully = MutableLiveData<ResultDataWrapper<Void?>>()
    val sendSuccessfully: LiveData<ResultDataWrapper<Void?>> = _sendSuccessfully

    override fun onCleared() {
        if (this::result.isInitialized) {
            result.removeObserver { _sendSuccessfully }
        }
        super.onCleared()
    }
}
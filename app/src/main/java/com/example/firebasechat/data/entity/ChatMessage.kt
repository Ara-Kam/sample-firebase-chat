package com.example.firebasechat.data.entity

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("uid") var uid: String = "000",
    @SerializedName("sender_name") var sender_name: String? = null,
    @SerializedName("text") var text: String = "",
    @SerializedName("date") var date: String? = null
)

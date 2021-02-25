package com.example.firebasechat.util

import android.text.TextUtils
import android.util.Patterns
import com.example.firebasechat.data.entity.User
import com.google.gson.Gson

fun isValidEmail(target: CharSequence?): Boolean =
    !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!).matches()

fun userToJson(user: User): String {
    val gson = Gson()
    return gson.toJson(user)
}

fun jsonToUser(json: String): User {
    val gson = Gson()
    return gson.fromJson(json, User::class.java)
}

fun tempUserNameFromUid(uid: String): String {
    return uid.take(2) + "**" + uid.takeLast(2)
}
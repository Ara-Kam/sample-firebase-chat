package com.example.firebasechat.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun timeStampDateTimeConverter(stringTimeStamp: String): String? {
    return try {
        val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm")
        val netDate = Date(stringTimeStamp.toLong())
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}
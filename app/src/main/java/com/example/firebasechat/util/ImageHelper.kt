package com.example.firebasechat.util

import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.example.firebasechat.BuildConfig

const val IMAGE_FRAME_SIZE = 100

fun getRotationAngle(uriString: String?, context: Context): Int {
    var degree = 0

    if (uriString == null || uriString.isBlank())
        return degree

    try {
        val inputStream = context.contentResolver.openInputStream(Uri.parse(uriString))
            ?: return degree

        val exifInterface = ExifInterface(inputStream)
        val orientation: Int = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        degree = when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> 0
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            ExifInterface.ORIENTATION_UNDEFINED -> 0
            else -> 90
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
    return degree
}

fun getUriForResource(resourceId: Int): String {
    return Uri.parse(
        "android.resource://${BuildConfig.APPLICATION_ID}/$resourceId"
    ).toString()
}
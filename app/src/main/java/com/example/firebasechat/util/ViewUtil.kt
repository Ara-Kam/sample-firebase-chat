package com.example.firebasechat.util

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager

fun closeKeyboard(context: Activity) {
    val view = context.currentFocus
    if (view != null) {
        val manager = context.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager?
        manager?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun pixels(context: Context, dip: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dip,
        context.resources.displayMetrics
    ).toInt()
}

fun dips(context: Context, pixel: Int): Int {
    return (pixel / context.resources.displayMetrics.density).toInt()
}
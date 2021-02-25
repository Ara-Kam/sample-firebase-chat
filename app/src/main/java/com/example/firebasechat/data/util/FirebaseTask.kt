package com.example.firebasechat.data.util

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

@ExperimentalCoroutinesApi
suspend fun <T> Task<T>.await(): ResultDataWrapper<T> {
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) {
                ResultDataWrapper.canceled("Task $this was cancelled normally.")
            } else {
                @Suppress("UNCHECKED_CAST")
                ResultDataWrapper.success(result as T)
            }
        } else {
            ResultDataWrapper.error(e.localizedMessage ?: "Task was failed.")
        }
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            val e = exception
            if (e == null) {
                @Suppress("UNCHECKED_CAST")
                if (isCanceled) {
                    cont.cancel()
                } else {
                    cont.resume(ResultDataWrapper.success(result as T), null)
                }
            } else {
                cont.resumeWithException(e)
            }
        }
    }
}

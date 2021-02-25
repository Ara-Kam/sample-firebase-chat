package com.example.firebasechat.data.util

data class ResultDataWrapper<out T>(val status: Status, val data: T?, val message: String?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,
        CANCELED
    }

    companion object {
        fun <T> success(data: T): ResultDataWrapper<T> {
            return ResultDataWrapper(Status.SUCCESS, data, null)
        }

        fun <T> error(message: String, data: T? = null): ResultDataWrapper<T> {
            return ResultDataWrapper(Status.ERROR, data, message)
        }

        fun <T> loading(data: T? = null): ResultDataWrapper<T> {
            return ResultDataWrapper(Status.LOADING, data, null)
        }

        fun <T> canceled(message: String, data: T? = null): ResultDataWrapper<T> {
            return ResultDataWrapper(Status.CANCELED, data, message)
        }
    }
}
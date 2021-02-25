package com.example.firebasechat.data.sharedpref

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.example.firebasechat.data.entity.User
import com.example.firebasechat.util.jsonToUser
import com.example.firebasechat.util.userToJson

class UserSharedPreferenceLiveData constructor(context: Context) : LiveData<User>() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val USER_KEY = "user_key"
        private const val PREFS_FILE_NAME = "prefs_file_name"

    }

    override fun onActive() {
        super.onActive()
        val prefs = sharedPreferences.getString(USER_KEY, null)
        value = jsonToUser(prefs ?: "{}")
        sharedPreferences.registerOnSharedPreferenceChangeListener(mUserSharedPreferenceListener)
    }

    override fun onInactive() {
        super.onInactive()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(mUserSharedPreferenceListener)
    }

    private val mUserSharedPreferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences?, key: String? ->
            if (key == USER_KEY) {
                value = jsonToUser(sharedPreferences?.getString(USER_KEY, null).toString())
            }
        }

    fun getUser(): User = jsonToUser(sharedPreferences.getString(USER_KEY, null).toString())
    fun setUser(user: User) = sharedPreferences.edit().putString(USER_KEY, userToJson(user)).apply()
}
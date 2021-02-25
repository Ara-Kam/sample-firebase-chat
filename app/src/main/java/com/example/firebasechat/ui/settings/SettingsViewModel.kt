package com.example.firebasechat.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firebasechat.data.entity.User
import com.example.firebasechat.data.repository.ManageUserRepository
import com.example.firebasechat.data.sharedpref.UserSharedPreferenceLiveData
import com.example.firebasechat.data.util.ResultDataWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSharedPreferenceLiveData:
    UserSharedPreferenceLiveData,
    private val manageUserRepository: ManageUserRepository,
    private val appContext: Application
) : AndroidViewModel(appContext) {
    fun getUserLiveData() = userSharedPreferenceLiveData

    private var _updateResult = MutableLiveData<ResultDataWrapper<Void?>>()
    val updateResult: LiveData<ResultDataWrapper<Void?>>
        get() = _updateResult

    lateinit var resultOfUpdate: LiveData<ResultDataWrapper<Void?>>

    @ExperimentalCoroutinesApi
    fun updateUserField(user: User, editUserField: User.Field) {
        resultOfUpdate = manageUserRepository.updateUserField(user, editUserField)
        resultOfUpdate.observeForever { _updateResult.value = it }
    }

    override fun onCleared() {
        if (this::resultOfUpdate.isInitialized) {
            resultOfUpdate.removeObserver { _updateResult }
        }
        super.onCleared()
    }
}
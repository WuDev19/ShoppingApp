package com.example.shoppingapp.ui.viewmodel.common

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.domain.usecase.profileusecase.ConfirmChangeEmailUseCase
import com.example.shoppingapp.domain.usecase.profileusecase.LogoutUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val confirmChangeEmailUseCase: ConfirmChangeEmailUseCase,
    private val logoutUseCase: LogoutUseCase,
    val auth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences,
    ) : ViewModel() {

    private val _updateEmailState: MutableStateFlow<StateListener<Unit>> =
        MutableStateFlow(StateListener.Idle)
    val updateEmailState = _updateEmailState.asStateFlow()

    private val _logoutState: MutableStateFlow<StateListener<Boolean>> =
        MutableStateFlow(StateListener.Idle)
    val logoutState = _logoutState.asStateFlow()
    fun confirmUpdateEmail() {
        viewModelScope.launch {
            try {
                auth.currentUser?.reload()?.await()
                confirmChangeEmailUseCase(
                    auth.currentUser?.uid,
                    auth.currentUser?.email
                ).collectLatest {
                    _updateEmailState.value = it
                }
            } catch (e: Exception) {
                _updateEmailState.value = StateListener.Error(e)
            }

        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase().collectLatest {
                _logoutState.value = it
            }
        }
    }

    fun writeStateEmailVerify() {
        sharedPreferences.edit().apply {
            putBoolean(StringConstants.SHARE_VERIFY_EMAIL, true)
            apply()
        }
    }

    fun getStateEmailVerify(): Boolean =
        sharedPreferences.getBoolean(StringConstants.SHARE_VERIFY_EMAIL, true)


}
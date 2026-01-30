package com.example.shoppingapp.ui.viewmodel.auth

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.domain.usecase.authusecase.LoginEmailUseCase
import com.example.shoppingapp.domain.usecase.authusecase.LoginGoogleUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.content.edit

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginEmailUseCase: LoginEmailUseCase,
    private val loginGoogleUseCase: LoginGoogleUseCase,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _state = MutableStateFlow<StateListener<AuthResponse>>(StateListener.Idle)
    val state: StateFlow<StateListener<AuthResponse>> = _state

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            loginEmailUseCase(email, password).collect {
                _state.value = it
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            loginGoogleUseCase(idToken).collect {
                _state.value = it
            }
        }
    }

    fun saveTokenToLocal(authResponse: AuthResponse){
        sharedPreferences.edit {
            putString(StringConstants.TOKEN, authResponse.token)
            putString(StringConstants.REFRESH_TOKEN, authResponse.refreshToken)
            putString(StringConstants.TIME_CREATE_TOKEN, authResponse.createTime)
        }
    }

}
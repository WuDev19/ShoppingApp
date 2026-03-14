package com.example.shoppingapp.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.utils.TokenManager
import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.domain.usecase.auth_usecase.LoginEmailUseCase
import com.example.shoppingapp.domain.usecase.auth_usecase.LoginGoogleUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginEmailUseCase: LoginEmailUseCase,
    private val loginGoogleUseCase: LoginGoogleUseCase,
    private val tokenManager: TokenManager
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

    fun saveTokenToLocal(authResponse: AuthResponse) {
        authResponse.sellerId?.let {
            tokenManager.setSeller(it)
        }
        tokenManager.writeAll(authResponse)
    }

}
package com.example.shoppingapp.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.usecase.authusecase.LoginEmailUseCase
import com.example.shoppingapp.domain.usecase.authusecase.LoginGoogleUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginEmailUseCase: LoginEmailUseCase,
    private val loginGoogleUseCase: LoginGoogleUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<StateListener<AuthResult>>(StateListener.Idle)
    val state: StateFlow<StateListener<AuthResult>> = _state

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            loginEmailUseCase(email, password).collect {
                _state.value = it
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            loginGoogleUseCase(idToken).collect{
                _state.value = it
            }
        }
    }

}
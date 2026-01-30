package com.example.shoppingapp.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.domain.model.LoginModel
import com.example.shoppingapp.domain.usecase.authusecase.CreateAccountUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(private val createAccountUseCase: CreateAccountUseCase) :
    ViewModel() {

    private val _state = MutableStateFlow<StateListener<LoginModel>>(StateListener.Idle)
    val state: StateFlow<StateListener<LoginModel>> = _state

    fun createAccount(
        email: String,
        password: String,
        phone: String,
        username: String
    ) {
        viewModelScope.launch {
            createAccountUseCase(email, password, phone, username).collect{
                _state.value = it
            }
        }
    }

}
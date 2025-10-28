package com.example.shoppingapp.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.usecase.authusecase.ResetPasswordUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(private val useCase: ResetPasswordUseCase) : ViewModel() {

    private val _state = MutableStateFlow<StateListener<Void>>(StateListener.Idle)
    val state: StateFlow<StateListener<Void>> = _state

    fun resetPassword(email: String){
        viewModelScope.launch {
            useCase(email).collect{
                _state.value = it
            }
        }
    }

}
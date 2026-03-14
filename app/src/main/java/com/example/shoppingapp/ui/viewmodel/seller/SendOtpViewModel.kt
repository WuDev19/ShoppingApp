package com.example.shoppingapp.ui.viewmodel.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.OtpModel
import com.example.shoppingapp.domain.usecase.seller_usecase.SendOtpUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendOtpViewModel @Inject constructor(private val sendOtpUseCase: SendOtpUseCase) :
    ViewModel() {

    private val _otpState: MutableStateFlow<StateListener<OtpModel>> =
        MutableStateFlow(StateListener.Idle)
    val otpState = _otpState.asStateFlow()

    fun sendOtp() {
        viewModelScope.launch {
            sendOtpUseCase().collectLatest {
                _otpState.value = it
            }
        }
    }

    fun setSate() {
        _otpState.value = StateListener.Idle
    }

}
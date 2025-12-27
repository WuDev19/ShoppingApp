package com.example.shoppingapp.ui.viewmodel.edit_profile

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.remote.dto.request.UserDTOUpdate
import com.example.shoppingapp.domain.usecase.profileusecase.ChangeEmailUseCase
import com.example.shoppingapp.domain.usecase.profileusecase.ChangePhoneUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeEmailAndPhoneViewModel @Inject constructor(
    private val changePhoneUseCase: ChangePhoneUseCase,
    private val changeEmailUseCase: ChangeEmailUseCase,
    private val sharedPreferences: SharedPreferences,
    val auth: FirebaseAuth
) : ViewModel() {

    private val _changePhoneState: MutableStateFlow<StateListener<Unit>> = MutableStateFlow(StateListener.Idle)
    val changePhoneState = _changePhoneState.asStateFlow()

    private val _changeEmailState: MutableStateFlow<StateListener<Unit>> = MutableStateFlow(StateListener.Idle)
    val changeEmailState = _changeEmailState.asStateFlow()

    fun changePhone(userDTOUpdate: UserDTOUpdate){
        viewModelScope.launch {
            try {
                changePhoneUseCase(userDTOUpdate).collectLatest {
                    _changePhoneState.value = it
                }
            }
            catch (e: IllegalArgumentException) {
                _changePhoneState.value = StateListener.Error(e)
            }
        }
    }

    fun changeEmail(userDTOUpdate: UserDTOUpdate) {
        viewModelScope.launch {
            try {
                changeEmailUseCase(userDTOUpdate).collectLatest {
                    _changeEmailState.value = it
                }
            }
            catch (e: IllegalArgumentException){
                _changeEmailState.value = StateListener.Error(e)
            }
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun checkIsGoogleAccount(): Boolean{
        //kiem tra xem hien tai dang dang nhap bang phuong thuc gi (google, email/password, ...)
        return auth.currentUser?.providerData?.any {
            it.providerId == StringConstants.GOOGLE_AUTH_ID
        } ?: false
    }

    fun writeStateEmailVerify(){
        sharedPreferences.edit().apply {
            putBoolean(StringConstants.SHARE_VERIFY_EMAIL, false)
            apply()
        }
    }

}
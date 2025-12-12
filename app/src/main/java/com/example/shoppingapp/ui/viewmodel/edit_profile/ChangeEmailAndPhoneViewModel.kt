package com.example.shoppingapp.ui.viewmodel.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.remote.dto.post.UserDTOUpdate
import com.example.shoppingapp.domain.usecase.profileusecase.ChangeEmailPhoneUseCase
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
    private val changeEmailPhoneUseCase: ChangeEmailPhoneUseCase,
    val auth: FirebaseAuth
) : ViewModel() {

    private val _changeEmailPhoneState: MutableStateFlow<StateListener<Unit>> = MutableStateFlow(StateListener.Idle)
    val changeEmailPhoneState = _changeEmailPhoneState.asStateFlow()

    fun changeEmailPhone(userDTOUpdate: UserDTOUpdate){
        viewModelScope.launch {
            changeEmailPhoneUseCase(userDTOUpdate).collectLatest {
                _changeEmailPhoneState.value = it
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

}
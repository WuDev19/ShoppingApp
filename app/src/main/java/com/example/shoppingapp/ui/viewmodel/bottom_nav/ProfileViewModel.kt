package com.example.shoppingapp.ui.viewmodel.bottom_nav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.user.BaseUser
import com.example.shoppingapp.domain.usecase.user_usecase.profile_usecase.GetUserInfoUseCase
import com.example.shoppingapp.domain.usecase.user_usecase.profile_usecase.LogoutUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _userProfile: MutableStateFlow<StateListener<BaseUser>> = MutableStateFlow(StateListener.Idle)
    val userProfile = _userProfile.asStateFlow()

    private val _logoutState: MutableStateFlow<StateListener<Boolean>> = MutableStateFlow(StateListener.Idle)
    val logoutState = _logoutState.asStateFlow()

    fun getCurrentUserId(): String {
        return auth.currentUser!!.uid
    }

    fun getUserProfile(){
        viewModelScope.launch {
            getUserInfoUseCase().collectLatest {
                _userProfile.value = it
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
            logoutUseCase().collectLatest {
                _logoutState.value = it
            }
        }
    }

}
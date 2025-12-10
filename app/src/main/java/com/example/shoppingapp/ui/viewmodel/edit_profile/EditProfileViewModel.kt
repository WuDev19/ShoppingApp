package com.example.shoppingapp.ui.viewmodel.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.user.UserProfile
import com.example.shoppingapp.domain.usecase.profileusecase.GetUserProfileUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
) : ViewModel() {

    private val _profileUserState: MutableStateFlow<StateListener<UserProfile>> =
        MutableStateFlow(StateListener.Idle)
    val profileUserState = _profileUserState.asStateFlow()

    fun getProfileUser() {
        viewModelScope.launch {
            getUserProfileUseCase().collectLatest {
                _profileUserState.value = it
            }
        }
    }

}
package com.example.shoppingapp.ui.viewmodel.user.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.remote.dto.request.UserDTOUpdate
import com.example.shoppingapp.domain.usecase.user_usecase.profile_usecase.ChangeNameGenderUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeNameAndGenderViewModel @Inject constructor(
    private val changeNameGenderUseCase: ChangeNameGenderUseCase,
    private val auth: FirebaseAuth
) :
    ViewModel() {

    private val _changeNameState: MutableStateFlow<StateListener<Unit>> =
        MutableStateFlow(StateListener.Idle)
    val changeNameState = _changeNameState.asStateFlow()

    fun updateName(userDTOUpdate: UserDTOUpdate) {
        viewModelScope.launch {
            try {
                changeNameGenderUseCase(userDTOUpdate).collectLatest {
                    _changeNameState.value = it
                }
            }
            catch (e: IllegalArgumentException){
                _changeNameState.value = StateListener.Error(e)
            }
        }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

}
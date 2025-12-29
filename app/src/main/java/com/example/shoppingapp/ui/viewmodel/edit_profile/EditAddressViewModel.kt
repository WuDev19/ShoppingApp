package com.example.shoppingapp.ui.viewmodel.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.remote.dto.request.UserAddressRequest
import com.example.shoppingapp.domain.usecase.profileusecase.AddUserAddressUseCase
import com.example.shoppingapp.domain.usecase.profileusecase.EditUserAddressUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAddressViewModel @Inject constructor(
    private val editUserAddressUseCase: EditUserAddressUseCase,
    private val addUserAddressUseCase: AddUserAddressUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _addressState: MutableStateFlow<StateListener<Unit>> =
        MutableStateFlow(StateListener.Idle)
    val addressState = _addressState.asStateFlow()

    fun updateAddress(idAddress: Int, userAddressRequest: UserAddressRequest) {
        viewModelScope.launch {
            editUserAddressUseCase(idAddress, userAddressRequest).collectLatest {
                _addressState.value = it
            }
        }
    }

    fun addAddress(userAddressRequest: UserAddressRequest){
        try {
            viewModelScope.launch {
                addUserAddressUseCase(auth.currentUser?.uid ?: "", userAddressRequest).collectLatest {
                    _addressState.value = it
                }
            }
        }
        catch (e: IllegalArgumentException){
            _addressState.value = StateListener.Error(IllegalArgumentException(e.message))
        }
    }

}
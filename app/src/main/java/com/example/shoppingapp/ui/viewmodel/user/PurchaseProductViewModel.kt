package com.example.shoppingapp.ui.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.usecase.profileusecase.GetUserAddressUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PurchaseProductViewModel @Inject constructor(
    private val getUserAddressUseCase: GetUserAddressUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    val getAddress = getUserAddressUseCase(auth.currentUser?.uid ?: "").stateIn(
        viewModelScope,
        SharingStarted.Lazily, emptyList()
    )

}
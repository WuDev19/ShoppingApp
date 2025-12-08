package com.example.shoppingapp.ui.viewmodel.bottom_nav

import androidx.lifecycle.ViewModel
import com.example.shoppingapp.domain.usecase.profileusecase.GetUserInfoUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val getUserInfoUseCase: GetUserInfoUseCase, private val auth: FirebaseAuth): ViewModel() {

    fun getCurrentUserId(): String{
        return auth.currentUser!!.uid
    }

}
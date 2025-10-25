package com.example.shoppingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.shoppingapp.domain.model.AuthUser
import com.example.shoppingapp.domain.usecase.CheckLoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val checkLoginUseCase : CheckLoginUseCase) : ViewModel() {

    private val stateFlow = MutableStateFlow(AuthUser(""))
    val uiState: StateFlow<AuthUser> = stateFlow
    suspend fun checkLogin(){
        var user = checkLoginUseCase()
        if(user == null){
            user = AuthUser("")
        }
        stateFlow.value = user
    }

}
package com.example.shoppingapp.ui.viewmodel.seller

import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.data.remote.dto.request.SellerRequest
import com.example.shoppingapp.domain.usecase.profileusecase.UploadAvatarUseCase
import com.example.shoppingapp.domain.usecase.sellerusecase.RegisterSellerUseCase
import com.example.shoppingapp.domain.usecase.sellerusecase.VerifyOtpUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.content.edit
import com.example.shoppingapp.constants.TokenManager

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val registerSellerUseCase: RegisterSellerUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _registerState: MutableStateFlow<StateListener<AuthResponse>> =
        MutableStateFlow(StateListener.Idle)
    val registerState = _registerState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun register(otp: String, uri: Uri, sellerRequest: SellerRequest){
        viewModelScope.launch {
            verifyOtpUseCase(otp).flatMapConcat {
                when(it){
                    is StateListener.Success<Unit> -> uploadAvatarUseCase(uri)
                    is StateListener.Idle -> flowOf(it)
                    is StateListener.Loading -> flowOf(it)
                    is StateListener.Error -> throw Exception(it.e?.message)
                }
            }.flatMapConcat {
                when(it){
                    is StateListener.Success<String> -> {
                        val data = it.result
                        sellerRequest.avtShop = data
                        registerSellerUseCase(sellerRequest)
                    }
                    is StateListener.Idle -> flowOf(it)
                    is StateListener.Loading -> flowOf(it)
                    is StateListener.Error -> throw Exception(it.e?.message)
                }
            }.catch {
                _registerState.value = StateListener.Error(Exception(it.message))
            }.collectLatest {
                _registerState.value = it
            }
        }
    }

    fun saveTokenToLocal(authResponse: AuthResponse) {
        tokenManager.writeAll(authResponse)
        tokenManager.setSeller()
    }

}
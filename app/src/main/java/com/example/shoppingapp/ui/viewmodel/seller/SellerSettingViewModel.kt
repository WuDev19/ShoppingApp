package com.example.shoppingapp.ui.viewmodel.seller

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.constants.TokenManager
import com.example.shoppingapp.domain.usecase.profileusecase.UploadAvatarUseCase
import com.example.shoppingapp.domain.usecase.sellerusecase.EditProfileSellerUseCase
import com.example.shoppingapp.domain.usecase.sellerusecase.GetSellerDetailInfoUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerSettingViewModel @Inject constructor(
    private val getSellerDetailInfoUseCase: GetSellerDetailInfoUseCase,
    private val editProfileSellerUseCase: EditProfileSellerUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _editState = MutableSharedFlow<StateListener<Unit>>()
    val editState = _editState.asSharedFlow()

    val getSellerDetail = getSellerDetailInfoUseCase(tokenManager.getSellerId()).stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        StateListener.Idle
    )

    fun editProfile(profile: Map<String, String>) {
        viewModelScope.launch {
            editProfileSellerUseCase(tokenManager.getSellerId(), profile).collectLatest {
                _editState.emit(it)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun uploadAvatarBeforeUpdateProfile(uri: Uri) {
        viewModelScope.launch {
            uploadAvatarUseCase(uri).flatMapConcat {
                when (it) {
                    is StateListener.Idle -> flowOf(it)
                    is StateListener.Loading -> flowOf(it)
                    is StateListener.Error -> flowOf(it)
                    is StateListener.Success<String> -> {
                        editProfileSellerUseCase(
                            tokenManager.getSellerId(), mapOf(
                                "newAvatar" to it.result
                            )
                        )
                    }
                }
            }.collectLatest {
                _editState.emit(it)
            }
        }
    }

}
package com.example.shoppingapp.ui.viewmodel.edit_profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.domain.model.user.UserProfile
import com.example.shoppingapp.domain.usecase.profileusecase.GetUserProfileUseCase
import com.example.shoppingapp.domain.usecase.profileusecase.UpdateAvatarUseCase
import com.example.shoppingapp.domain.usecase.profileusecase.UploadAvatarUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _profileUserState: MutableStateFlow<StateListener<UserProfile>> =
        MutableStateFlow(StateListener.Idle)
    val profileUserState = _profileUserState.asStateFlow()

    private val _avatarState: MutableStateFlow<StateListener<String>> =
        MutableStateFlow(StateListener.Idle)
    val avatarState = _avatarState.asStateFlow()
    fun getProfileUser() {
        viewModelScope.launch {
            getUserProfileUseCase().collectLatest {
                _profileUserState.value = it
            }
        }
    }

    //phân biệt đơn giản giữa các cái như zip, combine, merge (song song)
    //với flatMapConcat, flatMapLatest, flatMapMerge chính là song song với tuần tự

    //cach 1 ko su dung flatMapConcat
    fun uploadImageToCloudinary(uri: Uri) {
        viewModelScope.launch {
            uploadAvatarUseCase(uri).collectLatest { upload ->
                when (upload) {
                    is StateListener.Idle -> _avatarState.value = StateListener.Idle
                    is StateListener.Loading -> _avatarState.value = StateListener.Loading
                    is StateListener.Error -> _avatarState.value = upload
                    is StateListener.Success -> {
                        updateAvatarUseCase(auth.currentUser!!.uid, upload.result).collectLatest {
                            _avatarState.value = it
                        }
                    }
                }
            }
        }
    }

    //cach 2 su dung flatMapConcat
    @OptIn(ExperimentalCoroutinesApi::class)
    fun uploadAvatarToCloudinary(uri: Uri) {
        viewModelScope.launch {
            flowOf(uri)
                .flatMapConcat { uri ->
                    uploadAvatarUseCase(uri)
                }
                .flatMapConcat { value ->
                    when (value) {
                        is StateListener.Idle -> flowOf(value)
                        is StateListener.Loading -> flowOf(value)
                        is StateListener.Error -> flowOf(value)
                        is StateListener.Success -> {
                            Log.e(
                                StringConstants.DEBUG,
                                "uploadAvatarToCloudinary: ${value.result}"
                            )
                            updateAvatarUseCase(auth.currentUser!!.uid, value.result)
                        }
                    }
                }
                .collectLatest {
                    _avatarState.value = it
                }
        }
    }
}
package com.example.shoppingapp.domain.usecase.user_usecase.profile_usecase

import android.net.Uri
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadAvatarUseCase @Inject constructor(private val repo: UserRepository) {

    operator fun invoke(uri: Uri): Flow<StateListener<String>> = repo.uploadAvatarToCloudinary(uri)

}
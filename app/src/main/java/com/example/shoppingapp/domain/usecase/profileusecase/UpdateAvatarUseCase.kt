package com.example.shoppingapp.domain.usecase.profileusecase

import com.example.shoppingapp.data.source.cloudinary.CloudinaryUploader
import com.example.shoppingapp.di.qualifier.AvatarUploader
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateAvatarUseCase @Inject constructor(
    @param:AvatarUploader private val uploader: CloudinaryUploader, //qualifier cho param (trong constructor)
    private val repo: UserRepository
) {
    operator fun invoke(url: String): Flow<StateListener<String>> = flow {

    }

}
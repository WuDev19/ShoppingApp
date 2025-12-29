package com.example.shoppingapp.domain.usecase.profileusecase

import com.example.shoppingapp.data.remote.dto.request.UserAddressRequest
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EditUserAddressUseCase @Inject constructor(private val repo: UserRepository) {

    operator fun invoke(
        id: Int,
        userAddressRequest: UserAddressRequest
    ): Flow<StateListener<Unit>> = repo.editUserAddress(id, userAddressRequest)

}
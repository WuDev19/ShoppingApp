package com.example.shoppingapp.domain.usecase.profileusecase

import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteUserAddressUseCase @Inject constructor(private val repo: UserRepository) {

    operator fun invoke(idUser: String, idAddress: Int): Flow<StateListener<Int>> {
        require(idUser.isNotEmpty()){
            StringConstants.USER_ID_NULL_OR_EMPTY
        }
        return repo.deleteUserAddress(idUser, idAddress)
    }

}
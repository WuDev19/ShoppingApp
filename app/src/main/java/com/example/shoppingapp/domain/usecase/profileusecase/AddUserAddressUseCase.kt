package com.example.shoppingapp.domain.usecase.profileusecase

import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.remote.dto.request.UserAddressRequest
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
class AddUserAddressUseCase @Inject constructor(private val repo: UserRepository) {

    operator fun invoke(
        idUser: String,
        userAddressRequest: UserAddressRequest
    ): Flow<StateListener<Unit>> {
        require(
            !userAddressRequest.sdtND.isNullOrEmpty() &&
                    !userAddressRequest.tenND.isNullOrEmpty() &&
                    !userAddressRequest.tenDiaChi.isNullOrEmpty()
        )
        {
            StringConstants.USER_ADDRESS_ERROR_MESSAGE
        }
        return repo.addUserAddress(idUser, userAddressRequest);
    }

}
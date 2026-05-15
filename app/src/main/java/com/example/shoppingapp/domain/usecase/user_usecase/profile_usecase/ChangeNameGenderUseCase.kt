package com.example.shoppingapp.domain.usecase.user_usecase.profile_usecase

import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.data.remote.dto.request.UserDTOUpdate
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// TODO: refractor model
class ChangeNameGenderUseCase @Inject constructor(private val repo: UserRepository) {

    operator fun invoke(userDTOUpdate: UserDTOUpdate): Flow<StateListener<Unit>> {
        require(!userDTOUpdate.maND.isNullOrEmpty()) {
            StringConstants.USER_ID_NULL_OR_EMPTY
        }
        return repo.changeNameGenderUser(userDTOUpdate)
    }

}
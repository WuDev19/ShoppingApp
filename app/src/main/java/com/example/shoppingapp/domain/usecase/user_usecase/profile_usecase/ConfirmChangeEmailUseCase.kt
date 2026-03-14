package com.example.shoppingapp.domain.usecase.user_usecase.profile_usecase

import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConfirmChangeEmailUseCase @Inject constructor(private val repo: UserRepository) {

    operator fun invoke(id: String?, email: String?): Flow<StateListener<Unit>> {
        require(!id.isNullOrEmpty()){
            StringConstants.USER_ID_NULL_OR_EMPTY
        }
        require(!email.isNullOrEmpty()){
            StringConstants.USER_ID_NULL_OR_EMPTY
        }
        return repo.confirmChangeEmail(id, email)
    }

}
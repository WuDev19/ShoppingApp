package com.example.shoppingapp.domain.usecase.authusecase

import com.example.shoppingapp.domain.repository.authrepo.AuthRepository
import com.example.shoppingapp.ui.state.StateListener
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginGoogleUseCase @Inject constructor(private val repo: AuthRepository) {

    suspend operator fun invoke (idToken: String) : Flow<StateListener<AuthResult>>{
        return repo.loginWithGoogle(idToken)
    }

}
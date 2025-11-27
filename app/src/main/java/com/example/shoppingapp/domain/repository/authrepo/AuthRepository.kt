package com.example.shoppingapp.domain.repository.authrepo

import com.example.shoppingapp.domain.model.AuthUser
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun getCurrentUser() : AuthUser?
    suspend fun loginWithEmail(email: String, password: String) : Flow<StateListener<AuthResult>>
    suspend fun createAccountWithEmail(email: String, password: String, phone: String, username: String) : Flow<StateListener<AuthResult>>
    suspend fun resetPassword(email: String) : Flow<StateListener<Void>>
    suspend fun loginWithGoogle(idToken: String) : Flow<StateListener<AuthResult>>
}
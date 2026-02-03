package com.example.shoppingapp.domain.repository.auth

import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.data.remote.dto.request.RefreshRequest
import com.example.shoppingapp.domain.model.AuthUser
import com.example.shoppingapp.domain.model.LoginModel
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun getCurrentUser(): AuthUser?
    fun loginWithEmail(email: String, password: String): Flow<StateListener<AuthResponse>>
    fun createAccountWithEmail(
        email: String,
        password: String,
        phone: String,
        username: String
    ): Flow<StateListener<LoginModel>>

    fun resetPassword(email: String): Flow<StateListener<Void>>
    fun loginWithGoogle(idToken: String): Flow<StateListener<AuthResponse>>
    fun logout(): Flow<StateListener<Boolean>>

    suspend fun refresh(refreshRequest: RefreshRequest): StateListener<AuthResponse>
}
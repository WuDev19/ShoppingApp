package com.example.shoppingapp.data.repository

import com.example.shoppingapp.domain.model.AuthUser
import com.example.shoppingapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import jakarta.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val auth : FirebaseAuth): AuthRepository {

    override suspend fun getCurrentUser(): AuthUser? {
        return mapFirebaseUserToAuthUser()
    }

    private fun mapFirebaseUserToAuthUser() : AuthUser? {
        return auth.currentUser?.let { AuthUser(it.uid) }
    }

}
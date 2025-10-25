package com.example.shoppingapp.domain.repository

import com.example.shoppingapp.domain.model.AuthUser

interface AuthRepository {
    suspend fun getCurrentUser() : AuthUser?
}
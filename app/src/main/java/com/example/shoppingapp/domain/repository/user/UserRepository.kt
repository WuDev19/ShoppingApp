package com.example.shoppingapp.domain.repository.user

import com.example.shoppingapp.domain.model.user.User
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserName(): Flow<StateListener<String>>
    fun getCurrentUser(id: String): Flow<StateListener<User>>

}
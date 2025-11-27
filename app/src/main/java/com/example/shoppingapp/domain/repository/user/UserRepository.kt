package com.example.shoppingapp.domain.repository.user

import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun getUserName() : Flow<StateListener<String>>

}
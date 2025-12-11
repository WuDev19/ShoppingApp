package com.example.shoppingapp.domain.repository.user

import com.example.shoppingapp.data.remote.dto.post.UserDTOUpdate
import com.example.shoppingapp.domain.model.user.BaseUser
import com.example.shoppingapp.domain.model.user.UserProfile
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserName(): Flow<StateListener<String>>
    fun getCurrentUser(): Flow<StateListener<BaseUser>>
    fun logout(): Flow<StateListener<Boolean>>
    fun getProfileUser(): Flow<StateListener<UserProfile>>

    fun changeNameGenderUser(userDTOUpdate: UserDTOUpdate): Flow<StateListener<Unit>>

}
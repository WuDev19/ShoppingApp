package com.example.shoppingapp.data.repository.impl

import com.example.shoppingapp.data.remote.api.UserAPI
import com.example.shoppingapp.domain.model.user.User
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val userAPI: UserAPI
) : UserRepository {

    override fun getUserName(): Flow<StateListener<String>> = flow {
        try {
            val id = auth.currentUser!!.uid
            val responseGetUserName = userAPI.getUserName(id)
            if (responseGetUserName.isSuccessful) {
                val userDTO = responseGetUserName.body()
                userDTO?.let {
                    emit(StateListener.Success(it.ten ?: "Sir"))
                }
            } else {
                emit(StateListener.Error(Exception("Can't find user")))
            }
        } catch (e: Exception) {
            emit(StateListener.Error(e))
        }
    }

    override fun getCurrentUser(id: String): Flow<StateListener<User>> {
        TODO("Not yet implemented")
    }

}
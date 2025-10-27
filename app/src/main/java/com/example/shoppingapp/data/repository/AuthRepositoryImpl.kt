package com.example.shoppingapp.data.repository

import com.example.shoppingapp.domain.model.AuthUser
import com.example.shoppingapp.domain.repository.authrepo.AuthRepository
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val auth : FirebaseAuth): AuthRepository {

    override suspend fun getCurrentUser(): AuthUser? {
        return mapFirebaseUserToAuthUser()
    }

    override suspend fun loginWithEmail(email: String, password: String): Flow<StateListener<AuthResult>> {
        return flow {
            emit(StateListener.Loading)
            try {
                val task = auth.signInWithEmailAndPassword(email, password).await()
                emit(StateListener.Success(task))
            }
            catch (e : Exception){
                emit(StateListener.Error(e))
            }
        }
    }

    private fun mapFirebaseUserToAuthUser() : AuthUser? {
        return auth.currentUser?.let { AuthUser(it.uid) }
    }

}
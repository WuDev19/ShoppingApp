package com.example.shoppingapp.data.repository

import com.example.shoppingapp.domain.model.AuthUser
import com.example.shoppingapp.domain.model.User
import com.example.shoppingapp.domain.repository.authrepo.AuthRepository
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun getCurrentUser(): AuthUser? {
        return mapFirebaseUserToAuthUser()
    }

    override suspend fun loginWithEmail(
        email: String,
        password: String
    ): Flow<StateListener<AuthResult>> {
        return flow {
            emit(StateListener.Loading)
            try {
                val task = auth.signInWithEmailAndPassword(email, password).await()
                emit(StateListener.Success(task))
            } catch (e: Exception) {
                emit(StateListener.Error(e))
            }
        }
    }

    override suspend fun createAccountWithEmail(
        email: String,
        password: String,
        phone: String,
        username: String
    ): Flow<StateListener<User>> {
        return flow {
            emit(StateListener.Loading)
            try {
                val createResult = auth.createUserWithEmailAndPassword(email, password).await()
                createResult?.let {
                    val user = User(createResult.user!!.uid, username, email, phone, "")
                    val createUserFireStore = firestore
                        .collection("user")
                        .add(user)
                        .await()
                    createUserFireStore?.let {
                        emit(StateListener.Success(user))
                    } ?: emit(StateListener.Error(Exception("Can't not create user")))
                } ?: emit(StateListener.Error(Exception("User not exist")))
            } catch (e: Exception) {
                emit(StateListener.Error(e))
            }
        }
    }

    override suspend fun resetPassword(email: String): Flow<StateListener<Void>> = flow {
        emit(StateListener.Loading)
        try {
            val result = auth.sendPasswordResetEmail(email).await()
            emit(StateListener.Success(result))
        } catch (e: Exception) {
            emit(StateListener.Error(e))
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Flow<StateListener<AuthResult>> =
        flow {
            emit(StateListener.Loading)
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                emit(StateListener.Success(result))
            }
            catch (e: Exception){
                emit(StateListener.Error(e))
            }
        }

    private fun mapFirebaseUserToAuthUser(): AuthUser? {
        return auth.currentUser?.let { AuthUser(it.uid) }
    }

}
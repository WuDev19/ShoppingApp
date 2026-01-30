package com.example.shoppingapp.data.repository.impl

import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.remote.api.UserAPI
import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.data.remote.dto.get.ErrorResponseDTO
import com.example.shoppingapp.data.remote.dto.request.UserDTOCreate
import com.example.shoppingapp.domain.model.AuthUser
import com.example.shoppingapp.domain.model.LoginModel
import com.example.shoppingapp.domain.repository.auth.AuthRepository
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userAPI: UserAPI,
    private val moshi: Moshi
) : AuthRepository {

    override suspend fun getCurrentUser(): AuthUser? {
        return auth.currentUser?.mapToAuthUser()
    }

    override fun loginWithEmail(
        email: String,
        password: String
    ): Flow<StateListener<AuthResponse>> {
        return flow {
            emit(StateListener.Loading)
            try {
                val task = auth.signInWithEmailAndPassword(email, password).await()
                val response = userAPI.token(task.user!!.uid)
                if (response.isSuccessful) {
                    val data = response.body()
                    data?.let {
                        emit(StateListener.Success(it))
                    } ?: emit(StateListener.Error(Exception(StringConstants.API_ERROR)))
                } else {
                    val errorResponse = moshi.adapter(ErrorResponseDTO::class.java)
                    emit(
                        StateListener.Error(
                            Exception(
                                errorResponse.fromJson(
                                    response.errorBody().toString()
                                )?.message
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                emit(StateListener.Error(e))
            }
        }
    }

    override fun createAccountWithEmail(
        email: String,
        password: String,
        phone: String,
        username: String
    ): Flow<StateListener<LoginModel>> {
        return flow {
            emit(StateListener.Loading)
            try {
                val createResult = auth.createUserWithEmailAndPassword(email, password).await()
                createResult.user?.let {
                    val user: UserDTOCreate = if (phone.isEmpty()) {
                        UserDTOCreate(it.uid, username, email, null, null, "USER")
                    } else {
                        UserDTOCreate(it.uid, username, email, phone, null, "USER")
                    }
                    firestore
                        .collection("user")
                        .document(it.uid)
                        .set(user)
                        .await()
                    val responsePostUser = userAPI.createUser(user)
                    if (responsePostUser.isSuccessful) {
                        val useTmp = responsePostUser.body()
                        if (useTmp == null) {
                            emit(StateListener.Error(Exception("Can't not create user")))
                            it.delete()
                        } else {
                            emit(StateListener.Success(LoginModel(email, password)))
                        }
                    } else {
                        emit(StateListener.Error(Exception("Internet Issue")))
                        it.delete()
                    }
                } ?: emit(StateListener.Error(Exception("User not exist")))
            } catch (e: Exception) {
                emit(StateListener.Error(e))
            }
        }
    }

    override fun resetPassword(email: String): Flow<StateListener<Void>> = flow {
        emit(StateListener.Loading)
        try {
            val result = auth.sendPasswordResetEmail(email).await()
            emit(StateListener.Success(result))
        } catch (e: Exception) {
            emit(StateListener.Error(e))
        }
    }

    override fun loginWithGoogle(idToken: String): Flow<StateListener<AuthResponse>> =
        flow {
            emit(StateListener.Loading)
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                result.user?.let {
                    val user = UserDTOCreate(
                        it.uid, it.displayName, it.email,
                        it.phoneNumber, null, "USER"
                    )
                    firestore
                        .collection("user")
                        .document(user.maND!!)
                        .set(user)
                        .await()
                    val responsePostUser = userAPI.createUser(user)
                    if (responsePostUser.isSuccessful) {
                        val useTmp = responsePostUser.body()
                        if (useTmp == null) {
                            emit(StateListener.Error(Exception("Can't not create user")))
                            it.delete()
                        } else {
                            val responseToken = userAPI.token(it.uid)
                            if (responseToken.isSuccessful) {
                                val data = responseToken.body()
                                data?.let { authResponse ->
                                    emit(StateListener.Success(authResponse))
                                } ?: emit(StateListener.Error(Exception(StringConstants.API_ERROR)))
                            } else {
                                val errorResponse = moshi.adapter(ErrorResponseDTO::class.java)
                                emit(
                                    StateListener.Error(
                                        Exception(
                                            errorResponse.fromJson(
                                                responseToken.errorBody().toString()
                                            )?.message
                                        )
                                    )
                                )
                            }
                        }
                    } else {
                        emit(StateListener.Error(Exception(responsePostUser.message())))
                        it.delete()
                    }
                } ?: emit(StateListener.Error(Exception("User not exist")))
            } catch (e: Exception) {
                emit(StateListener.Error(e))
            }
        }
    private fun FirebaseUser.mapToAuthUser(): AuthUser {
        return AuthUser(uid)
    }

}
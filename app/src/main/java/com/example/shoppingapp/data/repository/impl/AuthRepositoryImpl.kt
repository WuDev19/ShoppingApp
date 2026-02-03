package com.example.shoppingapp.data.repository.impl

import android.content.SharedPreferences
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.remote.api.AuthAPI
import com.example.shoppingapp.data.remote.api.UserAPI
import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.data.remote.dto.get.ErrorResponseDTO
import com.example.shoppingapp.data.remote.dto.request.GenerateTokenRequest
import com.example.shoppingapp.data.remote.dto.request.LogoutRequest
import com.example.shoppingapp.data.remote.dto.request.RefreshRequest
import com.example.shoppingapp.data.remote.dto.request.UserDTOCreate
import com.example.shoppingapp.di.qualifier.DeviceId
import com.example.shoppingapp.di.qualifier.IpAddress
import com.example.shoppingapp.domain.model.AuthUser
import com.example.shoppingapp.domain.model.LoginModel
import com.example.shoppingapp.domain.repository.auth.AuthRepository
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userAPI: UserAPI,
    private val authAPI: AuthAPI,
    private val moshi: Moshi,
    private val credentialManager: CredentialManager,
    @param:IpAddress private val ipAddress: String?,
    @param:DeviceId private val deviceId: String?,
    private val sharedPreferences: SharedPreferences
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
                val response = authAPI.token(
                    GenerateTokenRequest(
                        task.user!!.uid,
                        deviceId,
                        ipAddress
                    )
                )
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
                            val responseToken = authAPI.token(
                                GenerateTokenRequest(
                                    result.user!!.uid,
                                    deviceId,
                                    ipAddress
                                )
                            )
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

    override fun logout(): Flow<StateListener<Boolean>> = flow {
        emit(StateListener.Loading)
        val token = sharedPreferences.getString(StringConstants.TOKEN, null)
        val refreshToken = sharedPreferences.getString(StringConstants.REFRESH_TOKEN, null)
        if (token == null || refreshToken == null) {
            emit(StateListener.Error(Exception(StringConstants.HAS_ERROR)))
        } else {
            auth.signOut()
            try {
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
                val response = authAPI.logout(LogoutRequest(token, refreshToken))
                if (response.isSuccessful) {
                    emit(StateListener.Success(true))
                } else {
                    val errorBody = response.errorBody()
                    val adapter = moshi.adapter(ErrorResponseDTO::class.java)
                    val errorDTO = errorBody?.string()?.let {
                        adapter.fromJson(it)
                    }
                    emit(StateListener.Error(Exception(errorDTO?.message)))
                }
            } catch (e: ClearCredentialException) {
                emit(StateListener.Error(e))
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun refresh(refreshRequest: RefreshRequest): StateListener<AuthResponse> {
        try {
            val response = authAPI.refresh(refreshRequest)
            if (response.isSuccessful) {
                val authResult = response.body()
                authResult?.let {
                    return StateListener.Success(it)
                }
            } else {
                val errorResponse = response.errorBody()
                val adapter = moshi.adapter(ErrorResponseDTO::class.java)
                val errorResponseDTO = errorResponse?.string()?.let {
                    adapter.fromJson(it)
                }
                return StateListener.Error(Exception(buildString {
                    append(errorResponseDTO?.status)
                    append("_")
                    append(errorResponseDTO?.message)
                }))
            }
        } catch (e: Exception) {
            return StateListener.Error(e)
        }
        return StateListener.Error(Exception(StringConstants.API_ERROR))
    }

    private fun FirebaseUser.mapToAuthUser(): AuthUser {
        return AuthUser(uid)
    }

}
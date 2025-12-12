package com.example.shoppingapp.data.repository.impl

import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import com.example.shoppingapp.data.local.mapper.UserMapper
import com.example.shoppingapp.data.remote.api.UserAPI
import com.example.shoppingapp.data.remote.dto.post.UserDTOUpdate
import com.example.shoppingapp.domain.model.user.BaseUser
import com.example.shoppingapp.domain.model.user.UserProfile
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val userAPI: UserAPI,
    private val credentialManager: CredentialManager,
    private val firestore: FirebaseFirestore
) : UserRepository {

    override fun getUserName(): Flow<StateListener<String>> = flow {
        try {
            val id = auth.currentUser!!.uid
            val responseGetUserName = userAPI.getUser(id)
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

    override fun getCurrentUser(): Flow<StateListener<BaseUser>> = flow {
        emit(StateListener.Loading)
        try {
            val userResponse = userAPI.getUser(auth.currentUser?.uid ?: "")
            if (userResponse.isSuccessful) {
                val dataResponse = userResponse.body()
                dataResponse?.let {
                    emit(StateListener.Success(UserMapper.mapToBaseUser(it)))
                } ?: emit(StateListener.Error(Exception("Map Error")))
            } else {
                emit(StateListener.Error(Exception("Bad Request")))
            }
        } catch (e: Exception) {
            emit(StateListener.Error(e))
        }
    }

    override fun logout(): Flow<StateListener<Boolean>> = flow {
        emit(StateListener.Loading)
        auth.signOut()
        try {
            val clearRequest = ClearCredentialStateRequest()
            credentialManager.clearCredentialState(clearRequest)
            emit(StateListener.Success(true))
        } catch (e: ClearCredentialException) {
            emit(StateListener.Error(e))
        }
    }

    override fun getProfileUser(): Flow<StateListener<UserProfile>> = flow {
        emit(StateListener.Loading)
        try {
            val userResponse = userAPI.getUser(auth.currentUser?.uid ?: "")
            if (userResponse.isSuccessful) {
                val dataResponse = userResponse.body()
                dataResponse?.let {
                    emit(StateListener.Success(UserMapper.mapToUserProfile(it)))
                } ?: emit(StateListener.Error(Exception("Map Error")))
            } else {
                emit(StateListener.Error(Exception("Bad Request")))
            }
        } catch (e: Exception) {
            emit(StateListener.Error(e))
        }
    }

    override fun changeNameGenderUser(userDTOUpdate: UserDTOUpdate): Flow<StateListener<Unit>> =
        flow {
            emit(StateListener.Loading)
            try {
                val response = userAPI.updateField(userDTOUpdate)
                if (response.isSuccessful) {
                    emit(StateListener.Success(Unit))
                } else {
                    emit(StateListener.Error(Exception("Cant update")))
                }
            } catch (e: Exception) {
                emit(StateListener.Error(e))
            }
        }

    //them cap nhat cho auth nua
    // TODO: them man hinh de xac nhan cap nhat da roi moi goi api
    override fun changeEmailPhoneUser(userDTOUpdate: UserDTOUpdate): Flow<StateListener<Unit>> =
        flow {
            emit(StateListener.Loading)
            userDTOUpdate.email?.let {
                auth.currentUser?.verifyBeforeUpdateEmail(it)?.await()
            }
            val response = userAPI.updateField(userDTOUpdate)
            if (response.isSuccessful) {
                emit(StateListener.Success(Unit))
            } else {
                emit(StateListener.Error(Exception(response.message())))
            }
        }.catch {
            emit(StateListener.Error(Exception(it.message)))
        }
}
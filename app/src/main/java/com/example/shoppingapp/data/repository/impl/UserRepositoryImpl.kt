package com.example.shoppingapp.data.repository.impl

import android.net.Uri
import android.util.Log
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.data.local.dao.UserAddressDAO
import com.example.shoppingapp.data.local.mapper.UserAddressMapper
import com.example.shoppingapp.data.local.mapper.UserMapper
import com.example.shoppingapp.data.remote.api.OrderAPI
import com.example.shoppingapp.data.remote.api.PaymentAPI
import com.example.shoppingapp.data.remote.api.UserAPI
import com.example.shoppingapp.data.remote.dto.get.ErrorResponseDTO
import com.example.shoppingapp.data.remote.dto.request.OrderPaymentRequest
import com.example.shoppingapp.data.remote.dto.request.OrderRequest
import com.example.shoppingapp.data.remote.dto.request.UserAddressRequest
import com.example.shoppingapp.data.remote.dto.request.UserDTOUpdate
import com.example.shoppingapp.data.remote.helper.NetworkHelper
import com.example.shoppingapp.data.remote.mapper.OrderMapper
import com.example.shoppingapp.data.source.cloudinary.CloudinaryUploader
import com.example.shoppingapp.di.qualifier.AvatarUploader
import com.example.shoppingapp.domain.model.AddressItem
import com.example.shoppingapp.domain.model.AfterPayment
import com.example.shoppingapp.domain.model.Order
import com.example.shoppingapp.domain.model.OrderDetail
import com.example.shoppingapp.domain.model.OrderStatus
import com.example.shoppingapp.domain.model.user.BaseUser
import com.example.shoppingapp.domain.model.user.UserProfile
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val userAPI: UserAPI,
    private val orderAPI: OrderAPI,
    private val paymentAPI: PaymentAPI,
    private val firestore: FirebaseFirestore,
    @param:AvatarUploader private val avatarUploader: CloudinaryUploader,
    private val moshi: Moshi,
    private val userAddressDAO: UserAddressDAO
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
    }.flowOn(Dispatchers.IO)

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
    }.flowOn(Dispatchers.IO)

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
    }.flowOn(Dispatchers.IO)

    override fun changeNameGenderUser(userDTOUpdate: UserDTOUpdate): Flow<StateListener<Unit>> =
        flow {
            emit(StateListener.Loading)
            try {
                if (userDTOUpdate.ten != null) {
                    firestore.collection("user")
                        .document(userDTOUpdate.maND!!)
                        .update("ten", userDTOUpdate.ten)
                        .await()
                }
                if (userDTOUpdate.gioitinh != null) {
                    firestore.collection("user")
                        .document(userDTOUpdate.maND!!)
                        .update("gioitinh", userDTOUpdate.gioitinh)
                        .await()
                }
                val response = userAPI.updateNameGender(userDTOUpdate)
                if (response.isSuccessful) {
                    emit(StateListener.Success(Unit))
                } else {
                    emit(StateListener.Error(Exception("Cant update")))
                }
            } catch (e: Exception) {
                emit(StateListener.Error(e))
            }
        }.flowOn(Dispatchers.IO)

    override fun changePhone(userDTOUpdate: UserDTOUpdate): Flow<StateListener<Unit>> =
        flow {
            emit(StateListener.Loading)
            firestore.collection("user")
                .document(userDTOUpdate.maND!!)
                .update("sdt", userDTOUpdate.sdt)
                .await()
            val response = userAPI.updatePhone(userDTOUpdate.maND!!, userDTOUpdate.sdt!!)
            if (response.isSuccessful) {
                emit(StateListener.Success(Unit))
            } else {
                emit(StateListener.Error(Exception(response.message())))
            }
        }.catch {
            emit(StateListener.Error(Exception(it.message)))
        }.flowOn(Dispatchers.IO)

    //xac nhan email xong roi moi cap nhat tren db
    override fun confirmChangeEmail(id: String, email: String): Flow<StateListener<Unit>> =
        flow {
            emit(StateListener.Loading)
            firestore.collection("user")
                .document(id)
                .update("email", email)
                .await()
            val response = userAPI.updateEmail(id, email)
            if (response.isSuccessful) {
                emit(StateListener.Success(Unit))
            } else {
                emit(StateListener.Error(Exception(response.message())))
            }
        }.catch {
            emit(StateListener.Error(Exception(it.message)))
        }.flowOn(Dispatchers.IO)

    override fun changeEmail(userDTOUpdate: UserDTOUpdate): Flow<StateListener<Unit>> =
        flow {
            emit(StateListener.Loading)
            //cap nhat phone luon
            userDTOUpdate.sdt?.let {
                firestore.collection("user")
                    .document(userDTOUpdate.maND!!)
                    .update("sdt", it)
                    .await()
                userAPI.updatePhone(userDTOUpdate.maND!!, it)
            }
            auth.currentUser?.verifyBeforeUpdateEmail(userDTOUpdate.email!!)?.await()
            emit(StateListener.Success(Unit))
        }.catch {
            emit(StateListener.Error(Exception(it.message)))
        }.flowOn(Dispatchers.IO)

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun updateAvatar(id: String, url: String): Flow<StateListener<String>> = flow {
        emit(StateListener.Loading)
        firestore.collection("user")
            .document(id)
            .update("anhdaidien", url)
            .await()
        val response = userAPI.updateAvatar(id, url)
        if (response.isSuccessful) {
            val data = response.body()
            emit(StateListener.Success(data?.anhdaidien ?: ""))
        } else {
            val errorBody = response.errorBody()
            val errorDTO = moshi.adapter(ErrorResponseDTO::class.java)
            emit(StateListener.Error(Exception(errorDTO.fromJson(errorBody?.string())?.message)))
        }
    }.catch {
        emit(StateListener.Error(Exception(it.message)))
    }.flowOn(Dispatchers.IO)

    override fun uploadAvatarToCloudinary(uri: Uri): Flow<StateListener<String>> = flow {
        emit(StateListener.Loading)
        val url = avatarUploader.upload(uri)
        emit(StateListener.Success(url))
    }.catch {
        emit(StateListener.Error(Exception(it.message)))
    }.flowOn(Dispatchers.IO)

    //implement ssot, pattern về quản lý dữ liệu tại một nơi duy nhất là room, room sẽ trả về Flow<List<Data>>
    //nên tách room riêng ra một method, call api -> insert vào room ra một method, như vậy sẽ chuẩn hơn, như này thì mỗi lần gọi api thì lại truy vấn room ko ổn lắm
    //room chỉ việc lắng nghe dữ liệu khi call api sau đó insert
    override fun getUserAddress(userId: String): Flow<List<AddressItem>> = flow {
        //room là nơi phát nguồn dữ liệu chính
        emitAll(
            userAddressDAO.getUserAddress(userId).onEach {
                Log.e(
                    StringConstants.DEBUG,
                    "getUserAddress: emit lai sau khi call api"
                )
            }.map { list ->
                list.map { UserAddressMapper.mapToAddressItem(it) }
            }
        )
    }.onStart {
        try {
            val response = userAPI.getUserAddress(userId) //api chỉ để làm mới room
            if (response.isSuccessful) {
                val data = response.body().orEmpty()
                val listUserAddressEntity = data.map {
                    UserAddressMapper.mapToUserAddressEntity(it)
                }
                userAddressDAO.clearAll(userId)
                userAddressDAO.insertAll(listUserAddressEntity)
            }
        } catch (e: Exception) {
            Log.e(StringConstants.DEBUG, "getUserAddress: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)

    override fun editUserAddress(
        idAddress: Int,
        userAddressRequest: UserAddressRequest
    ): Flow<StateListener<Unit>> = flow {
        emit(StateListener.Loading)
        val response = userAPI.updateAddress(idAddress, userAddressRequest)
        if (response.isSuccessful) {
            emit(StateListener.Success(Unit))
        } else {
            val errorBody = response.errorBody()
            val adapter = moshi.adapter(ErrorResponseDTO::class.java)
            errorBody?.string()?.let {
                emit(StateListener.Error(Exception(adapter.fromJson(it)?.message)))
            }
        }
    }.catch {
        emit(StateListener.Error(Exception(it.message)))
    }.flowOn(Dispatchers.IO)

    override fun addUserAddress(
        idUser: String,
        userAddressRequest: UserAddressRequest
    ): Flow<StateListener<Unit>> = flow {
        emit(StateListener.Loading)
        val response = userAPI.addAddress(idUser, userAddressRequest)
        if (response.isSuccessful) {
            emit(StateListener.Success(Unit))
        } else {
            val errorBody = response.errorBody()
            val adapter = moshi.adapter(ErrorResponseDTO::class.java)
            errorBody?.string()?.let {
                emit(StateListener.Error(Exception(adapter.fromJson(it)?.message)))
            }
        }
    }.catch {
        emit(StateListener.Error(Exception(it.message)))
    }.flowOn(Dispatchers.IO)

    override fun deleteUserAddress(
        idUser: String,
        idAddress: Int
    ): Flow<StateListener<Int>> = flow {
        emit(StateListener.Loading)
        val response = userAPI.deleteUserAddress(idUser, idAddress)
        if (response.isSuccessful) {
            emit(StateListener.Success(response.code()))
            Log.e(StringConstants.DEBUG, "deleteUserAddress: ${response.code()}")
        } else {
            emit(StateListener.Error(Exception(StringConstants.API_ERROR)))
        }
    }.catch {
        emit(StateListener.Error(Exception(it.message)))
    }.flowOn(Dispatchers.IO)

    override suspend fun createOrder(
        shopGhnId: Int,
        orderRequest: OrderRequest
    ): StateListener<Order> =
        try {
            val response = orderAPI.createOrder(shopGhnId, orderRequest)
            if (response.isSuccessful) {
                val result = response.body()
                result?.let {
                    StateListener.Success(Order(it.orderId, it.amount))
                } ?: StateListener.Error(Exception(StringConstants.API_ERROR))
            } else {
                val errorBody = response.errorBody()
                val adapter = moshi.adapter(ErrorResponseDTO::class.java)
                errorBody?.string()?.let {
                    StateListener.Error(Exception(adapter.fromJson(it)?.message))
                } ?: StateListener.Error(Exception(StringConstants.API_ERROR))
            }
        } catch (e: Exception) {
            StateListener.Error(e)
        }

    override suspend fun createPayment(orderPaymentRequest: OrderPaymentRequest): StateListener<AfterPayment> =
        try {
            val response = paymentAPI.createPayment(orderPaymentRequest)
            if (response.isSuccessful) {
                val result = response.body()
                result?.let {
                    StateListener.Success(AfterPayment(orderPaymentRequest.orderId, it.paymentUrl))
                } ?: StateListener.Error(Exception(StringConstants.API_ERROR))
            } else {
                val errorBody = response.errorBody()
                val adapter = moshi.adapter(ErrorResponseDTO::class.java)
                errorBody?.string()?.let {
                    StateListener.Error(Exception(adapter.fromJson(it)?.message))
                } ?: StateListener.Error(Exception(StringConstants.API_ERROR))
            }
        } catch (e: Exception) {
            StateListener.Error(e)
        }

    override suspend fun updateStatusOrder(orderId: Int): StateListener<Order> =
        try {
            val response = orderAPI.updateStatusOrder(orderId)
            if (response.isSuccessful) {
                val result = response.body()
                result?.let {
                    StateListener.Success(Order(it.orderId, it.amount))
                } ?: StateListener.Error(Exception(StringConstants.API_ERROR))
            } else {
                val errorBody = response.errorBody()
                val adapter = moshi.adapter(ErrorResponseDTO::class.java)
                errorBody?.string()?.let {
                    StateListener.Error(Exception(adapter.fromJson(it)?.message))
                } ?: StateListener.Error(Exception(StringConstants.API_ERROR))
            }
        } catch (e: Exception) {
            StateListener.Error(e)
        }

    override suspend fun getOrderStatus(orderId: Int): StateListener<OrderStatus> {
        return NetworkHelper.callApi(
            apiCall = {
                orderAPI.getOrderDetail(orderId)
            },
            mapper = {
                OrderMapper.mapToOrderStatus(it)
            },
            moshi
        )
    }

    override suspend fun getOrderDetail(orderId: Int): StateListener<OrderDetail> {
        return NetworkHelper.callApi(
            apiCall = {
                orderAPI.getOrderDetail(orderId)
            },
            mapper = { orderDetailDTO ->
                OrderMapper.mapToOrderDetail(orderDetailDTO)
            },
            moshi
        )
    }
}
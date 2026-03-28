package com.example.shoppingapp.domain.repository.user

import android.net.Uri
import androidx.paging.PagingData
import com.example.shoppingapp.data.remote.dto.request.OrderPaymentRequest
import com.example.shoppingapp.data.remote.dto.request.OrderRequest
import com.example.shoppingapp.data.remote.dto.request.UserAddressRequest
import com.example.shoppingapp.data.remote.dto.request.UserDTOUpdate
import com.example.shoppingapp.domain.model.AddressItem
import com.example.shoppingapp.domain.model.AfterPayment
import com.example.shoppingapp.domain.model.Order
import com.example.shoppingapp.domain.model.OrderDetail
import com.example.shoppingapp.domain.model.OrderStatus
import com.example.shoppingapp.domain.model.OrderUiModel
import com.example.shoppingapp.domain.model.user.BaseUser
import com.example.shoppingapp.domain.model.user.UserProfile
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserName(): Flow<StateListener<String>>
    fun getCurrentUser(): Flow<StateListener<BaseUser>>
    fun getProfileUser(): Flow<StateListener<UserProfile>>
    fun changeNameGenderUser(userDTOUpdate: UserDTOUpdate): Flow<StateListener<Unit>>
    fun changePhone(userDTOUpdate: UserDTOUpdate): Flow<StateListener<Unit>>
    fun confirmChangeEmail(id: String, email: String): Flow<StateListener<Unit>>
    fun changeEmail(userDTOUpdate: UserDTOUpdate): Flow<StateListener<Unit>>
    fun updateAvatar(id: String, url: String): Flow<StateListener<String>>
    fun uploadAvatarToCloudinary(uri: Uri): Flow<StateListener<String>>
    fun getUserAddress(userId: String): Flow<List<AddressItem>>
    fun editUserAddress(
        idAddress: Int,
        userAddressRequest: UserAddressRequest
    ): Flow<StateListener<Unit>>
    fun addUserAddress(
        idUser: String,
        userAddressRequest: UserAddressRequest
    ): Flow<StateListener<Unit>>
    fun deleteUserAddress(idUser: String, idAddress: Int): Flow<StateListener<Int>>
    suspend fun createOrder(shopGhnId: Int, orderRequest: OrderRequest): StateListener<Order>
    suspend fun createPayment(orderPaymentRequest: OrderPaymentRequest): StateListener<AfterPayment>
    suspend fun updateStatusOrder(orderId: String): StateListener<Order>
    suspend fun getOrderStatus(orderId: String): StateListener<OrderStatus>
    suspend fun getOrderDetail(orderId: String): StateListener<OrderDetail>
    fun getOrderViaStatus(userId: String, status: String): Flow<PagingData<OrderUiModel>>

}
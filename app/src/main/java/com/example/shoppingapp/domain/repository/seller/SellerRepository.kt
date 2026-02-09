package com.example.shoppingapp.domain.repository.seller

import androidx.paging.PagingData
import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.data.remote.dto.request.SellerRequest
import com.example.shoppingapp.domain.model.OtpModel
import com.example.shoppingapp.domain.model.SellerInStore
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface SellerRepository {
    fun getAllSeller(): Flow<PagingData<SellerInStore>>
    fun getSellerById(idSeller: Int): Flow<StateListener<SellerInStore>>
    fun sendOtp(): Flow<StateListener<OtpModel>>
    fun verifyOtp(otp: String): Flow<StateListener<Unit>>
    fun registerSeller(sellerRequest: SellerRequest): Flow<StateListener<AuthResponse>>

}
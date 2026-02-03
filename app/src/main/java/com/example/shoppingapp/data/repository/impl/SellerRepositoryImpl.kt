package com.example.shoppingapp.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.data.remote.mapper.SellerMapper
import com.example.shoppingapp.data.remote.api.SellerAPI
import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.data.remote.dto.get.ErrorResponseDTO
import com.example.shoppingapp.data.remote.dto.get.OtpDto
import com.example.shoppingapp.data.remote.dto.request.GenerateTokenRequest
import com.example.shoppingapp.data.remote.dto.request.RegisterSellerRequest
import com.example.shoppingapp.data.remote.dto.request.SellerRequest
import com.example.shoppingapp.data.source.paging_source.PagingSourceSeller
import com.example.shoppingapp.di.qualifier.DeviceId
import com.example.shoppingapp.di.qualifier.IpAddress
import com.example.shoppingapp.domain.model.OtpModel
import com.example.shoppingapp.domain.model.SellerInStore
import com.example.shoppingapp.domain.repository.seller.SellerRepository
import com.example.shoppingapp.ui.state.StateListener
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SellerRepositoryImpl @Inject constructor(
    private val sellerAPI: SellerAPI,
    private val moshi: Moshi,
    @param:DeviceId private val deviceId: String?,
    @param:IpAddress private val ipAddress: String?
) :
    SellerRepository {

    override fun getAllSeller(): Flow<PagingData<SellerInStore>> {
        return Pager(
            config = PagingConfig(
                pageSize = StringConstants.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PagingSourceSeller(sellerAPI)
            }
        ).flow
    }

    override fun getSellerById(id: Int): Flow<StateListener<SellerInStore>> = flow {
        try {
            val response = sellerAPI.getSellerById(id)
            if (response.isSuccessful) {
                val sellerDTO = response.body()
                val sellerInStore = sellerDTO?.let {
                    SellerMapper.mapToSellerInStore(it)
                }
                sellerInStore?.let {
                    emit(StateListener.Success(it))
                } ?: emit(StateListener.Error(Exception("Cast Error")))
            } else {
                emit(StateListener.Error(Exception(response.message())))
            }
        } catch (e: Exception) {
            emit(StateListener.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun sendOtp(): Flow<StateListener<OtpModel>> = flow {
        val response = sellerAPI.sendOtp()
        if (response.isSuccessful) {
            val data = response.body()
            data?.let { emit(StateListener.Success(it.mapToOtpModel())) }
                ?: emit(
                    StateListener.Error(
                        Exception(StringConstants.API_ERROR)
                    )
                )
        } else {
            emit(StateListener.Error(Exception(StringConstants.API_ERROR)))
        }
    }.catch {
        emit(StateListener.Error(Exception(it.message)))
    }.flowOn(Dispatchers.IO)

    override fun verifyOtp(otp: String): Flow<StateListener<Unit>> = flow {
        emit(StateListener.Loading)
        val response = sellerAPI.verifyOtp(otp)
        if (response.isSuccessful) {
            emit(StateListener.Success(Unit))
        } else {
            val errorBody = response.errorBody()
            val errorResponseDTO = errorBody?.string()?.let {
                moshi.adapter(ErrorResponseDTO::class.java).fromJson(it)
            }
            emit(StateListener.Error(Exception(errorResponseDTO?.message)))
        }
    }.catch {
        emit(StateListener.Error(Exception(it.message)))
    }.flowOn(Dispatchers.IO)

    override fun registerSeller(sellerRequest: SellerRequest): Flow<StateListener<AuthResponse>> =
        flow {
            emit(StateListener.Loading)
            val response = sellerAPI.registerSeller(
                sellerRequest.maND,
                RegisterSellerRequest(
                    sellerRequest = sellerRequest,
                    generateTokenRequest = GenerateTokenRequest(
                        sellerRequest.maND,
                        deviceId,
                        ipAddress
                    )
                )
            )
            if (response.isSuccessful) {
                val data = response.body()
                data?.let {
                    emit(StateListener.Success(it))
                }
            } else {
                val errorBody = response.errorBody()
                val errorResponseDTO = errorBody?.string()?.let {
                    moshi.adapter(ErrorResponseDTO::class.java).fromJson(it)
                }
                emit(StateListener.Error(Exception(errorResponseDTO?.message)))
            }
        }.catch {
            emit(StateListener.Error(Exception(it.message)))
        }.flowOn(Dispatchers.IO)

    private fun OtpDto.mapToOtpModel(): OtpModel {
        return OtpModel(this.expireTime, this.message)
    }

}
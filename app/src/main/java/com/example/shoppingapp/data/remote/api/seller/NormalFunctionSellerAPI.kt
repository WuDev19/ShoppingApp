package com.example.shoppingapp.data.remote.api.seller

import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.data.remote.dto.get.ListSellerDTO
import com.example.shoppingapp.data.remote.dto.get.OtpDto
import com.example.shoppingapp.data.remote.dto.get.SellerDTO
import com.example.shoppingapp.data.remote.dto.request.RegisterSellerRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NormalFunctionSellerAPI {

    @GET("seller/get")
    suspend fun getAllSeller(@Query("pageNumber") pageNumber: Int): Response<ListSellerDTO>

    @GET("seller/{idSeller}")
    suspend fun getSellerById(@Path("idSeller") idSeller: Int): Response<SellerDTO>

    @POST("seller/send/otp")
    suspend fun sendOtp(): Response<OtpDto>

    @POST("seller/verify-otp")
    suspend fun verifyOtp(@Query("otp") otp: String): Response<Unit>

    @POST("seller/register/{idUser}")
    suspend fun registerSeller(
        @Path("idUser") idUser: String,
        @Body registerSellerRequest: RegisterSellerRequest
    ): Response<AuthResponse>

}
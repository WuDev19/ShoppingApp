package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.get.AuthResponse
import com.example.shoppingapp.data.remote.dto.get.UserAddressDTO
import com.example.shoppingapp.data.remote.dto.get.UserDTOGet
import com.example.shoppingapp.data.remote.dto.request.UserAddressRequest
import com.example.shoppingapp.data.remote.dto.request.UserDTOCreate
import com.example.shoppingapp.data.remote.dto.request.UserDTOUpdate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserAPI {

    @GET("user/get/{id}")
    suspend fun getUser(@Path("id") id: String): Response<UserDTOGet>

    @PATCH("user/update/name/gender")
    suspend fun updateNameGender(@Body user: UserDTOUpdate): Response<UserDTOGet>

    @PATCH("user/update/phone/{id}")
    suspend fun updatePhone(
        @Path("id") id: String,
        @Query("phone") phone: String
    ): Response<UserDTOGet>

    @PATCH("user/update/email/{id}")
    suspend fun updateEmail(
        @Path("id") id: String,
        @Query("email") email: String
    ): Response<UserDTOGet>

    @PATCH("user/update/avt/{id}")
    suspend fun updateAvatar(
        @Path("id") id: String,
        @Query("url") url: String
    ): Response<UserDTOGet>

    @GET("user/get/address/{id}")
    suspend fun getUserAddress(@Path("id") id: String): Response<List<UserAddressDTO>>

    @PATCH("user/update/address/{id}")
    suspend fun updateAddress(
        @Path("id") id: Int,
        @Body userAddressRequest: UserAddressRequest
    ): Response<Unit>

    @PATCH("user/update/add/address/{id}")
    suspend fun addAddress(
        @Path("id") id: String,
        @Body userAddressRequest: UserAddressRequest
    ): Response<Unit>

    @DELETE("user/{idUser}/address/{idAddress}")
    suspend fun deleteUserAddress(
        @Path("idUser") idUser: String,
        @Path("idAddress") idAddress: Int
    ): Response<Unit>

}
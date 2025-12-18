package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.get.UserDTOGet
import com.example.shoppingapp.data.remote.dto.post.UserDTOCreate
import com.example.shoppingapp.data.remote.dto.post.UserDTOUpdate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserAPI {

    @POST("user/create")
    suspend fun createUser(@Body user: UserDTOCreate): Response<UserDTOGet>

    @GET("user/get/{id}")
    suspend fun getUser(@Path("id") id: String) : Response<UserDTOGet>

    @PATCH("user/update/name/gender")
    suspend fun updateNameGender(@Body user: UserDTOUpdate) : Response<UserDTOGet>

    @PATCH("user/update/phone/{id}")
    suspend fun updatePhone(@Path("id") id: String, @Query("phone") phone: String) : Response<UserDTOGet>

    @PATCH("user/update/email/{id}")
    suspend fun updateEmail(@Path("id") id: String, @Query("email") email: String) : Response<UserDTOGet>

    @PATCH("user/update/avt/{id}")
    suspend fun updateAvatar(@Path("id") id: String, @Query("url") url: String) : Response<UserDTOGet>

}
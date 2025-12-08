package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.get.UserDTOGet
import com.example.shoppingapp.data.remote.dto.post.UserDTORequest
import com.example.shoppingapp.domain.model.user.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserAPI {

    @POST("user/create")
    suspend fun createUser(@Body user: UserDTORequest): Response<UserDTOGet>

    @GET("user/get/{id}")
    suspend fun getUserName(@Path("id") id: String) : Response<UserDTOGet>

}
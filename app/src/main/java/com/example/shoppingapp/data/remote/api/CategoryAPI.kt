package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.CategoryAndQuantityDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoryAPI {

    @GET("danhmuc/tenVaSl/{id}")
    suspend fun getDanhMucVaSl(@Path("id") id: Int): Response<List<CategoryAndQuantityDTO>>

}
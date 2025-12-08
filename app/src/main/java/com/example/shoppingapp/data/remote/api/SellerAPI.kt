package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.remote.dto.get.ListSellerDTO
import com.example.shoppingapp.data.remote.dto.get.SellerDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SellerAPI {

    @GET("seller/get")
    suspend fun getAllSeller(@Query("pageNumber") pageNumber: Int) : Response<ListSellerDTO>

    @GET("seller/{id}")
    suspend fun getSellerById(@Path("id") id: Int) : Response<SellerDTO>

}
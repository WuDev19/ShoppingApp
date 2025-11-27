package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.model.ListSellerDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SellerAPI {

    @GET("seller/get")
    suspend fun getAllSeller(@Query("offset") offset: Int) : Response<ListSellerDTO>

}
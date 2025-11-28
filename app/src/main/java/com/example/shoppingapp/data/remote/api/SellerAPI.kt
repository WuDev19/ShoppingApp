package com.example.shoppingapp.data.remote.api

import com.example.shoppingapp.data.model.ListSellerDTO
import com.example.shoppingapp.data.model.ProductDTO
import com.example.shoppingapp.data.model.SellerDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SellerAPI {

    @GET("seller/get")
    suspend fun getAllSeller(@Query("offset") offset: Int) : Response<ListSellerDTO>

    @GET("seller/{id}")
    suspend fun getSellerById(@Path("id") id: Int) : Response<SellerDTO>

    @GET("seller/searchShop/{id}")
    suspend fun timKiemSanPhamCuaShop(@Path("id") id: Int, @Query("text") text: String, @Query("offset") offset: Int): Response<ProductDTO>

}
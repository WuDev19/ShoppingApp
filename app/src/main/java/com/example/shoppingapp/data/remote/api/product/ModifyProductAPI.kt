package com.example.shoppingapp.data.remote.api.product

import com.example.shoppingapp.data.remote.dto.request.PostProductRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ModifyProductAPI {

    @POST("sanpham/modification/new-product/{sellerId}")
    suspend fun postProduct(@Path("sellerId") sellerId: Int, @Body postProductRequest: PostProductRequest) : Int

}
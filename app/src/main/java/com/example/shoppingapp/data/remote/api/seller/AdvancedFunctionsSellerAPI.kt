package com.example.shoppingapp.data.remote.api.seller

import com.example.shoppingapp.data.remote.dto.get.SellerDTO
import com.example.shoppingapp.data.remote.dto.get.ShopVouchersDTO
import com.example.shoppingapp.data.remote.dto.request.VoucherRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface AdvancedFunctionsSellerAPI {

    @PATCH("seller/profile/{sellerId}")
    suspend fun editProfile(
        @Path("sellerId") sellerId: Int,
        @QueryMap profile: Map<String, String>
    ): Response<SellerDTO>

    @POST("seller/vouchers/create/{sellerId}")
    suspend fun createVoucher(
        @Path("sellerId") sellerId: Int,
        @Body voucherRequest: VoucherRequest
    ): Response<Unit>

    @GET("seller/vouchers/{sellerId}")
    suspend fun getShopVouchers(@Path("sellerId") sellerId: Int): Response<List<ShopVouchersDTO>>

}
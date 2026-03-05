package com.example.shoppingapp.data.remote.api.ghn

import com.example.shoppingapp.data.remote.dto.get.ghn.CalculateFeeGhnDTO
import com.example.shoppingapp.data.remote.dto.get.ghn.EstimateTimeDeliveryDTO
import com.example.shoppingapp.data.remote.dto.get.ghn.ServiceDTO
import com.example.shoppingapp.data.remote.dto.request.ghn.CalculateFeeGhnRequest
import com.example.shoppingapp.data.remote.dto.request.ghn.EstimateTimeGhnRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ServiceAPI {

    @GET("ghn/services")
    suspend fun getService(@QueryMap params: Map<String, String>): Response<ServiceDTO>

    @POST("ghn/services/fee/{shopId}")
    suspend fun calculateFee(
        @Path("shopId") shopId: Int,
        @Body calculateFeeGhnRequest: CalculateFeeGhnRequest
    ): Response<CalculateFeeGhnDTO>

    @POST("ghn/services/time-delivery/{shopId}")
    suspend fun estimateTime(
        @Path("shopId") shopId: Int,
        @Body estimateTimeGhnRequest: EstimateTimeGhnRequest
    ): Response<EstimateTimeDeliveryDTO>

}
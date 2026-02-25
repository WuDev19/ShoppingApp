package com.example.shoppingapp.data.remote.api.ghn

import com.example.shoppingapp.data.remote.dto.get.ghn.ProvinceGhnDTO
import retrofit2.Response
import retrofit2.http.GET

interface AddressAPI {

    @GET("ghn/provinces")
    suspend fun getProvinces(): Response<List<ProvinceGhnDTO>>

}
package com.example.shoppingapp.data.remote.api.ghn

import com.example.shoppingapp.data.remote.dto.get.ghn.DistrictGhnDTO
import com.example.shoppingapp.data.remote.dto.get.ghn.ProvinceGhnDTO
import com.example.shoppingapp.data.remote.dto.get.ghn.WardGhnDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AddressAPI {

    @GET("ghn/provinces")
    suspend fun getProvinces(): Response<List<ProvinceGhnDTO>>

    @GET("ghn/province/districts")
    suspend fun getDistrictOfProvince(@Query("provinceId") provinceId: Int): Response<List<DistrictGhnDTO>>

    @GET("ghn/province/district/wards")
    suspend fun getWardOfDistrict(@Query("districtId") districtId: Int): Response<List<WardGhnDTO>>

}
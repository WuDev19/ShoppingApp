package com.example.shoppingapp.domain.repository.ghn

import com.example.shoppingapp.data.remote.dto.get.ghn.CalculateFeeGhnDTO
import com.example.shoppingapp.data.remote.dto.request.ghn.CalculateFeeGhnRequest
import com.example.shoppingapp.data.remote.dto.request.ghn.EstimateTimeGhnRequest
import com.example.shoppingapp.domain.model.ghn.FeeServiceGhn
import com.example.shoppingapp.domain.model.ghn.DistrictGhn
import com.example.shoppingapp.domain.model.ghn.EstimateGhn
import com.example.shoppingapp.domain.model.ghn.ProvinceGhn
import com.example.shoppingapp.domain.model.ghn.ServiceGhn
import com.example.shoppingapp.domain.model.ghn.WardGhn
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface GhnRepository {
    fun getProvinceGhn(): Flow<StateListener<List<ProvinceGhn>>>
    fun getDistrictOfProvince(provinceId: Int): Flow<StateListener<List<DistrictGhn>>>
    fun getWardOfDistrict(districtId: Int): Flow<StateListener<List<WardGhn>>>
    fun getService(params: Map<String, String>): Flow<StateListener<ServiceGhn>>
    fun calculateFee(
        calculateFeeGhnRequest: CalculateFeeGhnRequest,
        shopId: Int,
        serviceName: String
    ): Flow<StateListener<FeeServiceGhn>>
    fun estimateTime(
        estimateTimeGhnRequest: EstimateTimeGhnRequest,
        shopId: Int
    ): Flow<StateListener<EstimateGhn>>
}
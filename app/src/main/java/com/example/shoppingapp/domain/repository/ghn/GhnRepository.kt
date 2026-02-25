package com.example.shoppingapp.domain.repository.ghn

import com.example.shoppingapp.domain.model.ghn.DistrictGhn
import com.example.shoppingapp.domain.model.ghn.ProvinceGhn
import com.example.shoppingapp.domain.model.ghn.WardGhn
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface GhnRepository {
    fun getProvinceGhn(): Flow<StateListener<List<ProvinceGhn>>>
    fun getDistrictOfProvince(provinceId: Int): Flow<StateListener<List<DistrictGhn>>>
    fun getWardOfDistrict(districtId: Int): Flow<StateListener<List<WardGhn>>>
}
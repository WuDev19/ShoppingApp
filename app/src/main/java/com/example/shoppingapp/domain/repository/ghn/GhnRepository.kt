package com.example.shoppingapp.domain.repository.ghn

import com.example.shoppingapp.domain.model.ghn.ProvinceGhn
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface GhnRepository {
    fun getProvinceGhn(): Flow<StateListener<List<ProvinceGhn>>>
}
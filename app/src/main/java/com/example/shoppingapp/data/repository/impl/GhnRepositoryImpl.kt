package com.example.shoppingapp.data.repository.impl

import com.example.shoppingapp.data.local.dao.ProvinceGhnDAO
import com.example.shoppingapp.data.local.entity.ProvinceGhnEntity
import com.example.shoppingapp.data.local.mapper.ProvinceMapper
import com.example.shoppingapp.data.remote.api.ghn.AddressAPI
import com.example.shoppingapp.data.remote.dto.get.ErrorResponseDTO
import com.example.shoppingapp.domain.model.ghn.ProvinceGhn
import com.example.shoppingapp.domain.repository.ghn.GhnRepository
import com.example.shoppingapp.ui.state.StateListener
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GhnRepositoryImpl @Inject constructor(
    private val addressAPI: AddressAPI,
    private val moshi: Moshi,
    private val provinceGhnDAO: ProvinceGhnDAO
) : GhnRepository {

    override fun getProvinceGhn(): Flow<StateListener<List<ProvinceGhn>>> = flow {
        try {
            val check = provinceGhnDAO.checkDataExist()
            if (check == null) {
                val result = addressAPI.getProvinces()
                if (result.isSuccessful) {
                    val data = result.body() ?: emptyList()
                    val provincesDomain = ArrayList<ProvinceGhn>(
                        data.size
                    ) //tạo sẵn kích thước để tối ưu, ko phải mở rộng mỗi lần add
                    val provincesEntity = ArrayList<ProvinceGhnEntity>(data.size)
                    data.forEach {
                        provincesDomain.add(ProvinceMapper.mapFromDtoToProvinceGhnDomain(it))
                        provincesEntity.add(ProvinceMapper.mapToProvinceGhnEntity(it))
                    }
                    if (provincesEntity.isNotEmpty()) {
                        provinceGhnDAO.insertAll(provincesEntity)
                    }
                    emit(StateListener.Success(provincesDomain))
                } else {
                    val errorBody = result.errorBody()
                    val adapter = moshi.adapter(ErrorResponseDTO::class.java)
                    errorBody?.string()?.let {
                        emit(StateListener.Error(Exception(adapter.fromJson(it)?.message)))
                    }
                }
            } else {
                val result = provinceGhnDAO.getProvinces()
                emit(StateListener.Success(result.map {
                    ProvinceMapper.mapFromEntityToProvinceGhnDomain(it)
                }))
            }
        } catch (e: Exception) {
            emit(StateListener.Error(e))
        }
    }.flowOn(Dispatchers.IO)

}
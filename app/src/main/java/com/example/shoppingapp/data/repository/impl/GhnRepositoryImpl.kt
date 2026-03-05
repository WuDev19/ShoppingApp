package com.example.shoppingapp.data.repository.impl

import android.util.Log
import com.example.shoppingapp.data.local.dao.DistrictGhnDAO
import com.example.shoppingapp.data.local.dao.ProvinceGhnDAO
import com.example.shoppingapp.data.local.dao.WardGhnDAO
import com.example.shoppingapp.data.local.entity.DistrictGhnEntity
import com.example.shoppingapp.data.local.entity.ProvinceGhnEntity
import com.example.shoppingapp.data.local.entity.WardGhnEntity
import com.example.shoppingapp.data.local.mapper.DistrictMapper
import com.example.shoppingapp.data.local.mapper.ProvinceMapper
import com.example.shoppingapp.data.local.mapper.WardMapper
import com.example.shoppingapp.data.remote.api.ghn.AddressAPI
import com.example.shoppingapp.data.remote.api.ghn.ServiceAPI
import com.example.shoppingapp.data.remote.dto.get.ErrorResponseDTO
import com.example.shoppingapp.data.remote.dto.get.ghn.CalculateFeeGhnDTO
import com.example.shoppingapp.data.remote.dto.request.ghn.CalculateFeeGhnRequest
import com.example.shoppingapp.data.remote.dto.request.ghn.EstimateTimeGhnRequest
import com.example.shoppingapp.data.remote.mapper.GhnMapper
import com.example.shoppingapp.domain.model.ghn.FeeServiceGhn
import com.example.shoppingapp.domain.model.ghn.DistrictGhn
import com.example.shoppingapp.domain.model.ghn.EstimateGhn
import com.example.shoppingapp.domain.model.ghn.ProvinceGhn
import com.example.shoppingapp.domain.model.ghn.ServiceGhn
import com.example.shoppingapp.domain.model.ghn.WardGhn
import com.example.shoppingapp.domain.repository.ghn.GhnRepository
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.utils.StringConstants
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.retryWhen
import javax.inject.Inject

class GhnRepositoryImpl @Inject constructor(
    private val addressAPI: AddressAPI,
    private val serviceAPI: ServiceAPI,
    private val moshi: Moshi,
    private val provinceGhnDAO: ProvinceGhnDAO,
    private val districtGhnDAO: DistrictGhnDAO,
    private val wardGhnDAO: WardGhnDAO
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

    override fun getDistrictOfProvince(provinceId: Int): Flow<StateListener<List<DistrictGhn>>> =
        flow {
            try {
                val check = districtGhnDAO.checkDataExist(provinceId)
                if (check == null) {
                    val result = addressAPI.getDistrictOfProvince(provinceId)
                    if (result.isSuccessful) {
                        val data = result.body() ?: emptyList()
                        val districtDomain = ArrayList<DistrictGhn>(
                            data.size
                        ) //tạo sẵn kích thước để tối ưu, ko phải mở rộng mỗi lần add
                        val districtEntity = ArrayList<DistrictGhnEntity>(data.size)
                        data.forEach {
                            districtDomain.add(DistrictMapper.mapFromDtoToDomain(it))
                            districtEntity.add(DistrictMapper.mapFromDtoToEntity(it))
                        }
                        if (districtEntity.isNotEmpty()) {
                            districtGhnDAO.insertAll(districtEntity)
                        }
                        emit(StateListener.Success(districtDomain))
                    } else {
                        val errorBody = result.errorBody()
                        val adapter = moshi.adapter(ErrorResponseDTO::class.java)
                        errorBody?.string()?.let {
                            emit(StateListener.Error(Exception(adapter.fromJson(it)?.message)))
                        }
                    }
                } else {
                    val result = districtGhnDAO.getDistrictOfProvince(provinceId)
                    emit(StateListener.Success(result.map {
                        DistrictMapper.mapFromEntityToDomain(it)
                    }))
                }
            } catch (e: Exception) {
                emit(StateListener.Error(e))
            }
        }.flowOn(Dispatchers.IO)

    override fun getWardOfDistrict(districtId: Int): Flow<StateListener<List<WardGhn>>> =
        flow {
            try {
                val check = wardGhnDAO.checkDataExist(districtId)
                if (check == null) {
                    val result = addressAPI.getWardOfDistrict(districtId)
                    if (result.isSuccessful) {
                        val data = result.body() ?: emptyList()
                        val wardDomain = ArrayList<WardGhn>(
                            data.size
                        ) //tạo sẵn kích thước để tối ưu, ko phải mở rộng mỗi lần add
                        val wardEntity = ArrayList<WardGhnEntity>(data.size)
                        data.forEach {
                            wardDomain.add(WardMapper.mapFromDtoToDomain(it))
                            wardEntity.add(WardMapper.mapFromDtoToEntity(it))
                        }
                        if (wardEntity.isNotEmpty()) {
                            wardGhnDAO.insertAll(wardEntity)
                        }
                        emit(StateListener.Success(wardDomain))
                    } else {
                        val errorBody = result.errorBody()
                        val adapter = moshi.adapter(ErrorResponseDTO::class.java)
                        errorBody?.string()?.let {
                            emit(StateListener.Error(Exception(adapter.fromJson(it)?.message)))
                        }
                    }
                } else {
                    val result = wardGhnDAO.getWardOfDistrict(districtId)
                    emit(StateListener.Success(result.map {
                        WardMapper.mapFromEntityToDomain(it)
                    }))
                }
            } catch (e: Exception) {
                emit(StateListener.Error(e))
            }
        }.flowOn(Dispatchers.IO)

    override fun getService(params: Map<String, String>): Flow<StateListener<ServiceGhn>> = flow {
        val response = serviceAPI.getService(params)
        if (response.isSuccessful) {
            val data = response.body()
            Log.e(StringConstants.DEBUG, "getService: $data")
            data?.let {
                emit(StateListener.Success(it.mapToDomain()))
            }
        } else {
            val errorBody = response.errorBody()
            val adapter = moshi.adapter(ErrorResponseDTO::class.java)
            errorBody?.string()?.let {
                emit(StateListener.Error(Exception(adapter.fromJson(it)?.message)))
            }
        }
    }.catch {
        emit(StateListener.Error(Exception(it.message)))
    }.flowOn(Dispatchers.IO)

    override fun calculateFee(
        calculateFeeGhnRequest: CalculateFeeGhnRequest,
        shopId: Int,
        serviceName: String
    ): Flow<StateListener<FeeServiceGhn>> = flow {
        val response = serviceAPI.calculateFee(shopId, calculateFeeGhnRequest)
        if (response.isSuccessful) {
            val data = response.body()
            data?.let {
                emit(StateListener.Success(GhnMapper.mapFromFeeDtoToFeeServiceGhn(it, serviceName)))
            }
        } else {
            emit(StateListener.Error(Exception(StringConstants.API_ERROR)))
        }
    }.catch {
        emit(StateListener.Error(Exception(it.message)))
    }.flowOn(Dispatchers.IO)

    override fun estimateTime(
        estimateTimeGhnRequest: EstimateTimeGhnRequest,
        shopId: Int
    ): Flow<StateListener<EstimateGhn>> = flow {
        val response = serviceAPI.estimateTime(shopId, estimateTimeGhnRequest)
        if (response.isSuccessful) {
            val data = response.body()
            data?.let {
                val from =
                    it.from_estimate_date.substring(0, 10).split("-").reversed().joinToString("-")
                        .substring(0, 5)
                val to =
                    it.to_estimate_date.substring(0, 10).split("-").reversed().joinToString("-")
                        .substring(0, 5)
                emit(StateListener.Success(EstimateGhn(from, to)))
            }
        } else {
            emit(StateListener.Error(Exception(StringConstants.API_ERROR)))
        }
    }.catch {
        emit(StateListener.Error(Exception(it.message)))
    }.flowOn(Dispatchers.IO)

}
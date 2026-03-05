package com.example.shoppingapp.ui.viewmodel.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.domain.model.ghn.DistrictGhn
import com.example.shoppingapp.domain.model.ghn.ProvinceGhn
import com.example.shoppingapp.domain.model.ghn.WardGhn
import com.example.shoppingapp.domain.usecase.ghn_usecase.GetDistrictOfProvinceUseCase
import com.example.shoppingapp.domain.usecase.ghn_usecase.GetProvinceUseCase
import com.example.shoppingapp.domain.usecase.ghn_usecase.GetWardOfDistrictUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class RegisterSellerViewModel @Inject constructor(
    private val getProvinceUseCase: GetProvinceUseCase,
    private val getDistrictOfProvinceUseCase: GetDistrictOfProvinceUseCase,
    private val getWardOfDistrictUseCase: GetWardOfDistrictUseCase
) :
    ViewModel() {
    private val _provincesState: MutableStateFlow<StateListener<List<ProvinceGhn>>> =
        MutableStateFlow(StateListener.Idle)
    val provincesState = _provincesState.asStateFlow()
    private val _districtState: MutableStateFlow<StateListener<List<DistrictGhn>>> =
        MutableStateFlow(StateListener.Idle)
    val districtState = _districtState.asStateFlow()
    private val _wardState: MutableStateFlow<StateListener<List<WardGhn>>> =
        MutableStateFlow(StateListener.Idle)
    val wardState = _wardState.asStateFlow()
    fun getProvinces() {
        viewModelScope.launch {
            getProvinceUseCase().collectLatest {
                _provincesState.value = it
            }
        }
    }

    fun getDistrictOfProvince(provinceId: Int) {
        viewModelScope.launch {
            getDistrictOfProvinceUseCase(provinceId).collectLatest {
                _districtState.value = it
            }
        }
    }

    fun getWardOfDistrict(districtId: Int) {
        viewModelScope.launch {
            getWardOfDistrictUseCase(districtId).collectLatest {
                _wardState.value = it
            }
        }
    }
}
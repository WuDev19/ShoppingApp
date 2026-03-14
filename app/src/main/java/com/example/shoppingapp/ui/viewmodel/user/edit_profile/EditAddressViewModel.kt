package com.example.shoppingapp.ui.viewmodel.user.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.remote.dto.request.UserAddressRequest
import com.example.shoppingapp.domain.model.ghn.DistrictGhn
import com.example.shoppingapp.domain.model.ghn.ProvinceGhn
import com.example.shoppingapp.domain.model.ghn.WardGhn
import com.example.shoppingapp.domain.usecase.ghn_usecase.GetDistrictOfProvinceUseCase
import com.example.shoppingapp.domain.usecase.ghn_usecase.GetProvinceUseCase
import com.example.shoppingapp.domain.usecase.ghn_usecase.GetWardOfDistrictUseCase
import com.example.shoppingapp.domain.usecase.user_usecase.profile_usecase.AddUserAddressUseCase
import com.example.shoppingapp.domain.usecase.user_usecase.profile_usecase.DeleteUserAddressUseCase
import com.example.shoppingapp.domain.usecase.user_usecase.profile_usecase.EditUserAddressUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAddressViewModel @Inject constructor(
    private val editUserAddressUseCase: EditUserAddressUseCase,
    private val addUserAddressUseCase: AddUserAddressUseCase,
    private val deleteUserAddressUseCase: DeleteUserAddressUseCase,
    private val getProvinceUseCase: GetProvinceUseCase,
    private val getDistrictOfProvinceUseCase: GetDistrictOfProvinceUseCase,
    private val getWardOfDistrictUseCase: GetWardOfDistrictUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _addressState: MutableStateFlow<StateListener<Unit>> =
        MutableStateFlow(StateListener.Idle)
    val addressState = _addressState.asStateFlow()
    private val _deleteState: MutableStateFlow<StateListener<Int>> =
        MutableStateFlow(StateListener.Idle)
    val deleteState = _deleteState.asStateFlow()

    private val _provincesState: MutableStateFlow<StateListener<List<ProvinceGhn>>> =
        MutableStateFlow(StateListener.Idle)
    val provincesState = _provincesState.asStateFlow()
    private val _districtState: MutableStateFlow<StateListener<List<DistrictGhn>>> =
        MutableStateFlow(StateListener.Idle)
    val districtState = _districtState.asStateFlow()
    private val _wardState: MutableStateFlow<StateListener<List<WardGhn>>> =
        MutableStateFlow(StateListener.Idle)
    val wardState = _wardState.asStateFlow()
    fun updateAddress(idAddress: Int, userAddressRequest: UserAddressRequest) {
        viewModelScope.launch {
            editUserAddressUseCase(idAddress, userAddressRequest).collectLatest {
                _addressState.value = it
            }
        }
    }

    fun addAddress(userAddressRequest: UserAddressRequest) {
        viewModelScope.launch {
            try {
                addUserAddressUseCase(
                    auth.currentUser?.uid ?: "",
                    userAddressRequest
                ).collectLatest {
                    _addressState.value = it
                }
            } catch (e: IllegalArgumentException) {
                _addressState.value = StateListener.Error(IllegalArgumentException(e.message))
            }
        }
    }

    fun deleteUserAddress(idUser: String, idAddress: Int) {
        viewModelScope.launch {
            try {
                deleteUserAddressUseCase(idUser, idAddress).collectLatest {
                    _deleteState.value = it
                }
            } catch (e: IllegalArgumentException) {
                _deleteState.value = StateListener.Error(IllegalArgumentException(e.message))
            }
        }
    }

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
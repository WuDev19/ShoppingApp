package com.example.shoppingapp.ui.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.remote.dto.get.ghn.ServiceDTO
import com.example.shoppingapp.data.remote.dto.request.ghn.CalculateFeeGhnRequest
import com.example.shoppingapp.data.remote.dto.request.ghn.EstimateTimeGhnRequest
import com.example.shoppingapp.domain.model.ghn.FeeServiceGhn
import com.example.shoppingapp.domain.model.ghn.ServiceGhn
import com.example.shoppingapp.domain.model.ghn.ShippingMethod
import com.example.shoppingapp.domain.usecase.ghn_usecase.CalculateFeeUseCase
import com.example.shoppingapp.domain.usecase.ghn_usecase.EstimateTimeDeliveryUseCase
import com.example.shoppingapp.domain.usecase.ghn_usecase.GetServiceGhnUseCase
import com.example.shoppingapp.domain.usecase.profileusecase.GetUserAddressUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PurchaseProductViewModel @Inject constructor(
    private val getUserAddressUseCase: GetUserAddressUseCase,
    private val getServiceGhnUseCase: GetServiceGhnUseCase,
    private val calculateFeeUseCase: CalculateFeeUseCase,
    private val estimateTimeDeliveryUseCase: EstimateTimeDeliveryUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _serviceState = MutableStateFlow<StateListener<ShippingMethod>>(StateListener.Idle)
    val serviceState = _serviceState.asStateFlow()

    val getAddress = getUserAddressUseCase(auth.currentUser?.uid ?: "").stateIn(
        viewModelScope,
        SharingStarted.Lazily, emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getService(
        params: Map<String, String>,
        calculateFeeGhnRequest: CalculateFeeGhnRequest,
        shopId: Int
    ) {
        viewModelScope.launch {
            getServiceGhnUseCase(params)
                .flatMapConcat {
                    when (it) {
                        is StateListener.Idle -> flowOf(StateListener.Idle)
                        is StateListener.Loading -> flowOf(StateListener.Loading)
                        is StateListener.Error -> flowOf(it)
                        is StateListener.Success<ServiceGhn> -> {
                            val service = it.result
                            calculateFeeUseCase(calculateFeeGhnRequest, shopId, service)
                                .zip(
                                    estimateTimeDeliveryUseCase(
                                        EstimateTimeGhnRequest(
                                            calculateFeeGhnRequest.to_district_id,
                                            calculateFeeGhnRequest.to_ward_code,
                                            service.serviceId
                                        ),
                                        shopId
                                    )
                                ) { fee, time ->
                                    if (fee is StateListener.Success && time is StateListener.Success) {
                                        StateListener.Success(
                                            ShippingMethod(
                                                shopId,
                                                fee.result,
                                                time.result,
                                                false
                                            )
                                        )
                                    } else {
                                        (fee as? StateListener.Error)
                                            ?: (time as? StateListener.Error)
                                            ?: StateListener.Loading
                                    }
                                }
                        }
                    }
                }
                .collectLatest {
                    _serviceState.value = it
                }
        }
    }

}
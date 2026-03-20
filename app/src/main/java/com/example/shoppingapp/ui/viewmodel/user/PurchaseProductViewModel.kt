package com.example.shoppingapp.ui.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.remote.dto.request.OrderPaymentRequest
import com.example.shoppingapp.data.remote.dto.request.ghn.CalculateFeeGhnRequest
import com.example.shoppingapp.data.remote.dto.request.ghn.EstimateTimeGhnRequest
import com.example.shoppingapp.domain.model.AddressItem
import com.example.shoppingapp.domain.model.AfterPayment
import com.example.shoppingapp.domain.model.FinalPrice
import com.example.shoppingapp.domain.model.Order
import com.example.shoppingapp.domain.model.OrderFromBuyNow
import com.example.shoppingapp.domain.model.OrderFromCart
import com.example.shoppingapp.domain.model.VoucherOrder
import com.example.shoppingapp.domain.model.ghn.ServiceGhn
import com.example.shoppingapp.domain.model.ghn.ShippingMethod
import com.example.shoppingapp.domain.usecase.ghn_usecase.CalculateFeeUseCase
import com.example.shoppingapp.domain.usecase.ghn_usecase.EstimateTimeDeliveryUseCase
import com.example.shoppingapp.domain.usecase.ghn_usecase.GetServiceGhnUseCase
import com.example.shoppingapp.domain.usecase.user_usecase.order_usecase.CreateOrderUseCase
import com.example.shoppingapp.domain.usecase.user_usecase.order_usecase.CreatePaymentUseCase
import com.example.shoppingapp.domain.usecase.user_usecase.profile_usecase.GetUserAddressUseCase
import com.example.shoppingapp.ui.state.StateListener
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PurchaseProductViewModel @Inject constructor(
    private val getUserAddressUseCase: GetUserAddressUseCase,
    private val getServiceGhnUseCase: GetServiceGhnUseCase,
    private val calculateFeeUseCase: CalculateFeeUseCase,
    private val estimateTimeDeliveryUseCase: EstimateTimeDeliveryUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val createPaymentUseCase: CreatePaymentUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _serviceState = MutableStateFlow<StateListener<ShippingMethod>>(StateListener.Idle)
    val serviceState = _serviceState.asStateFlow()
    private val _createOrderState = Channel<StateListener<String>>()
    val createOrderState = _createOrderState.receiveAsFlow()
    private val _paymentUrlState = Channel<StateListener<AfterPayment>>()
    val paymentUrlState = _paymentUrlState.receiveAsFlow()

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
                                                false,
                                                service.serviceId,
                                                service.serviceTypeId
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

    fun createOrder(
        orderFromBuyNow: OrderFromBuyNow? = null,
        listOrder: List<OrderFromCart>? = null,
        addressItem: AddressItem,
        shippingMethods: Map<Int, ShippingMethod>,
        voucherOrders: Map<Int, VoucherOrder>,
        finalPrices: Map<Int, FinalPrice>,
        note: Map<Int, String>,
        paymentMethod: String,
    ) {
        when (paymentMethod) {
            "COD" -> {
                createOrderWithCod(
                    orderFromBuyNow,
                    listOrder,
                    addressItem,
                    shippingMethods,
                    voucherOrders,
                    finalPrices,
                    note,
                    paymentMethod,
                )
            }

            "VNPay" -> {
                createOrderWithVNPay(
                    orderFromBuyNow,
                    listOrder,
                    addressItem,
                    shippingMethods,
                    voucherOrders,
                    finalPrices,
                    note,
                    paymentMethod,
                )
            }

            else -> viewModelScope.launch { _createOrderState.send(StateListener.Error(Exception("Please choose a payment method"))) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun createOrderWithCod(
        orderFromBuyNow: OrderFromBuyNow? = null,
        listOrder: List<OrderFromCart>? = null,
        addressItem: AddressItem,
        shippingMethods: Map<Int, ShippingMethod>,
        voucherOrders: Map<Int, VoucherOrder>,
        finalPrices: Map<Int, FinalPrice>,
        note: Map<Int, String>,
        paymentMethod: String,
    ) {
        viewModelScope.launch {
            _createOrderState.send(StateListener.Loading)
            if (orderFromBuyNow != null) {
                val result = createOrderUseCase(
                    orderFromBuyNow.shopGhnId,
                    orderFromBuyNow,
                    null,
                    addressItem,
                    shippingMethods,
                    voucherOrders,
                    finalPrices,
                    note,
                    paymentMethod,
                )
                if (result is StateListener.Success) {
                    _createOrderState.send(StateListener.Success(result.result.orderId))
                } else if (result is StateListener.Error) {
                    _createOrderState.send(StateListener.Error(result.e))
                }
            } else {
                val listShopId = listOrder!!.map { it.cart.shopGhnId }
                val listResult = listShopId.asFlow()
                    .flatMapMerge(concurrency = 5) { shopGhnId ->
                        flow {
                            val data = createOrderUseCase(
                                shopGhnId,
                                null,
                                listOrder.first { it.cart.shopGhnId == shopGhnId },
                                addressItem,
                                shippingMethods,
                                voucherOrders,
                                finalPrices,
                                note,
                                paymentMethod,
                            )
                            emit(data)
                        }.flowOn(Dispatchers.Default)
                    }.filterIsInstance<StateListener.Success<Order>>()
                    .toList()
                // TODO: sửa lại logic chỗ này
                if (listResult.isNotEmpty() && listResult.size == listShopId.size) {
                    val orderIdBuilder = StringBuilder()
                    listResult.forEach { order ->
                        orderIdBuilder.append((order.result.orderId)).append("_")
                    }
                    _createOrderState.send(
                        StateListener.Success(
                            orderIdBuilder.dropLast(1).toString()
                        )
                    )
                } else {
                    _createOrderState.send(StateListener.Error(Exception("Please check your data or connection")))
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun createOrderWithVNPay(
        orderFromBuyNow: OrderFromBuyNow? = null,
        listOrder: List<OrderFromCart>? = null,
        addressItem: AddressItem,
        shippingMethods: Map<Int, ShippingMethod>,
        voucherOrders: Map<Int, VoucherOrder>,
        finalPrices: Map<Int, FinalPrice>,
        note: Map<Int, String>,
        paymentMethod: String,
    ) {
        viewModelScope.launch {
            _paymentUrlState.send(StateListener.Loading)
            if (orderFromBuyNow != null) {
                flow {
                    emit(
                        createOrderUseCase(
                            orderFromBuyNow.shopGhnId,
                            orderFromBuyNow,
                            null,
                            addressItem,
                            shippingMethods,
                            voucherOrders,
                            finalPrices,
                            note,
                            paymentMethod,
                        )
                    )
                }.flatMapConcat { stateListenerOrder ->
                    flow {
                        when (stateListenerOrder) {
                            is StateListener.Idle -> emit(stateListenerOrder)
                            is StateListener.Loading -> emit(stateListenerOrder)
                            is StateListener.Error -> emit(stateListenerOrder)
                            is StateListener.Success<Order> -> {
                                val order = stateListenerOrder.result
                                val orderPaymentRequest =
                                    OrderPaymentRequest(order.orderId, order.amount)
                                val result = createPaymentUseCase(orderPaymentRequest)
                                emit(result)
                            }
                        }
                    }
                }.collectLatest {
                    _paymentUrlState.send(it)
                }
            } else {
                val listShopGhnId = listOrder!!.map { it.cart.shopGhnId }
                val listResult = listShopGhnId.asFlow()
                    .flatMapMerge { shopGhnId ->
                        flow {
                            val data = createOrderUseCase(
                                shopGhnId,
                                null,
                                listOrder.first { it.cart.shopGhnId == shopGhnId },
                                addressItem,
                                shippingMethods,
                                voucherOrders,
                                finalPrices,
                                note,
                                paymentMethod,
                            )
                            emit(data)
                        }.flowOn(Dispatchers.Default)
                    }.toList()
                val orderIdBuilder = StringBuilder()
                var totalAmount: Long = 0
                listResult.forEach {
                    if (it is StateListener.Success) {
                        orderIdBuilder.append(it.result.orderId).append("_")
                        totalAmount += it.result.amount
                    }
                }
                val orderId =
                    if (orderIdBuilder.isNotEmpty()) orderIdBuilder.dropLast(1).toString() else ""
                _paymentUrlState.send(
                    createPaymentUseCase(
                        OrderPaymentRequest(
                            orderId,
                            totalAmount
                        )
                    )
                )
            }
        }
    }
}
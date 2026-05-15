package com.example.shoppingapp.domain.usecase.user_usecase.order_usecase

import com.example.shoppingapp.data.remote.dto.request.OrderPaymentRequest
import com.example.shoppingapp.domain.model.AfterPayment
import com.example.shoppingapp.domain.repository.order.OrderRepository
import com.example.shoppingapp.ui.state.StateListener
import javax.inject.Inject

// TODO: refractor model
class CreatePaymentUseCase @Inject constructor(private val repo: OrderRepository) {

    suspend operator fun invoke(orderPaymentRequest: OrderPaymentRequest): StateListener<AfterPayment> =
        repo.createPayment(orderPaymentRequest)

}
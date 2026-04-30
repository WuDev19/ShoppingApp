package com.example.shoppingapp.domain.usecase.user_usecase.order_usecase

import com.example.shoppingapp.domain.model.Order
import com.example.shoppingapp.domain.repository.order.OrderRepository
import com.example.shoppingapp.ui.state.StateListener
import javax.inject.Inject

class UpdateStatusOrderWhenPaymentSuccess @Inject constructor(private val repo: OrderRepository) {

    suspend operator fun invoke(orderId: String): StateListener<Order> =
        repo.updateStatusOrderWhenPaymentSuccess(orderId)

}
package com.example.shoppingapp.domain.usecase.user_usecase.order_usecase

import android.annotation.SuppressLint
import com.example.shoppingapp.data.remote.dto.request.CancelOrderRequest
import com.example.shoppingapp.data.remote.dto.request.ghn.CancelOrderGhnRequest
import com.example.shoppingapp.domain.model.CancelOrder
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import java.time.Instant
import javax.inject.Inject

class CancelOrderUseCase @Inject constructor(private val repo: UserRepository) {

    @SuppressLint("NewApi")
    suspend operator fun invoke(
        orderId: String,
        shopGhnId: Int,
        orderGhnId: String
    ): StateListener<CancelOrder> {
        val cancelOrderGhnRequest = CancelOrderGhnRequest(listOf(orderGhnId))
        val cancelTime = Instant.now().toEpochMilli()
        val cancelOrderRequest = CancelOrderRequest(shopGhnId, cancelTime, cancelOrderGhnRequest)
        return repo.cancelOrder(orderId, cancelOrderRequest)
    }

}
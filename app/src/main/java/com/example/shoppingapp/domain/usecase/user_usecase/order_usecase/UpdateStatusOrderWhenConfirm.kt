package com.example.shoppingapp.domain.usecase.user_usecase.order_usecase

import com.example.shoppingapp.domain.model.user.OrderUpdateStatus
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import javax.inject.Inject

class UpdateStatusOrderWhenConfirm @Inject constructor(private val repo: UserRepository) {
    suspend operator fun invoke(orderId: String, status: String): StateListener<OrderUpdateStatus> =
        repo.updateStatusOrderWhenConfirm(orderId, status)
}
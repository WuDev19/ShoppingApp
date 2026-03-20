package com.example.shoppingapp.domain.usecase.user_usecase.order_usecase

import com.example.shoppingapp.domain.model.OrderDetail
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import javax.inject.Inject

class GetOrderDetailUseCase @Inject constructor(private val repo: UserRepository) {

    suspend operator fun invoke(orderId: String): StateListener<OrderDetail> =
        repo.getOrderDetail(orderId)

}
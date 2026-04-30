package com.example.shoppingapp.domain.usecase.user_usecase.order_usecase

import androidx.paging.PagingData
import com.example.shoppingapp.domain.model.OrderUiModel
import com.example.shoppingapp.domain.repository.order.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchOrderUseCase @Inject constructor(private val repo: OrderRepository) {

    operator fun invoke(
        userId: String,
        status: String,
        keyword: String
    ): Flow<PagingData<OrderUiModel>> = repo.searchOrder(userId, status, keyword)

}
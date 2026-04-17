package com.example.shoppingapp.domain.usecase.user_usecase.order_usecase

import androidx.paging.PagingData
import com.example.shoppingapp.domain.model.OrderUiModel
import com.example.shoppingapp.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchOrderUseCase @Inject constructor(private val repo: UserRepository) {

    operator fun invoke(
        userId: String,
        status: String,
        keyword: String
    ): Flow<PagingData<OrderUiModel>> = repo.searchOrder(userId, status, keyword)

}
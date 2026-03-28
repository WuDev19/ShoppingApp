package com.example.shoppingapp.domain.usecase.user_usecase.order_usecase

import androidx.paging.PagingData
import com.example.shoppingapp.domain.model.OrderUiModel
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrderViaStatusUseCase @Inject constructor(
    private val repo: UserRepository,
    private val auth: FirebaseAuth
) {

    operator fun invoke(status: String): Flow<PagingData<OrderUiModel>> =
        repo.getOrderViaStatus(auth.currentUser?.uid ?: "", status)

}
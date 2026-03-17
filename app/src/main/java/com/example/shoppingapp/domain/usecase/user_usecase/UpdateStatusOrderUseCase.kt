package com.example.shoppingapp.domain.usecase.user_usecase

import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import javax.inject.Inject

class UpdateStatusOrderUseCase @Inject constructor(private val repo: UserRepository) {

    suspend operator fun invoke(orderId: Int): StateListener<Unit> = repo.updateStatusOrder(orderId)

}
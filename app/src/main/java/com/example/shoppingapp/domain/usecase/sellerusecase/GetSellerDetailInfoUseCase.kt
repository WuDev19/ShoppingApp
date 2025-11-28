package com.example.shoppingapp.domain.usecase.sellerusecase

import com.example.shoppingapp.domain.model.SellerInStore
import com.example.shoppingapp.domain.repository.seller.SellerRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSellerDetailInfoUseCase @Inject constructor(private val repo: SellerRepository) {

    operator fun invoke(id: Int): Flow<StateListener<SellerInStore>> = repo.getSellerById(id)

}
package com.example.shoppingapp.domain.usecase.sellerusecase

import androidx.paging.PagingData
import com.example.shoppingapp.domain.model.SellerInStore
import com.example.shoppingapp.domain.repository.seller.SellerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSellerUseCase @Inject constructor(private val repo: SellerRepository) {

    operator fun invoke(): Flow<PagingData<SellerInStore>> = repo.getAllSeller()

}
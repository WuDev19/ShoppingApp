package com.example.shoppingapp.domain.usecase.sellerusecase

import androidx.paging.PagingData
import com.example.shoppingapp.domain.model.product.ProductDisplay
import com.example.shoppingapp.domain.repository.product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllProductOfShopUseCase @Inject constructor(private val repo: ProductRepository) {

    operator fun invoke(id: Int): Flow<PagingData<ProductDisplay>> = repo.getAllProductOfShop(id)

}
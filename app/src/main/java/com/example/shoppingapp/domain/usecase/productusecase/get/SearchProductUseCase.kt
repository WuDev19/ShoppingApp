package com.example.shoppingapp.domain.usecase.productusecase.get

import androidx.paging.PagingData
import com.example.shoppingapp.domain.model.product.ProductDisplay
import com.example.shoppingapp.domain.repository.product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchProductUseCase @Inject constructor(private val repo: ProductRepository) {

    operator fun invoke(text: String): Flow<PagingData<ProductDisplay>> = repo.searchProduct(text)

}
package com.example.shoppingapp.domain.usecase.product_usecase.get

import androidx.paging.PagingData
import com.example.shoppingapp.domain.model.product.ProductDisplay
import com.example.shoppingapp.domain.repository.product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductUseCase @Inject constructor(private val repo: ProductRepository) {

    operator fun invoke () : Flow<PagingData<ProductDisplay>> {
        return repo.getProducts()
    }

}
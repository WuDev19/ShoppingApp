package com.example.shoppingapp.domain.usecase.productusecase

import androidx.paging.PagingData
import com.example.shoppingapp.domain.model.Product
import com.example.shoppingapp.domain.repository.product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductByCategoryUseCase @Inject constructor(private val repo: ProductRepository) {

    operator fun invoke(theLoai: String): Flow<PagingData<Product>> = repo.getProductByCategory(theLoai)

}
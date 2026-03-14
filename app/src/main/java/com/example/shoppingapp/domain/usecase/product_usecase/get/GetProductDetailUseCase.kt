package com.example.shoppingapp.domain.usecase.product_usecase.get

import com.example.shoppingapp.domain.model.product.ProductDetail
import com.example.shoppingapp.domain.repository.product.ProductRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductDetailUseCase @Inject constructor(private val repo: ProductRepository) {

    operator fun invoke(id: Int) : Flow<StateListener<ProductDetail>> = repo.getProductDetail(id)

}
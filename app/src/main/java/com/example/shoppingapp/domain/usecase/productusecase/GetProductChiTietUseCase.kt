package com.example.shoppingapp.domain.usecase.productusecase

import com.example.shoppingapp.domain.model.ProductChiTiet
import com.example.shoppingapp.domain.repository.product.ProductRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductChiTietUseCase @Inject constructor(private val repo: ProductRepository) {

    operator fun invoke(id: Int): Flow<StateListener<List<ProductChiTiet>>> = repo.getSanPhamChiTiet(id)

}
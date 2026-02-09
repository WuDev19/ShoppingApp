package com.example.shoppingapp.domain.usecase.productusecase.get

import androidx.paging.PagingData
import com.example.shoppingapp.domain.model.product.ProductDisplay
import com.example.shoppingapp.domain.repository.product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSanPhamRelatedUseCase @Inject constructor(private val repo: ProductRepository) {

    operator fun invoke(id: Int): Flow<PagingData<ProductDisplay>> = repo.getSanPhamRelated(id)

}
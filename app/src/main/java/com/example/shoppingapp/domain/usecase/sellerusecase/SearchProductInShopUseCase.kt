package com.example.shoppingapp.domain.usecase.sellerusecase

import androidx.paging.PagingData
import com.example.shoppingapp.domain.model.Product
import com.example.shoppingapp.domain.repository.product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchProductInShopUseCase @Inject constructor(private val repo: ProductRepository) {

    operator fun invoke(id: Int, text: String): Flow<PagingData<Product>> = repo.timKiemSanPhamCuaShop(id, text)

}
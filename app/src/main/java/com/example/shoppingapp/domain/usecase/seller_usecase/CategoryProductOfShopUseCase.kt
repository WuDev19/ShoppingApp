package com.example.shoppingapp.domain.usecase.seller_usecase

import com.example.shoppingapp.domain.model.CategoryAndQuantity
import com.example.shoppingapp.domain.repository.category.CategoryRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryProductOfShopUseCase @Inject constructor(private val repo: CategoryRepository) {

    operator fun invoke(id: Int): Flow<StateListener<List<CategoryAndQuantity>>> = repo.getDanhMucAndSl(id)

}
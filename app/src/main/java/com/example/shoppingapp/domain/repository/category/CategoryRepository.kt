package com.example.shoppingapp.domain.repository.category

import com.example.shoppingapp.domain.model.CategoryAndQuantity
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    fun getDanhMucAndSl(id: Int) : Flow<StateListener<List<CategoryAndQuantity>>>

}
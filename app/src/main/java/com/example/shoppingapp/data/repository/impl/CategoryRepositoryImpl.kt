package com.example.shoppingapp.data.repository.impl

import com.example.shoppingapp.data.remote.api.CategoryAPI
import com.example.shoppingapp.data.remote.mapper.CategoryMapper
import com.example.shoppingapp.domain.model.CategoryAndQuantity
import com.example.shoppingapp.domain.repository.category.CategoryRepository
import com.example.shoppingapp.ui.state.StateListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(private val categoryAPI: CategoryAPI) : CategoryRepository {

    override fun getDanhMucAndSl(id: Int): Flow<StateListener<List<CategoryAndQuantity>>> = flow {
        val response = categoryAPI.getDanhMucVaSl(id)
        if(response.isSuccessful){
            val data = response.body()
            val listCategoryAndQuantity = data?.map {
                CategoryMapper.mapToCategory(it)
            } ?: emptyList()
            emit(StateListener.Success(listCategoryAndQuantity))
        }
        else{
            emit(StateListener.Error(Exception(response.message())))
        }
    }.flowOn(Dispatchers.IO)

}
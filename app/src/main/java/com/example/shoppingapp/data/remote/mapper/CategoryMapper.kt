package com.example.shoppingapp.data.remote.mapper

import com.example.shoppingapp.data.remote.dto.CategoryAndProductDTO
import com.example.shoppingapp.data.remote.dto.CategoryAndQuantityDTO
import com.example.shoppingapp.domain.model.CategoryAndQuantity
import com.example.shoppingapp.domain.model.Product

object CategoryMapper {

    fun mapToCategory(categoryAndQuantityDTO: CategoryAndQuantityDTO): CategoryAndQuantity {
        return CategoryAndQuantity(
            categoryAndQuantityDTO.tenDanhMuc,
            categoryAndQuantityDTO.sl
        )
    }

}
package com.example.shoppingapp.data.remote.mapper

import com.example.shoppingapp.data.remote.dto.get.CategoryAndQuantityDTO
import com.example.shoppingapp.domain.model.CategoryAndQuantity

object CategoryMapper {

    fun mapToCategory(categoryAndQuantityDTO: CategoryAndQuantityDTO): CategoryAndQuantity {
        return CategoryAndQuantity(
            categoryAndQuantityDTO.tenDanhMuc,
            categoryAndQuantityDTO.sl
        )
    }

}
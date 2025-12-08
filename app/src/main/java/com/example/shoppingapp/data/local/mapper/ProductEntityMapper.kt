package com.example.shoppingapp.data.local.mapper

import com.example.shoppingapp.data.local.entity.CategoryAndProductEntity
import com.example.shoppingapp.data.local.entity.ProductEntity
import com.example.shoppingapp.domain.model.product.ProductDisplay

object ProductEntityMapper {

    fun mapToProduct(productEntity: ProductEntity): ProductDisplay {
        return ProductDisplay(
            productEntity.id,
            productEntity.tenNguoiBan,
            productEntity.giaSp,
            productEntity.motaSp,
            productEntity.thumbnail,
            productEntity.giamgia,
            productEntity.numberBought,
            productEntity.maNguoiBan
        )
    }

    fun mapToProduct(categoryAndProductEntity: CategoryAndProductEntity): ProductDisplay {
        return ProductDisplay(
            categoryAndProductEntity.id,
            categoryAndProductEntity.tenNguoiBan,
            categoryAndProductEntity.giaSp,
            categoryAndProductEntity.motaSp,
            categoryAndProductEntity.thumbnail,
            categoryAndProductEntity.giamgia,
            categoryAndProductEntity.numberBought,
            categoryAndProductEntity.maNguoiBan
        )
    }

}
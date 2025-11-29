package com.example.shoppingapp.data.local.mapper

import com.example.shoppingapp.data.local.entity.ProductEntity
import com.example.shoppingapp.domain.model.Product

object ProductEntityMapper {

    fun mapToProduct(productEntity: ProductEntity): Product {
        return Product(
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

}
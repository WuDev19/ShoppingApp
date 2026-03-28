package com.example.shoppingapp.data.local.mapper

import com.example.shoppingapp.data.local.entity.CategoryAndProductEntity
import com.example.shoppingapp.data.local.entity.ProductDetailEntity
import com.example.shoppingapp.data.local.entity.ProductEntity
import com.example.shoppingapp.data.local.pojo.ProductDetailPojo
import com.example.shoppingapp.domain.model.product.ProductChiTiet
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

    fun mapToProductChiTiet(productDetailEntity: ProductDetailEntity): ProductChiTiet {
        return ProductChiTiet(
            productDetailEntity.maSanPhamDeTail,
            productDetailEntity.tenDetail,
            productDetailEntity.inStock,
            productDetailEntity.anhDetail,
            productDetailEntity.unitPrice,
            productDetailEntity.color,
            productDetailEntity.kichco,
            productDetailEntity.width,
            productDetailEntity.height,
            productDetailEntity.length,
            productDetailEntity.weight,
            productDetailEntity.maSp
        )
    }
    fun mapToProductChiTiet(productDetailPojo: ProductDetailPojo): ProductChiTiet {
        return ProductChiTiet(
            productDetailPojo.maSanPhamDeTail,
            productDetailPojo.tenDetail,
            productDetailPojo.inStock,
            productDetailPojo.anhDetail,
            productDetailPojo.unitPrice,
            productDetailPojo.color,
            productDetailPojo.kichco,
            productDetailPojo.width,
            productDetailPojo.height,
            productDetailPojo.length,
            productDetailPojo.weight,
            productDetailPojo.maSp
        )
    }

}
package com.example.shoppingapp.data.mapper

import com.example.shoppingapp.data.model.ProductChiTietDTO
import com.example.shoppingapp.data.model.ProductDTO
import com.example.shoppingapp.data.model.ProductDetailDTO
import com.example.shoppingapp.domain.model.Product
import com.example.shoppingapp.domain.model.ProductChiTiet
import com.example.shoppingapp.domain.model.ProductDetail

object ProductMapper {

    fun mapToProduct(productDTO: ProductDTO): Product {
        return Product(
            productDTO.maSp,
            productDTO.tenNguoiBan,
            productDTO.giaSp,
            productDTO.motaSp,
            productDTO.thumbnail,
            productDTO.giamgia,
            productDTO.numberBought,
            productDTO.maNguoiBan
        )
    }

    fun mapToProductDetail(productDetailDTO: ProductDetailDTO): ProductDetail{
        return ProductDetail(
            productDetailDTO.maSp,
            productDetailDTO.tenNguoiBan,
            productDetailDTO.maNguoiBan,
            productDetailDTO.avtShop,
            productDetailDTO.giaSp,
            productDetailDTO.motaSp,
            productDetailDTO.giamgia,
            productDetailDTO.numberBought,
            productDetailDTO.listAnhMoTa,
            productDetailDTO.totalProduct
        )
    }

    fun mapToProductChiTiet(productChiTietDTO: ProductChiTietDTO): ProductChiTiet {
        return ProductChiTiet(
            productChiTietDTO.maSanPhamDeTail,
            productChiTietDTO.tenDetail,
            productChiTietDTO.inStock,
            productChiTietDTO.anhDetail,
            productChiTietDTO.unitPrice,
            productChiTietDTO.color,
            productChiTietDTO.kichco,
            productChiTietDTO.maSp
        )
    }

}
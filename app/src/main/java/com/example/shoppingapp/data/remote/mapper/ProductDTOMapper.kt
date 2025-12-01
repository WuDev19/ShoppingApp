package com.example.shoppingapp.data.remote.mapper

import com.example.shoppingapp.data.remote.dto.CategoryAndProductDTO
import com.example.shoppingapp.data.remote.dto.ProductChiTietDTO
import com.example.shoppingapp.data.remote.dto.ProductDTO
import com.example.shoppingapp.data.remote.dto.ProductDetailDTO
import com.example.shoppingapp.domain.model.Product
import com.example.shoppingapp.domain.model.ProductChiTiet
import com.example.shoppingapp.domain.model.ProductDetail

object ProductDTOMapper {

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
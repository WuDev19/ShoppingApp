package com.example.shoppingapp.data.remote.mapper

import com.example.shoppingapp.data.remote.dto.get.ProductChiTietDTO
import com.example.shoppingapp.data.remote.dto.get.ProductDTO
import com.example.shoppingapp.data.remote.dto.get.ProductDetailDTO
import com.example.shoppingapp.domain.model.product.ProductDisplay
import com.example.shoppingapp.domain.model.product.ProductChiTiet
import com.example.shoppingapp.domain.model.product.ProductDetail

object ProductDTOMapper {

    fun mapToProduct(productDTO: ProductDTO): ProductDisplay {
        return ProductDisplay(
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

    fun mapToProductDetail(productDetailDTO: ProductDetailDTO): ProductDetail {
        return ProductDetail(
            productDetailDTO.maSp,
            productDetailDTO.nguoiBanDTO?.tenShop,
            productDetailDTO.nguoiBanDTO?.maNguoiBan,
            productDetailDTO.nguoiBanDTO?.avtShop,
            productDetailDTO.nguoiBanDTO?.provinceId,
            productDetailDTO.nguoiBanDTO?.districtId,
            productDetailDTO.nguoiBanDTO?.wardCode,
            productDetailDTO.nguoiBanDTO?.shopGhnId,
            productDetailDTO.nguoiBanDTO?.diaChi,
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
            productChiTietDTO.width,
            productChiTietDTO.height,
            productChiTietDTO.length,
            productChiTietDTO.weight,
            productChiTietDTO.maSp,
        )
    }

}
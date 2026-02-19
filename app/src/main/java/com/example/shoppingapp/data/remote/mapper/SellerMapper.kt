package com.example.shoppingapp.data.remote.mapper

import com.example.shoppingapp.data.remote.dto.get.SellerDTO
import com.example.shoppingapp.domain.model.SellerInStore
import com.example.shoppingapp.domain.model.SellerSetting

object SellerMapper {

    fun mapToSellerInStore(sellerDTO: SellerDTO): SellerInStore {
        return SellerInStore(
            sellerDTO.maNguoiBan,
            sellerDTO.avtShop,
            sellerDTO.tenShop,
            sellerDTO.sanphams?.size ?: 0
        )
    }

    fun mapToSellerSetting(sellerDTO: SellerDTO): SellerSetting {
        return SellerSetting(
            sellerDTO.avtShop,
            sellerDTO.tenShop,
            sellerDTO.diaChi,
            sellerDTO.sdtNguoiBan
        )
    }

}
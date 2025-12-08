package com.example.shoppingapp.data.remote.mapper

import com.example.shoppingapp.data.remote.dto.get.SellerDTO
import com.example.shoppingapp.domain.model.SellerInStore

object SellerMapper {

    fun mapToSellerInStore(sellerDTO: SellerDTO): SellerInStore{
        return SellerInStore(sellerDTO.maNguoiBan, sellerDTO.avtShop, sellerDTO.tenShop, sellerDTO.sanphams?.size ?: 0)
    }

}
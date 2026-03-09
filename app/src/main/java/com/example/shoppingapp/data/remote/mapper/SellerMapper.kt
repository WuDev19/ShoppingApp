package com.example.shoppingapp.data.remote.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.shoppingapp.data.remote.dto.get.SellerDTO
import com.example.shoppingapp.data.remote.dto.get.ShopVouchersDTO
import com.example.shoppingapp.domain.model.SellerInStore
import com.example.shoppingapp.domain.model.SellerSetting
import com.example.shoppingapp.domain.model.ShopVouchers
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun mapToShopVouchers(shopVouchersDTO: ShopVouchersDTO): ShopVouchers {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val endTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(shopVouchersDTO.endTime),
            ZoneId.systemDefault()
        ).format(formatter)
        return ShopVouchers(
            shopVouchersDTO.voucherId,
            shopVouchersDTO.voucherCode,
            shopVouchersDTO.voucherDescription,
            shopVouchersDTO.voucherType,
            shopVouchersDTO.sellerId,
            shopVouchersDTO.shopGhnId,
            shopVouchersDTO.minOrderCost,
            shopVouchersDTO.voucherValue,
            shopVouchersDTO.maxDiscountValue,
            shopVouchersDTO.usageLimit,
            shopVouchersDTO.usageCount,
            shopVouchersDTO.startTime,
            endTime.toString(),
            shopVouchersDTO.active,
            false
        )
    }

}
package com.example.shoppingapp.domain.model

import android.os.Parcelable
import com.example.shoppingapp.domain.model.product.ProductChiTiet
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val cartItemId: Int,
    val maGioHang: Int,
    val detailProduct: ProductChiTiet,
    val soLuong: Int,
    val giamGia: Int,
    val cartId: Int,
) : CartUiModel(), Parcelable
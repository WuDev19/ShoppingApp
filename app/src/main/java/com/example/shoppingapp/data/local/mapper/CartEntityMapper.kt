package com.example.shoppingapp.data.local.mapper

import com.example.shoppingapp.data.local.entity.CartEntity
import com.example.shoppingapp.data.local.entity.CartItemEntity
import com.example.shoppingapp.domain.model.Cart
import com.example.shoppingapp.domain.model.CartItem
import com.example.shoppingapp.domain.model.ProductChiTiet

object CartEntityMapper {

    fun mapToCartDomain(cartEntity: CartEntity): Cart {
        return Cart(
            cartEntity.cartId,
            cartEntity.maND,
            cartEntity.tenND,
            cartEntity.maNB,
            cartEntity.tenShop,
            cartEntity.updateTime
        )
    }

    fun mapCartItemEntityToCartItem(cartItemEntity: CartItemEntity): CartItem {
        return CartItem(
            cartItemEntity.cartItemId,
            cartItemEntity.maGioHang,
            ProductChiTiet(
                cartItemEntity.maSanPhamDeTail,
                cartItemEntity.tenDetail,
                cartItemEntity.inStock,
                cartItemEntity.anhDetail,
                cartItemEntity.unitPrice,
                cartItemEntity.color,
                cartItemEntity.kichco,
                cartItemEntity.maGioHang
            ),
            cartItemEntity.soLuong,
            cartItemEntity.giamGia
        )
    }

}
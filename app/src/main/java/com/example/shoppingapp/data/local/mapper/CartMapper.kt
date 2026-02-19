package com.example.shoppingapp.data.local.mapper

import com.example.shoppingapp.data.local.entity.CartEntity
import com.example.shoppingapp.data.local.entity.CartItemEntity
import com.example.shoppingapp.data.remote.dto.get.CartDTO
import com.example.shoppingapp.data.remote.dto.get.CartItemDTO
import com.example.shoppingapp.domain.model.Cart
import com.example.shoppingapp.domain.model.CartItem
import com.example.shoppingapp.domain.model.product.ProductChiTiet

object CartMapper {

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

    fun mapToCartEntity(cartDTO: CartDTO) : CartEntity{
        return CartEntity(
            cartDTO.cartId!!,
            cartDTO.maND!!,
            cartDTO.tenND!!,
            cartDTO.maNB!!,
            cartDTO.tenShop!!,
            cartDTO.updateTime!!
        )
    }

    fun mapToCartItemEntity(cartItemDTO: CartItemDTO, cartId: Int): CartItemEntity{
        return CartItemEntity(
            cartItemDTO.cartItemId!!,
            cartItemDTO.maGioHang!!,
            cartItemDTO.detailProduct!!.maSanPhamDeTail!!,
            cartItemDTO.detailProduct.tenDetail!!,
            cartItemDTO.detailProduct.inStock!!,
            cartItemDTO.detailProduct.anhDetail!!,
            cartItemDTO.detailProduct.unitPrice!!,
            cartItemDTO.detailProduct.color!!,
            cartItemDTO.detailProduct.kichco!!,
            cartItemDTO.detailProduct.maSp!!,
            cartItemDTO.soLuong!!,
            cartItemDTO.giamGia!!,
            cartId
        )
    }
}
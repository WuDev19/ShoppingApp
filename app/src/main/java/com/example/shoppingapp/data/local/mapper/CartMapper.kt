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
            cartEntity.avtShop,
            cartEntity.provinceIdShop,
            cartEntity.districtIdShop,
            cartEntity.wardCodeShop,
            cartEntity.shopGhnId,
            cartEntity.updateTime,
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
                cartItemEntity.width,
                cartItemEntity.height,
                cartItemEntity.length,
                cartItemEntity.weight,
                cartItemEntity.maGioHang
            ),
            cartItemEntity.soLuong,
            cartItemEntity.giamGia,
            cartItemEntity.cartId,
        )
    }

    fun mapToCartEntity(cartDTO: CartDTO): CartEntity {
        return CartEntity(
            cartDTO.cartId!!,
            cartDTO.maND!!,
            cartDTO.tenND!!,
            cartDTO.maNB!!,
            cartDTO.tenShop!!,
            cartDTO.avtShop!!,
            cartDTO.provinceIdShop!!,
            cartDTO.districtIdShop!!,
            cartDTO.wardCodeShop!!,
            cartDTO.shopGhnId!!,
            cartDTO.updateTime!!,
        )
    }

    fun mapToCartItemEntity(cartItemDTO: CartItemDTO): CartItemEntity {
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
            cartItemDTO.detailProduct.width!!,
            cartItemDTO.detailProduct.height!!,
            cartItemDTO.detailProduct.length!!,
            cartItemDTO.detailProduct.weight!!,
            cartItemDTO.detailProduct.maSp!!,
            cartItemDTO.soLuong!!,
            cartItemDTO.giamGia!!,
            cartItemDTO.cartId!!,
            cartItemDTO.updateTime!!
        )
    }
}
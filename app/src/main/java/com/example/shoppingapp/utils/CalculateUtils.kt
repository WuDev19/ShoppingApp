package com.example.shoppingapp.utils

import android.util.Log
import com.example.shoppingapp.domain.model.CartItem
import com.example.shoppingapp.domain.model.OrderFromBuyNow
import com.example.shoppingapp.domain.model.OrderFromCart

object CalculateUtils {
    fun calcDiscount(discount: Int, price: Int): Int {
        val deduct = price * discount / 100
        return price - deduct
    }

    fun calcTotalPrice(cartItems: List<CartItem>): Int =
        cartItems.sumOf { cartItem ->
            calcDiscount(
                cartItem.giamGia,
                cartItem.detailProduct.unitPrice!!
            ) * cartItem.soLuong
        }

    fun calcTotalPrice(orderFromBuyNow: OrderFromBuyNow): Int {
        Log.e(StringConstants.DEBUG, "calcTotalPrice: $orderFromBuyNow")
        return calcDiscount(
            orderFromBuyNow.discount,
            orderFromBuyNow.productChiTiet.unitPrice!!
        ) * orderFromBuyNow.number
    }

    fun calcTotalPriceFromCart(orderFromCarts: List<OrderFromCart>): Int {
        return orderFromCarts.sumOf { fromCart ->
            fromCart.cartItems.sumOf { cartItem ->
                calcDiscount(
                    cartItem.giamGia,
                    cartItem.detailProduct.unitPrice!!
                ) * cartItem.soLuong
            }
        }
    }

}
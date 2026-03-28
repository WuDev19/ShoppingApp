package com.example.shoppingapp.domain.model

sealed class OrderUiModel {

    data class OrderOfShop(
        val shopId: Int,
        val shopName: String,
        val shopAvt: String,
    ) : OrderUiModel()

    data class Order(
        val orderId: String,
        val orderStatus: String,
    ) : OrderUiModel()

    data class OrderItemUser(
        val orderOfShop: OrderOfShop,
        val order: Order,
        val orderItemDetail: OrderItemDetail
    ) : OrderUiModel()

    data class OrderReview(
        val orderId: String,
        val orderStatus: String,
    ) : OrderUiModel()
}
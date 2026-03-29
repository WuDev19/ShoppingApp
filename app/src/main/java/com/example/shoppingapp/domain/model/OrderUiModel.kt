package com.example.shoppingapp.domain.model

sealed class OrderUiModel {

    data class OrderOfShop(
        val shopId: Int,
        val shopGhnId: Int,
        val shopName: String,
        val shopAvt: String,
    ) : OrderUiModel()

    data class Order(
        val orderId: String,
        val ghnOrderId: String,
        val orderStatus: String,
    ) : OrderUiModel()

    data class OrderItemUser(
        val orderOfShop: OrderOfShop,
        val order: Order,
        val orderItemDetail: OrderItemDetail
    ) : OrderUiModel()

    data class OrderReviewOrCancel(
        val shopId: Int,
        val shopGhnId: Int,
        val order: Order
    ) : OrderUiModel()
}
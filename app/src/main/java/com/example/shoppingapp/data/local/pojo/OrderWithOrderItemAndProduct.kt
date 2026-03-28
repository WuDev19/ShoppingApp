package com.example.shoppingapp.data.local.pojo

import androidx.room.Embedded

data class OrderWithOrderItemAndProduct(
    @Embedded
    val orderPojo: OrderPojo,
    @Embedded
    val orderItemPojo: OrderItemPojo,
    @Embedded
    val productDetailPojo: ProductDetailPojo
)
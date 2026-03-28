package com.example.shoppingapp.data.local.mapper

import com.example.shoppingapp.data.local.pojo.OrderItemPojo
import com.example.shoppingapp.data.local.pojo.ProductDetailPojo
import com.example.shoppingapp.domain.model.OrderItemDetail

object OrderMapper {

    fun mapToOrderItemDetail(
        orderItemPojo: OrderItemPojo,
        productDetailPojo: ProductDetailPojo
    ): OrderItemDetail {
        return OrderItemDetail(
            orderItemPojo.orderItemId,
            ProductEntityMapper.mapToProductChiTiet(productDetailPojo),
            orderItemPojo.number,
            orderItemPojo.price
        )
    }

}
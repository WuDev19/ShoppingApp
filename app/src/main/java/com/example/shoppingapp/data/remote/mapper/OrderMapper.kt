package com.example.shoppingapp.data.remote.mapper

import com.example.shoppingapp.data.remote.dto.get.OrderDetailDTO
import com.example.shoppingapp.data.remote.dto.get.OrderItemDetailDTO
import com.example.shoppingapp.domain.model.OrderDetail
import com.example.shoppingapp.domain.model.OrderItemDetail
import com.example.shoppingapp.domain.model.OrderStatus

object OrderMapper {
    fun mapToOrderStatus(orderDetailDTO: OrderDetailDTO): OrderStatus {
        return OrderStatus(
            orderDetailDTO.orderId,
            orderDetailDTO.orderStatus,
            orderDetailDTO.ngayDat,
            orderDetailDTO.ngayDuKienToi
        )
    }

    fun mapToOrderItemDetail(orderItemDetailDTO: OrderItemDetailDTO): OrderItemDetail {
        return OrderItemDetail(
            orderItemDetailDTO.orderItemId,
            ProductDTOMapper.mapToProductChiTiet(orderItemDetailDTO.detailProduct),
            orderItemDetailDTO.number,
            orderItemDetailDTO.price
        )
    }

    fun mapToOrderDetail(orderDetailDTO: OrderDetailDTO): OrderDetail {
        return OrderDetail(
            orderDetailDTO.orderId,
            orderDetailDTO.orderStatus,
            orderDetailDTO.shippingMethod,
            orderDetailDTO.paymentMethod,
            orderDetailDTO.userName,
            orderDetailDTO.userPhone,
            orderDetailDTO.address,
            orderDetailDTO.shopName,
            orderDetailDTO.shopAvt,
            orderDetailDTO.totalAmount,
            orderDetailDTO.ngayDat,
            orderDetailDTO.ngayDuKienToi,
            orderDetailDTO.ghnOrderId,
            orderDetailDTO.userNote,
            orderDetailDTO.orderItems.map { orderItemDetailDTO ->
                mapToOrderItemDetail(orderItemDetailDTO)
            }
        )
    }

}
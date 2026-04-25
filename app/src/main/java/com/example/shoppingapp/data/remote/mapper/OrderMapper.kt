package com.example.shoppingapp.data.remote.mapper

import com.example.shoppingapp.data.local.entity.OrderEntity
import com.example.shoppingapp.data.local.entity.OrderItemEntity
import com.example.shoppingapp.data.local.entity.ProductDetailEntity
import com.example.shoppingapp.data.remote.dto.get.CancelOrderDTO
import com.example.shoppingapp.data.remote.dto.get.OrderDetailDTO
import com.example.shoppingapp.data.remote.dto.get.OrderItemDetailDTO
import com.example.shoppingapp.data.remote.dto.get.OrderStatusDTO
import com.example.shoppingapp.data.remote.dto.get.ProductChiTietDTO
import com.example.shoppingapp.domain.model.CancelOrder
import com.example.shoppingapp.domain.model.OrderDetail
import com.example.shoppingapp.domain.model.OrderItemDetail
import com.example.shoppingapp.domain.model.OrderStatus
import com.example.shoppingapp.domain.model.user.OrderUpdateStatus

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
            orderDetailDTO.userId,
            orderDetailDTO.userName,
            orderDetailDTO.userPhone,
            orderDetailDTO.address,
            orderDetailDTO.shopId,
            orderDetailDTO.shopGhnId,
            orderDetailDTO.shopName,
            orderDetailDTO.shopAvt,
            orderDetailDTO.totalAmount,
            orderDetailDTO.ngayDat,
            orderDetailDTO.ngayDuKienToi,
            orderDetailDTO.ngayHoanThanh,
            orderDetailDTO.ghnOrderId,
            orderDetailDTO.userNote,
            orderDetailDTO.orderItems.map { orderItemDetailDTO ->
                mapToOrderItemDetail(orderItemDetailDTO)
            }
        )
    }

    fun mapToOrderEntity(orderDetailDTO: OrderDetailDTO): OrderEntity {
        return OrderEntity(
            orderDetailDTO.orderId,
            orderDetailDTO.orderStatus,
            orderDetailDTO.shippingMethod,
            orderDetailDTO.paymentMethod,
            orderDetailDTO.userId,
            orderDetailDTO.userName,
            orderDetailDTO.userPhone,
            orderDetailDTO.address,
            orderDetailDTO.shopId,
            orderDetailDTO.shopGhnId,
            orderDetailDTO.shopName,
            orderDetailDTO.shopAvt,
            orderDetailDTO.totalAmount,
            orderDetailDTO.ngayDat,
            orderDetailDTO.ngayDuKienToi,
            orderDetailDTO.ngayHoanThanh,
            orderDetailDTO.ngayChinhSua,
            orderDetailDTO.ghnOrderId,
            orderDetailDTO.userNote
        )
    }

    fun mapToOrderItemEntity(
        orderId: String,
        orderItemDetailDTO: OrderItemDetailDTO
    ): OrderItemEntity {
        return OrderItemEntity(
            orderItemDetailDTO.orderItemId,
            orderItemDetailDTO.detailProduct.maSanPhamDeTail!!,
            orderItemDetailDTO.number,
            orderItemDetailDTO.price,
            orderId
        )
    }

    fun mapToProductDetailEntity(productChiTietDTO: ProductChiTietDTO): ProductDetailEntity {
        return ProductDetailEntity(
            productChiTietDTO.maSanPhamDeTail!!,
            productChiTietDTO.tenDetail!!,
            productChiTietDTO.inStock!!,
            productChiTietDTO.anhDetail!!,
            productChiTietDTO.unitPrice!!,
            productChiTietDTO.color!!,
            productChiTietDTO.kichco!!,
            productChiTietDTO.width!!,
            productChiTietDTO.height!!,
            productChiTietDTO.length!!,
            productChiTietDTO.weight!!,
            productChiTietDTO.maSp!!
        )
    }

    fun mapToCancelOrder(cancelOrderDTO: CancelOrderDTO): CancelOrder =
        CancelOrder(cancelOrderDTO.orderId, cancelOrderDTO.orderGhnCode)

    fun mapToOrderUpdateStatus(orderStatusDTO: OrderStatusDTO): OrderUpdateStatus {
        return OrderUpdateStatus(orderStatusDTO.orderId, orderStatusDTO.status)
    }
}
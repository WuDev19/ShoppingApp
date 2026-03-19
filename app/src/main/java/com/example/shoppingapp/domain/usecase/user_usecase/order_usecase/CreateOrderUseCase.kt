package com.example.shoppingapp.domain.usecase.user_usecase.order_usecase

import com.example.shoppingapp.data.remote.dto.request.OrderDetailRequest
import com.example.shoppingapp.data.remote.dto.request.OrderRequest
import com.example.shoppingapp.data.remote.dto.request.ghn.CreateOrderGhnRequest
import com.example.shoppingapp.data.remote.dto.request.ghn.CreateOrderItemGhnRequest
import com.example.shoppingapp.domain.model.AddressItem
import com.example.shoppingapp.domain.model.FinalPrice
import com.example.shoppingapp.domain.model.Order
import com.example.shoppingapp.domain.model.OrderFromBuyNow
import com.example.shoppingapp.domain.model.OrderFromCart
import com.example.shoppingapp.domain.model.VoucherOrder
import com.example.shoppingapp.domain.model.ghn.ShippingMethod
import com.example.shoppingapp.domain.repository.user.UserRepository
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.utils.CalculateUtils
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(
    private val repo: UserRepository,
    private val auth: FirebaseAuth,
) {
    suspend operator fun invoke(
        shopGhnId: Int,
        orderFromBuyNow: OrderFromBuyNow? = null,
        orderFromCart: OrderFromCart? = null,
        addressItem: AddressItem,
        shippingMethods: Map<Int, ShippingMethod>,
        voucherOrders: Map<Int, VoucherOrder>,
        finalPrices: Map<Int, FinalPrice>,
        note: Map<Int, String>,
        paymentMethod: String,
    ): StateListener<Order> {
        val shippingMethod = shippingMethods[shopGhnId]!!
        val voucherOrder = voucherOrders[shopGhnId]
        var unitPrice: Long
        var insuranceValue: Long
        var totalAmount: Long
        val finalPrice = finalPrices[shopGhnId]
        if (orderFromBuyNow != null) {
            val productChiTiet = orderFromBuyNow.productChiTiet
            val number = orderFromBuyNow.number
            if (finalPrice != null) {
                totalAmount = finalPrice.finalPrice //tổng tiền tính cả tiền ship
                unitPrice = CalculateUtils.calcDiscount(
                    orderFromBuyNow.discount,
                    productChiTiet.unitPrice!!
                ) //giá chưa áp voucher
                insuranceValue =
                    finalPrice.priceWithoutShip //giá sản phẩm khi áp voucher nhưng ko tính ship
            } else {
                unitPrice = CalculateUtils.calcDiscount(
                    orderFromBuyNow.discount,
                    productChiTiet.unitPrice!!
                )
                totalAmount =
                    CalculateUtils.calcTotalPrice(orderFromBuyNow) + shippingMethod.feeServiceGhn.totalFee
                insuranceValue = totalAmount - shippingMethod.feeServiceGhn.totalFee
            }
            val createOrderItemGhnRequest = CreateOrderItemGhnRequest(
                productChiTiet.tenDetail!!,
                number,
                unitPrice,
                productChiTiet.weight!!,
                productChiTiet.length!!,
                productChiTiet.width!!,
                productChiTiet.height!!
            )
            val createOrderGhnRequest = CreateOrderGhnRequest(
                addressItem.name,
                addressItem.sdt,
                addressItem.addressName,
                addressItem.wardCode,
                addressItem.districtId,
                productChiTiet.weight!!,
                shippingMethod.serviceId,
                shippingMethod.serviceTypeId,
                2,
                if (insuranceValue > 5000000) 5000000 else insuranceValue,
                if (insuranceValue > 50000000) 50000000 else insuranceValue,
                note[shopGhnId] ?: "",
                "KHONGCHOXEMHANG",
                listOf(createOrderItemGhnRequest)
            )
            val orderDetailRequest = OrderDetailRequest(
                productChiTiet.maSanPhamDeTail!!,
                number,
                unitPrice
            )
            val orderRequest = OrderRequest(
                auth.currentUser!!.uid,
                orderFromBuyNow.sellerId,
                shippingMethod.feeServiceGhn.serviceName,
                paymentMethod,
                totalAmount,
                voucherOrder?.voucherId ?: -1,
                note[shopGhnId] ?: "",
                createOrderGhnRequest,
                listOf(orderDetailRequest)
            )
            return repo.createOrder(shopGhnId, orderRequest)
        } else {
            val cart = orderFromCart!!.cart
            var totalWeight = 0
            val listCreateOrderItemGhnRequest = mutableListOf<CreateOrderItemGhnRequest>()
            val listOrderDetailRequest = mutableListOf<OrderDetailRequest>()
            if (finalPrice != null) {
                totalAmount = finalPrice.finalPrice
                insuranceValue = finalPrice.priceWithoutShip
            } else {
                totalAmount =
                    CalculateUtils.calcTotalPrice(orderFromCart.cartItems) + shippingMethod.feeServiceGhn.totalFee
                insuranceValue = totalAmount - shippingMethod.feeServiceGhn.totalFee
            }
            orderFromCart.cartItems.forEach { cartItem ->
                val productChiTiet = cartItem.detailProduct
                val number = cartItem.soLuong
                unitPrice = CalculateUtils.calcDiscount(
                    cartItem.giamGia,
                    cartItem.detailProduct.unitPrice!!
                )
                val createOrderItemGhnRequest = CreateOrderItemGhnRequest(
                    productChiTiet.tenDetail!!,
                    number,
                    unitPrice,
                    productChiTiet.weight!!,
                    productChiTiet.length!!,
                    productChiTiet.width!!,
                    productChiTiet.height!!
                )
                listCreateOrderItemGhnRequest.add(createOrderItemGhnRequest)
                val orderDetailRequest = OrderDetailRequest(
                    productChiTiet.maSanPhamDeTail!!,
                    number,
                    unitPrice
                )
                listOrderDetailRequest.add(orderDetailRequest)
                totalWeight += cartItem.detailProduct.weight!!
            }
            val createOrderGhnRequest = CreateOrderGhnRequest(
                addressItem.name,
                addressItem.sdt,
                addressItem.addressName,
                addressItem.wardCode,
                addressItem.districtId,
                totalWeight,
                shippingMethod.serviceId,
                shippingMethod.serviceTypeId,
                2,
                if (insuranceValue > 5000000) 5000000 else insuranceValue,
                if (insuranceValue > 50000000) 50000000 else insuranceValue,
                note[shopGhnId] ?: "",
                "KHONGCHOXEMHANG",
                listCreateOrderItemGhnRequest
            )
            val orderRequest = OrderRequest(
                auth.currentUser!!.uid,
                cart.maNB,
                shippingMethod.feeServiceGhn.serviceName,
                paymentMethod,
                totalAmount,
                voucherOrder?.voucherId ?: -1,
                note[shopGhnId] ?: "",
                createOrderGhnRequest,
                listOrderDetailRequest
            )
            return repo.createOrder(shopGhnId, orderRequest)
        }
    }

}
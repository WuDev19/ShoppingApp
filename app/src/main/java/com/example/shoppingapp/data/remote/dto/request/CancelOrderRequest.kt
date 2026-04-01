package com.example.shoppingapp.data.remote.dto.request

import com.example.shoppingapp.data.remote.dto.request.ghn.CancelOrderGhnRequest

data class CancelOrderRequest(
    val shopGhnId: Int,
    val cancelTime: Long,
    val cancelOrderGhnRequest: CancelOrderGhnRequest
)

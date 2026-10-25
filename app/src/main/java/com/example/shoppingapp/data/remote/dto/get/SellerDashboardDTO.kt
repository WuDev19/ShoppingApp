package com.example.shoppingapp.data.remote.dto.get

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SellerDashboardDTO(
    val sellerId: Int, val fromDate: String, val toDate: String,
    val totalOrders: Long, val completedOrders: Long, val completedOrderValue: Long,
    val onlinePaidAmount: Long, val completedCodOrderValue: Long, val unitsSold: Long,
    val undatedCompletedOrders: Long, val totalProducts: Long, val totalStock: Long,
    val outOfStockVariants: Long, val ordersByStatus: List<DashboardOrderStatusDTO>,
    val dailySales: List<DashboardDailySalesDTO>, val topProducts: List<DashboardTopProductDTO>
)
@JsonClass(generateAdapter = true)
data class DashboardOrderStatusDTO(val status: String, val orderCount: Long)
@JsonClass(generateAdapter = true)
data class DashboardDailySalesDTO(val date: String, val completedOrderValue: Long, val onlinePaidAmount: Long, val unitsSold: Long)
@JsonClass(generateAdapter = true)
data class DashboardTopProductDTO(val productId: Int, val description: String?, val thumbnail: String?, val unitsSold: Long)
@JsonClass(generateAdapter = true)
data class SellerMonthlyStatsDTO(val sellerId: Int, val year: Int, val undatedCompletedOrders: Long, val months: List<DashboardMonthDTO>)
@JsonClass(generateAdapter = true)
data class DashboardMonthDTO(val month: Int, val completedOrderValue: Long, val onlinePaidAmount: Long, val unitsSold: Long)

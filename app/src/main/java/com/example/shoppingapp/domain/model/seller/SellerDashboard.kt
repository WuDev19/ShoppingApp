package com.example.shoppingapp.domain.model.seller

data class SellerDashboard(
    val sellerId: Int, val fromDate: String, val toDate: String,
    val totalOrders: Long, val completedOrders: Long, val completedOrderValue: Long,
    val onlinePaidAmount: Long, val completedCodOrderValue: Long, val unitsSold: Long,
    val undatedCompletedOrders: Long, val totalProducts: Long, val totalStock: Long,
    val outOfStockVariants: Long, val ordersByStatus: List<DashboardOrderStatus>,
    val dailySales: List<DashboardDailySales>, val topProducts: List<DashboardTopProduct>
)
data class DashboardOrderStatus(val status: String, val orderCount: Long)
data class DashboardDailySales(val date: String, val completedOrderValue: Long, val onlinePaidAmount: Long, val unitsSold: Long)
data class DashboardTopProduct(val productId: Int, val description: String?, val thumbnail: String?, val unitsSold: Long)
data class SellerMonthlyStats(val sellerId: Int, val year: Int, val undatedCompletedOrders: Long, val months: List<DashboardMonth>)
data class DashboardMonth(val month: Int, val completedOrderValue: Long, val onlinePaidAmount: Long, val unitsSold: Long)

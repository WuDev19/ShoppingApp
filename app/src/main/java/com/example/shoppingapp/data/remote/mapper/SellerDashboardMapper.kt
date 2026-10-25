package com.example.shoppingapp.data.remote.mapper

import com.example.shoppingapp.data.remote.dto.get.SellerDashboardDTO
import com.example.shoppingapp.data.remote.dto.get.SellerMonthlyStatsDTO
import com.example.shoppingapp.domain.model.seller.DashboardDailySales
import com.example.shoppingapp.domain.model.seller.DashboardMonth
import com.example.shoppingapp.domain.model.seller.DashboardOrderStatus
import com.example.shoppingapp.domain.model.seller.DashboardTopProduct
import com.example.shoppingapp.domain.model.seller.SellerDashboard
import com.example.shoppingapp.domain.model.seller.SellerMonthlyStats

fun SellerDashboardDTO.toDomain() = SellerDashboard(
    sellerId, fromDate, toDate, totalOrders, completedOrders, completedOrderValue,
    onlinePaidAmount, completedCodOrderValue, unitsSold, undatedCompletedOrders,
    totalProducts, totalStock, outOfStockVariants,
    ordersByStatus.map { DashboardOrderStatus(it.status, it.orderCount) },
    dailySales.map { DashboardDailySales(it.date, it.completedOrderValue, it.onlinePaidAmount, it.unitsSold) },
    topProducts.map { DashboardTopProduct(it.productId, it.description, it.thumbnail, it.unitsSold) }
)
fun SellerMonthlyStatsDTO.toDomain() = SellerMonthlyStats(sellerId, year, undatedCompletedOrders,
    months.map { DashboardMonth(it.month, it.completedOrderValue, it.onlinePaidAmount, it.unitsSold) })

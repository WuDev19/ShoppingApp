package com.example.shoppingapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.shoppingapp.data.local.entity.OrderEntity
import com.example.shoppingapp.data.local.pojo.OrderWithOrderItemAndProduct

@Dao
interface OrderDAO {

    @Query("""
        SELECT * FROM orders od
        JOIN order_item oi ON od.orderId = oi.orderId
        JOIN product_details pd ON pd.maSanPhamDeTail = oi.maSanPhamDeTail
        WHERE od.userId = :userId 
        AND od.orderStatus = :status 
        ORDER BY od.shopId, od.ngayDuKienToi DESC
    """)
    fun getPagingSourceOrderViaStatus(
        userId: String,
        status: String
    ): PagingSource<Int, OrderWithOrderItemAndProduct>

    @Query("""
        SELECT * FROM orders od
        JOIN order_item oi ON od.orderId = oi.orderId
        JOIN product_details pd ON pd.maSanPhamDeTail = oi.maSanPhamDeTail
        WHERE od.userId = :userId 
        AND od.orderStatus = :status 
        ORDER BY od.shopId, od.ngayChinhSua DESC
    """)
    fun getPagingSourceOrderViaStatusCancel(
        userId: String,
        status: String
    ): PagingSource<Int, OrderWithOrderItemAndProduct>

    @Query("""
        SELECT * FROM orders od
        JOIN order_item oi ON od.orderId = oi.orderId
        JOIN product_details pd ON pd.maSanPhamDeTail = oi.maSanPhamDeTail
        WHERE od.userId = :userId 
        AND od.orderStatus = :status 
        AND pd.tenDetail LIKE :orderName || "%" COLLATE NOCASE
        ORDER BY od.shopId, od.ngayChinhSua DESC
    """)
    fun searchOrderCancelPagingSource(
        userId: String,
        status: String,
        orderName: String
    ): PagingSource<Int, OrderWithOrderItemAndProduct>

    @Query("""
        SELECT * FROM orders od
        JOIN order_item oi ON od.orderId = oi.orderId
        JOIN product_details pd ON pd.maSanPhamDeTail = oi.maSanPhamDeTail
        WHERE od.userId = :userId 
        AND od.orderStatus = :status 
        AND pd.tenDetail LIKE :orderName || "%" COLLATE NOCASE
        ORDER BY od.shopId, od.ngayDuKienToi DESC
    """)
    fun searchOrderPagingSource(
        userId: String,
        status: String,
        orderName: String
    ): PagingSource<Int, OrderWithOrderItemAndProduct>

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(orderEntities: List<OrderEntity>)

    @Query("DELETE FROM orders WHERE userId = :userId AND orderStatus = :status")
    suspend fun deleteByStatus(userId: String, status: String)

}
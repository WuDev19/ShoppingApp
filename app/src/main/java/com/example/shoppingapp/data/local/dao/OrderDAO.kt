package com.example.shoppingapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
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
        ORDER BY od.ngayDuKienToi DESC
    """)
    fun getPagingSourceOrderViaStatus(
        userId: String,
        status: String
    ): PagingSource<Int, OrderWithOrderItemAndProduct>

    @Insert
    suspend fun insertAll(orderEntities: List<OrderEntity>)

    @Query("DELETE FROM orders")
    suspend fun deleteAll()

}
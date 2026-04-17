package com.example.shoppingapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.shoppingapp.data.local.entity.OrderItemEntity

@Dao
interface OrderItemDAO {

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(orderItemEntities: List<OrderItemEntity>)

}
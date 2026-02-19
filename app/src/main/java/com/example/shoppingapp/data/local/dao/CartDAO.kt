package com.example.shoppingapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.shoppingapp.data.local.entity.CartEntity
import com.example.shoppingapp.domain.model.CartItemInCart

@Dao
interface CartDAO {

    @Transaction
    @Query("SELECT * FROM carts WHERE maND = :id ORDER BY updateTime DESC")
    fun getPagingSourceCartUser(id: String): PagingSource<Int, CartItemInCart>

    @Query("DELETE FROM carts WHERE maND = :id")
    suspend fun clearCartUser(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cart: CartEntity)

}
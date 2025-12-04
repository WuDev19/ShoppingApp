package com.example.shoppingapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shoppingapp.data.local.entity.CartEntity

interface CartDAO {

    @Query("SELECT * FROM carts WHERE maND = :id ORDER BY updateTime DESC")
    fun getCartUser(id: Int): PagingSource<Int, CartEntity>

    @Query("DELETE FROM carts WHERE maND = :id")
    fun clearCartUser(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(carts: List<CartEntity>)

}
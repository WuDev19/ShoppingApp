package com.example.shoppingapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shoppingapp.data.local.entity.CartItemEntity

@Dao
interface CartItemDAO {

    @Query("SELECT * FROM cart_items WHERE maGioHang = :idCart")
    fun pagingSourceCartItemInCartOfUser(idCart: Int): PagingSource<Int, CartItemEntity>

    @Query("DELETE FROM cart_items WHERE maGioHang = :idCart")
    suspend fun clearCartItemInCartOfUser(idCart: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartItems: List<CartItemEntity>)

}
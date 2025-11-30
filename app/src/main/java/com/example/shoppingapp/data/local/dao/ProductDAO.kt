package com.example.shoppingapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shoppingapp.data.local.entity.ProductEntity

@Dao
interface ProductDAO {

    @Query("SELECT * FROM products WHERE maNguoiBan = :id AND motaSp LIKE '%' || :text || '%'")
    fun getPagingSource(id: Int, text: String): PagingSource<Int, ProductEntity>

    @Query("SELECT * FROM products WHERE maNguoiBan = :id")
    fun pagingSourceProductShop(id: Int): PagingSource<Int, ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("DELETE FROM products WHERE maNguoiBan = :id")
    suspend fun clearProductOfShop(id: Int)

    @Query("DELETE FROM products")
    suspend fun clearAll()

}
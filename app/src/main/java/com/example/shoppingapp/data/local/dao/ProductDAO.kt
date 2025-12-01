package com.example.shoppingapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shoppingapp.data.local.entity.ProductEntity

@Dao
interface ProductDAO {

    //tim kiem trong shop
    @Query("SELECT * FROM products WHERE maNguoiBan = :id AND motaSp LIKE '%' || :text || '%'")
    fun getPagingSourceSearchProductShop(id: Int, text: String): PagingSource<Int, ProductEntity>

    //lay san pham cua shop
    @Query("SELECT * FROM products WHERE maNguoiBan = :id")
    fun pagingSourceProductShop(id: Int): PagingSource<Int, ProductEntity>

    //lay nhung san pham ban chay (best seller)
    @Query("SELECT * FROM products WHERE maNguoiBan = :id AND numberBought >= 50")
    fun pagingSourceBestSellerProductShop(id: Int): PagingSource<Int, ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    //xoa search product
    @Query("DELETE FROM products WHERE maNguoiBan = :id AND motaSp LIKE '%' || :text || '%'")
    suspend fun clearSearchProductShop(id: Int, text: String)

    //xoa product cua shop
    @Query("DELETE FROM products WHERE maNguoiBan = :id")
    suspend fun clearProductOfShop(id: Int)

    //xoa nhung san pham ban chay
    @Query("DELETE FROM products WHERE maNguoiBan = :id AND numberBought >= 50")
    suspend fun clearBestSellerProductOfShop(id: Int)

    @Query("DELETE FROM products")
    suspend fun clearAll()

}
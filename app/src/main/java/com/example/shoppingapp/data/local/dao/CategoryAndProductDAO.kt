package com.example.shoppingapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shoppingapp.data.local.entity.CategoryAndProductEntity

@Dao
interface CategoryAndProductDAO {

    //lay danh muc va san pham tuong ung trong 1 shop
    @Query("SELECT * FROM category_product_shop WHERE maNguoiBan = :id AND tenDanhMuc = :tenDanhMuc")
    fun pagingSourceCategoryAndProductShop(id: Int, tenDanhMuc: String): PagingSource<Int, CategoryAndProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categoryAndProducts: List<CategoryAndProductEntity>)

    @Query("DELETE FROM category_product_shop WHERE maNguoiBan = :id AND tenDanhMuc = :tenDanhMuc")
    suspend fun clearCategoryAndProductShop(id: Int, tenDanhMuc: String)

}
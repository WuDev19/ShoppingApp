package com.example.shoppingapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.shoppingapp.data.local.entity.ProductDetailEntity

@Dao
interface ProductDetailDAO {

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(productDetailEntities: List<ProductDetailEntity>)

    @Query("DELETE FROM product_details")
    suspend fun deleteProductDetail()
}
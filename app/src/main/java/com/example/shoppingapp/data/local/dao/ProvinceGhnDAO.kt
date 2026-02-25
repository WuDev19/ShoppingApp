package com.example.shoppingapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.shoppingapp.data.local.entity.ProvinceGhnEntity

@Dao
interface ProvinceGhnDAO {

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(provinces: List<ProvinceGhnEntity>)

    @Query("SELECT 1 FROM provinces_ghn LIMIT 1")
    suspend fun checkDataExist(): Int?

    @Query("SELECT * FROM provinces_ghn")
    suspend fun getProvinces(): List<ProvinceGhnEntity>

}
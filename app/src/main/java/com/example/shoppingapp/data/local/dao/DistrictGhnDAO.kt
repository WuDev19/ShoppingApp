package com.example.shoppingapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.example.shoppingapp.data.local.entity.DistrictGhnEntity

@Dao
interface DistrictGhnDAO {

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(districts: List<DistrictGhnEntity>)

    @Query("SELECT 1 FROM districts_ghn WHERE provinceID = :provinceId LIMIT 1")
    suspend fun checkDataExist(provinceId: Int): Int?

    @Transaction
    @Query("SELECT * FROM districts_ghn WHERE provinceID = :provinceId")
    suspend fun getDistrictOfProvince(provinceId: Int): List<DistrictGhnEntity>

}
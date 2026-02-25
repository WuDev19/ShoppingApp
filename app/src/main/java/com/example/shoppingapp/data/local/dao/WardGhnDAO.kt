package com.example.shoppingapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.example.shoppingapp.data.local.entity.WardGhnEntity

@Dao
interface WardGhnDAO {

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(wards: List<WardGhnEntity>)

    @Query("SELECT 1 FROM wards_ghn WHERE districtID = :districtId LIMIT 1")
    suspend fun checkDataExist(districtId: Int): Int?

    @Transaction
    @Query("SELECT * FROM wards_ghn WHERE districtID = :districtId")
    suspend fun getWardOfDistrict(districtId: Int): List<WardGhnEntity>

}
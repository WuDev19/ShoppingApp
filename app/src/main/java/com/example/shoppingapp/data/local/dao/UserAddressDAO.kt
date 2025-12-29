package com.example.shoppingapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shoppingapp.data.local.entity.UserAddressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAddressDAO {
    @Query(value = "SELECT * FROM user_address WHERE maND = :maND ORDER BY defaultAddress DESC")
    fun getUserAddress(maND: String): Flow<List<UserAddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(userAddresses: List<UserAddressEntity>)

    @Query(value = "DELETE FROM user_address WHERE maND = :maND")
    suspend fun clearAll(maND: String)

}
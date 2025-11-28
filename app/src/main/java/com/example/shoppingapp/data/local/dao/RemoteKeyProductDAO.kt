package com.example.shoppingapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shoppingapp.data.local.entity.RemoteKeyProduct

@Dao
interface RemoteKeyProductDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeyProducts: List<RemoteKeyProduct>)

    @Query("DELETE FROM product_key_remotes")
    suspend fun clearAll()

    @Query("SELECT * FROM product_key_remotes WHERE id = :id")
    suspend fun getRemoteKeyById(id: Int): RemoteKeyProduct?

}
package com.example.shoppingapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shoppingapp.data.local.entity.RemoteKeyEntity

@Dao
interface RemoteKeyDAO {

    @Query("SELECT * FROM remote_key WHERE queryKey = :queryKey")
    suspend fun getRemoteKey(queryKey: String): RemoteKeyEntity?

    @Query("DELETE FROM remote_key WHERE queryKey = :queryKey")
    suspend fun clearByQueryKey(queryKey: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: RemoteKeyEntity)

    @Query("DELETE FROM remote_key")
    suspend fun clearAll()

}
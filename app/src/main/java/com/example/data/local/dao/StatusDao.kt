package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.StatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatusDao {
    @Query("SELECT * FROM statuses ORDER BY timestamp DESC")
    fun getAllStatuses(): Flow<List<StatusEntity>>

    @Query("SELECT * FROM statuses WHERE userCode = :userCode ORDER BY timestamp DESC")
    fun getStatusesByUser(userCode: String): Flow<List<StatusEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatus(status: StatusEntity)

    @Query("UPDATE statuses SET isViewed = 1 WHERE statusId = :statusId")
    suspend fun markAsViewed(statusId: String)

    @Query("DELETE FROM statuses WHERE statusId = :statusId")
    suspend fun deleteStatus(statusId: String)

    @Query("DELETE FROM statuses WHERE timestamp < :cutoffTime")
    suspend fun deleteExpiredStatuses(cutoffTime: Long)
}

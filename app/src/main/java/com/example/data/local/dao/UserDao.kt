package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userCode = :code")
    fun getUserByCode(code: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE userCode = :code")
    suspend fun getUserSync(code: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)

    @Query("UPDATE users SET isOnline = :isOnline, lastSeen = :lastSeen WHERE userCode = :code")
    suspend fun updateOnlineStatus(code: String, isOnline: Boolean, lastSeen: Long)

    @Query("UPDATE users SET isTyping = :isTyping WHERE userCode = :code")
    suspend fun updateTypingStatus(code: String, isTyping: Boolean)
}

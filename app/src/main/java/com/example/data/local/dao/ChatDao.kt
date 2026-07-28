package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY isPinned DESC, lastMessageTimestamp DESC")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE chatId = :chatId")
    fun getChatById(chatId: String): Flow<ChatEntity?>

    @Query("SELECT * FROM chats WHERE peerCode = :peerCode")
    suspend fun getChatByPeerCodeSync(peerCode: String): ChatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateChat(chat: ChatEntity)

    @Query("UPDATE chats SET lastMessageText = :lastText, lastMessageTimestamp = :timestamp, unreadCount = :unreadCount WHERE chatId = :chatId")
    suspend fun updateLastMessage(chatId: String, lastText: String, timestamp: Long, unreadCount: Int)

    @Query("UPDATE chats SET unreadCount = 0 WHERE chatId = :chatId")
    suspend fun clearUnreadCount(chatId: String)

    @Query("DELETE FROM chats WHERE chatId = :chatId")
    suspend fun deleteChat(chatId: String)
}

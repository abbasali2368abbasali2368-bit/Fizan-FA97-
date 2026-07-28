package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userCode: String,
    val displayName: String,
    val avatarColorHex: String,
    val bio: String,
    val isOnline: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis(),
    val isTyping: Boolean = false
)

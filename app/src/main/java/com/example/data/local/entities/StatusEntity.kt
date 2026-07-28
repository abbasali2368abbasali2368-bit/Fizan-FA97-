package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "statuses")
data class StatusEntity(
    @PrimaryKey
    val statusId: String,
    val userCode: String,
    val userName: String,
    val userAvatarHex: String,
    val statusType: String = "TEXT", // "TEXT" or "IMAGE"
    val textContent: String,
    val mediaUri: String? = null,
    val bgGradientHex: String = "#00A884",
    val timestamp: Long = System.currentTimeMillis(),
    val isViewed: Boolean = false
)

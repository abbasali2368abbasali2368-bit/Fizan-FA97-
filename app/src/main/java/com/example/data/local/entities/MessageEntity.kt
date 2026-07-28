package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val messageId: String,
    val chatId: String,
    val senderCode: String,
    val receiverCode: String,
    val messageType: String, // TEXT, IMAGE, VIDEO, DOCUMENT, VOICE, EMOJI
    val text: String,
    val cipherText: String,
    val mediaUri: String? = null,
    val voiceDurationSec: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val deliveryStatus: String = "SENT", // SENT, DELIVERED, READ
    val isEncrypted: Boolean = true
)

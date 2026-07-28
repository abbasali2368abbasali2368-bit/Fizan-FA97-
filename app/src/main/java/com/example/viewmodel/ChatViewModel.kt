package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferences
import com.example.data.local.entities.MessageEntity
import com.example.data.local.entities.UserEntity
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val prefs = UserPreferences(application)
    val repository = ChatRepository(application, db, prefs)

    private val _currentChatId = MutableStateFlow<String?>(null)
    val currentChatId: StateFlow<String?> = _currentChatId.asStateFlow()

    private val _peerCode = MutableStateFlow<String?>(null)
    val peerCode: StateFlow<String?> = _peerCode.asStateFlow()

    private val _peerUser = MutableStateFlow<UserEntity?>(null)
    val peerUser: StateFlow<UserEntity?> = _peerUser.asStateFlow()

    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages: StateFlow<List<MessageEntity>> = _messages.asStateFlow()

    fun loadChat(chatId: String, peerCode: String) {
        _currentChatId.value = chatId
        _peerCode.value = peerCode

        viewModelScope.launch {
            repository.markChatAsRead(chatId)
        }

        viewModelScope.launch {
            repository.getMessagesForChat(chatId).collect {
                _messages.value = it
            }
        }

        viewModelScope.launch {
            repository.getUserByCode(peerCode).collect {
                _peerUser.value = it
            }
        }
    }

    fun sendMessage(
        text: String,
        type: String = "TEXT",
        mediaUri: String? = null,
        voiceDurationSec: Int = 0
    ) {
        val chatId = _currentChatId.value ?: return
        val peer = _peerCode.value ?: return
        if (text.isBlank() && mediaUri == null && voiceDurationSec == 0) return

        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                peerCode = peer,
                text = text,
                messageType = type,
                mediaUri = mediaUri,
                voiceDurationSec = voiceDurationSec
            )
        }
    }

    fun clearHistory() {
        val chatId = _currentChatId.value ?: return
        viewModelScope.launch {
            repository.clearChatHistory(chatId)
        }
    }

    fun deleteChat() {
        val chatId = _currentChatId.value ?: return
        viewModelScope.launch {
            repository.deleteChat(chatId)
        }
    }
}

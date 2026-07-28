package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferences
import com.example.data.local.entities.ChatEntity
import com.example.data.local.entities.StatusEntity
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    val prefs = UserPreferences(application)
    val repository = ChatRepository(application, db, prefs)

    val userCode: String get() = prefs.getUserCode()

    private val _userName = MutableStateFlow(prefs.getUserName())
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userBio = MutableStateFlow(prefs.getUserBio())
    val userBio: StateFlow<String> = _userBio.asStateFlow()

    private val _avatarColorHex = MutableStateFlow(prefs.getUserAvatarColorHex())
    val avatarColorHex: StateFlow<String> = _avatarColorHex.asStateFlow()

    private val _themeMode = MutableStateFlow(prefs.getThemeMode())
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(prefs.isNotificationsEnabled())
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    val chats: StateFlow<List<ChatEntity>> = repository.allChats
        .let { flow ->
            val state = MutableStateFlow<List<ChatEntity>>(emptyList())
            viewModelScope.launch {
                flow.collect { state.value = it }
            }
            state.asStateFlow()
        }

    val statuses: StateFlow<List<StatusEntity>> = repository.allStatuses
        .let { flow ->
            val state = MutableStateFlow<List<StatusEntity>>(emptyList())
            viewModelScope.launch {
                flow.collect { state.value = it }
            }
            state.asStateFlow()
        }

    init {
        viewModelScope.launch {
            repository.initializeStarterDataIfNeeded()
        }
    }

    fun updateProfile(name: String, bio: String, avatarColorHex: String) {
        repository.updateUserProfile(name, bio, avatarColorHex)
        _userName.value = name
        _userBio.value = bio
        _avatarColorHex.value = avatarColorHex
    }

    fun setThemeMode(mode: String) {
        prefs.setThemeMode(mode)
        _themeMode.value = mode
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.setNotificationsEnabled(enabled)
        _notificationsEnabled.value = enabled
    }

    suspend fun createChatByCode(code: String): String {
        return repository.addChatByCode(code)
    }

    fun uploadStatus(textContent: String, mediaUri: String? = null, bgGradientHex: String = "#00A884", statusType: String = "TEXT") {
        viewModelScope.launch {
            repository.uploadStatus(textContent, mediaUri, bgGradientHex, statusType)
        }
    }

    fun markStatusAsViewed(statusId: String) {
        viewModelScope.launch {
            repository.markStatusAsViewed(statusId)
        }
    }

    fun deleteStatus(statusId: String) {
        viewModelScope.launch {
            repository.deleteStatus(statusId)
        }
    }

    fun sendReplyToStatus(peerCode: String, replyText: String) {
        viewModelScope.launch {
            val chatId = repository.addChatByCode(peerCode)
            repository.sendMessage(chatId = chatId, peerCode = peerCode, text = replyText)
        }
    }
}

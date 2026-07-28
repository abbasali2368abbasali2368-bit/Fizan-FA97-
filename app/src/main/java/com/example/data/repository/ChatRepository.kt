package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferences
import com.example.data.local.dao.StatusDao
import com.example.data.local.entities.ChatEntity
import com.example.data.local.entities.MessageEntity
import com.example.data.local.entities.StatusEntity
import com.example.data.local.entities.UserEntity
import com.example.util.CodeGenerator
import com.example.util.EncryptionUtils
import com.example.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatRepository(
    private val context: Context,
    private val db: AppDatabase,
    val prefs: UserPreferences
) {
    private val userDao = db.userDao()
    private val chatDao = db.chatDao()
    private val messageDao = db.messageDao()
    private val statusDao = db.statusDao()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    val currentUserCode: String get() = prefs.getUserCode()
    val currentUserName: String get() = prefs.getUserName()

    val allChats: Flow<List<ChatEntity>> = chatDao.getAllChats()
    val allStatuses: Flow<List<StatusEntity>> = statusDao.getAllStatuses()

    suspend fun initializeStarterDataIfNeeded() {
        val myCode = prefs.getOrGenerateUserCode()
        
        // Check if starter contacts exist
        val existingChats = db.chatDao().getChatByPeerCodeSync("SARAH9X2")
        if (existingChats == null) {
            // Seed starter contacts
            val sarah = UserEntity("SARAH9X2", "Sarah Connor", "#00A884", "Cybersecurity Specialist", true)
            val cipherBot = UserEntity("CIPHER81", "Cipher AI", "#38BDF8", "Encrypted Echo Bot • Try sending a code!", true)
            val alex = UserEntity("ALEX4K7M", "Alex Vance", "#A855F7", "Coding in Kotlin & Jetpack Compose", false)

            userDao.insertOrUpdateUser(sarah)
            userDao.insertOrUpdateUser(cipherBot)
            userDao.insertOrUpdateUser(alex)

            // Seed starter chats
            val now = System.currentTimeMillis()
            val chat1Id = getChatId(myCode, "SARAH9X2")
            val chat2Id = getChatId(myCode, "CIPHER81")
            val chat3Id = getChatId(myCode, "ALEX4K7M")

            chatDao.insertOrUpdateChat(
                ChatEntity(chat1Id, "SARAH9X2", "Sarah Connor", "#00A884", "Hey! Connected with code SARAH9X2 🔒", now - 120000, unreadCount = 1)
            )
            chatDao.insertOrUpdateChat(
                ChatEntity(chat2Id, "CIPHER81", "Cipher AI", "#38BDF8", "Send me any message to test E2E encryption!", now - 300000, unreadCount = 0)
            )
            chatDao.insertOrUpdateChat(
                ChatEntity(chat3Id, "ALEX4K7M", "Alex Vance", "#A855F7", "Let's review the secure key exchange later.", now - 86400000, unreadCount = 0)
            )

            // Seed starter messages
            val cipherText1 = EncryptionUtils.encrypt("Hey! Connected with code SARAH9X2 🔒")
            messageDao.insertMessage(
                MessageEntity(
                    messageId = UUID.randomUUID().toString(),
                    chatId = chat1Id,
                    senderCode = "SARAH9X2",
                    receiverCode = myCode,
                    messageType = "TEXT",
                    text = "Hey! Connected with code SARAH9X2 🔒",
                    cipherText = cipherText1,
                    timestamp = now - 120000,
                    deliveryStatus = "DELIVERED",
                    isEncrypted = true
                )
            )

            val cipherText2 = EncryptionUtils.encrypt("Welcome to CodeChat! Send me any message to test E2E encryption and real-time delivery ticks!")
            messageDao.insertMessage(
                MessageEntity(
                    messageId = UUID.randomUUID().toString(),
                    chatId = chat2Id,
                    senderCode = "CIPHER81",
                    receiverCode = myCode,
                    messageType = "TEXT",
                    text = "Welcome to CodeChat! Send me any message to test E2E encryption and real-time delivery ticks!",
                    cipherText = cipherText2,
                    timestamp = now - 300000,
                    deliveryStatus = "READ",
                    isEncrypted = true
                )
            )

            // Seed starter statuses
            statusDao.insertStatus(
                StatusEntity(
                    statusId = UUID.randomUUID().toString(),
                    userCode = "SARAH9X2",
                    userName = "Sarah Connor",
                    userAvatarHex = "#00A884",
                    statusType = "TEXT",
                    textContent = "🔒 Security audit complete! All encryption keys verified on FA97 Chat.",
                    bgGradientHex = "#00A884",
                    timestamp = now - 1800000,
                    isViewed = false
                )
            )
            statusDao.insertStatus(
                StatusEntity(
                    statusId = UUID.randomUUID().toString(),
                    userCode = "ALEX4K7M",
                    userName = "Alex Vance",
                    userAvatarHex = "#A855F7",
                    statusType = "TEXT",
                    textContent = "🚀 Building amazing Android UI with Jetpack Compose & Kotlin!",
                    bgGradientHex = "#8B5CF6",
                    timestamp = now - 3600000,
                    isViewed = false
                )
            )
        }
    }

    suspend fun uploadStatus(
        textContent: String,
        mediaUri: String? = null,
        bgGradientHex: String = "#00A884",
        statusType: String = "TEXT"
    ) {
        val myCode = prefs.getUserCode()
        val myName = prefs.getUserName()
        val myAvatarHex = prefs.getUserAvatarColorHex()

        val newStatus = StatusEntity(
            statusId = UUID.randomUUID().toString(),
            userCode = myCode,
            userName = myName,
            userAvatarHex = myAvatarHex,
            statusType = statusType,
            textContent = textContent,
            mediaUri = mediaUri,
            bgGradientHex = bgGradientHex,
            timestamp = System.currentTimeMillis(),
            isViewed = true
        )
        statusDao.insertStatus(newStatus)
    }

    suspend fun markStatusAsViewed(statusId: String) {
        statusDao.markAsViewed(statusId)
    }

    suspend fun deleteStatus(statusId: String) {
        statusDao.deleteStatus(statusId)
    }

    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>> {
        return messageDao.getMessagesForChat(chatId)
    }

    fun getUserByCode(code: String): Flow<UserEntity?> {
        return userDao.getUserByCode(code)
    }

    suspend fun addChatByCode(code: String): String {
        val formattedCode = CodeGenerator.formatCode(code)
        val myCode = prefs.getUserCode()
        val chatId = getChatId(myCode, formattedCode)

        // Check if chat exists
        val existingChat = chatDao.getChatByPeerCodeSync(formattedCode)
        if (existingChat != null) {
            return existingChat.chatId
        }

        // Check if user entity exists, otherwise create
        var user = userDao.getUserSync(formattedCode)
        if (user == null) {
            user = UserEntity(
                userCode = formattedCode,
                displayName = "Contact $formattedCode",
                avatarColorHex = getRandomColorHex(formattedCode),
                bio = "CodeChat Peer",
                isOnline = true
            )
            userDao.insertOrUpdateUser(user)
        }

        val newChat = ChatEntity(
            chatId = chatId,
            peerCode = user.userCode,
            peerName = user.displayName,
            peerAvatarHex = user.avatarColorHex,
            lastMessageText = "Chat created via Code ${user.userCode}",
            lastMessageTimestamp = System.currentTimeMillis(),
            unreadCount = 0
        )
        chatDao.insertOrUpdateChat(newChat)
        return chatId
    }

    suspend fun sendMessage(
        chatId: String,
        peerCode: String,
        text: String,
        messageType: String = "TEXT",
        mediaUri: String? = null,
        voiceDurationSec: Int = 0
    ) {
        val myCode = prefs.getUserCode()
        val msgId = UUID.randomUUID().toString()
        val encryptedText = EncryptionUtils.encrypt(text)
        val now = System.currentTimeMillis()

        val message = MessageEntity(
            messageId = msgId,
            chatId = chatId,
            senderCode = myCode,
            receiverCode = peerCode,
            messageType = messageType,
            text = text,
            cipherText = encryptedText,
            mediaUri = mediaUri,
            voiceDurationSec = voiceDurationSec,
            timestamp = now,
            deliveryStatus = "SENT",
            isEncrypted = true
        )

        messageDao.insertMessage(message)

        val displaySummary = when (messageType) {
            "IMAGE" -> "📷 Photo"
            "VIDEO" -> "🎥 Video"
            "DOCUMENT" -> "📄 Document"
            "VOICE" -> "🎤 Voice message ($voiceDurationSec s)"
            "EMOJI" -> text
            else -> text
        }

        chatDao.updateLastMessage(chatId, displaySummary, now, unreadCount = 0)

        // Simulate delivery ticks & real-time response
        repositoryScope.launch {
            delay(800)
            messageDao.updateMessageStatus(msgId, "DELIVERED")

            delay(1200)
            messageDao.updateMessageStatus(msgId, "READ")

            // Auto-reply response simulation for test bots / contacts
            triggerPeerAutoReply(chatId, peerCode, text, messageType)
        }
    }

    private suspend fun triggerPeerAutoReply(
        chatId: String,
        peerCode: String,
        userMsgText: String,
        type: String
    ) {
        // Show typing indicator
        userDao.updateTypingStatus(peerCode, true)
        delay(2000)
        userDao.updateTypingStatus(peerCode, false)

        val replyText = when (peerCode) {
            "CIPHER81" -> "🔒 Cipher Echo: Received your encrypted $type! Content verified and secured."
            "SARAH9X2" -> "Got your message! Thanks for reaching out via code $peerCode. 👍"
            else -> "Hey! Thanks for chatting via CodeChat ($peerCode). Let's keep in touch!"
        }

        val replyMsgId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val replyEncrypted = EncryptionUtils.encrypt(replyText)

        val replyMessage = MessageEntity(
            messageId = replyMsgId,
            chatId = chatId,
            senderCode = peerCode,
            receiverCode = prefs.getUserCode(),
            messageType = "TEXT",
            text = replyText,
            cipherText = replyEncrypted,
            timestamp = now,
            deliveryStatus = "DELIVERED",
            isEncrypted = true
        )

        messageDao.insertMessage(replyMessage)
        chatDao.updateLastMessage(chatId, replyText, now, unreadCount = 1)

        if (prefs.isNotificationsEnabled()) {
            val peerName = userDao.getUserSync(peerCode)?.displayName ?: peerCode
            NotificationHelper.showNotification(context, peerName, replyText, peerCode)
        }
    }

    suspend fun markChatAsRead(chatId: String) {
        chatDao.clearUnreadCount(chatId)
        messageDao.markChatMessagesAsRead(chatId, prefs.getUserCode())
    }

    suspend fun clearChatHistory(chatId: String) {
        messageDao.deleteAllMessagesForChat(chatId)
        chatDao.updateLastMessage(chatId, "Chat history cleared", System.currentTimeMillis(), 0)
    }

    suspend fun deleteChat(chatId: String) {
        messageDao.deleteAllMessagesForChat(chatId)
        chatDao.deleteChat(chatId)
    }

    fun updateUserProfile(name: String, bio: String, avatarColorHex: String) {
        prefs.setUserName(name)
        prefs.setUserBio(bio)
        prefs.setUserAvatarColorHex(avatarColorHex)
    }

    private fun getChatId(code1: String, code2: String): String {
        val sorted = listOf(code1, code2).sorted()
        return "chat_${sorted[0]}_${sorted[1]}"
    }

    private fun getRandomColorHex(code: String): String {
        val colors = listOf("#00A884", "#38BDF8", "#A855F7", "#EC4899", "#F59E0B", "#10B981")
        val index = Math.abs(code.hashCode()) % colors.size
        return colors[index]
    }
}

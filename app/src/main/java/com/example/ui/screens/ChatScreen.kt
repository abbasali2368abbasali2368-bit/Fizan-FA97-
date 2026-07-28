package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmojiPickerModal
import com.example.ui.components.MediaAttachmentModal
import com.example.ui.components.MessageItem
import com.example.ui.components.UserAvatar
import com.example.ui.components.VoiceRecorderBar
import com.example.ui.theme.EmeraldLight
import com.example.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val peerUser by viewModel.peerUser.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showEmojiModal by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.testTag("chat_header_peer_info")
                    ) {
                        UserAvatar(
                            name = peerUser?.displayName ?: "Peer",
                            colorHex = peerUser?.avatarColorHex ?: "#00A884",
                            size = 40.dp,
                            isOnline = peerUser?.isOnline ?: true
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = peerUser?.displayName ?: "Loading...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(EmeraldLight.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = peerUser?.userCode ?: "",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = EmeraldLight
                                    )
                                }
                            }

                            Text(
                                text = if (peerUser?.isTyping == true) "typing..." else if (peerUser?.isOnline == true) "Online" else "Offline",
                                fontSize = 12.sp,
                                color = if (peerUser?.isTyping == true) EmeraldLight else Color.Gray
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("chat_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Voice call connection requested...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Voice Call", tint = Color.White)
                    }
                    IconButton(onClick = {
                        Toast.makeText(context, "Video call connection requested...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = "Video Call", tint = Color.White)
                    }
                    IconButton(onClick = { showOverflowMenu = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More Options", tint = Color.White)
                    }

                    DropdownMenu(
                        expanded = showOverflowMenu,
                        onDismissRequest = { showOverflowMenu = false },
                        modifier = Modifier.background(Color(0xFF202C33))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Clear History", color = Color.White) },
                            onClick = {
                                viewModel.clearHistory()
                                showOverflowMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Chat", color = Color.White) },
                            onClick = {
                                viewModel.deleteChat()
                                showOverflowMenu = false
                                onBack()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF111B21)
                )
            )
        },
        containerColor = Color(0xFF0B141A)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("chat_screen_content")
        ) {
            // E2E Encryption Banner Notice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF182229))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = EmeraldLight,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Messages are end-to-end encrypted with base key.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // Message History List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                items(messages, key = { it.messageId }) { msg ->
                    MessageItem(
                        message = msg,
                        isFromMe = msg.senderCode != peerUser?.userCode
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Input Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF111B21))
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                AnimatedVisibility(
                    visible = isRecordingVoice,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    VoiceRecorderBar(
                        onCancel = { isRecordingVoice = false },
                        onSend = { sec ->
                            viewModel.sendMessage(
                                text = "🎤 Voice message",
                                type = "VOICE",
                                voiceDurationSec = sec
                            )
                            isRecordingVoice = false
                        }
                    )
                }

                AnimatedVisibility(
                    visible = !isRecordingVoice,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { showAttachmentSheet = true },
                            modifier = Modifier.testTag("attachment_sheet_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Attachment",
                                tint = EmeraldLight
                            )
                        }

                        IconButton(
                            onClick = { showEmojiModal = true },
                            modifier = Modifier.testTag("emoji_modal_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SentimentSatisfiedAlt,
                                contentDescription = "Emoji Picker",
                                tint = Color.Gray
                            )
                        }

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Type an encrypted message...", fontSize = 14.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("message_input_text_field"),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF202C33),
                                unfocusedContainerColor = Color(0xFF202C33),
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        if (inputText.isBlank()) {
                            IconButton(
                                onClick = { isRecordingVoice = true },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldLight)
                                    .testTag("start_voice_recording_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Record Voice Note",
                                    tint = Color.White
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    val textToSend = inputText
                                    inputText = ""
                                    viewModel.sendMessage(text = textToSend, type = "TEXT")
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldLight)
                                    .testTag("send_message_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send Message",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAttachmentSheet) {
        MediaAttachmentModal(
            onDismiss = { showAttachmentSheet = false },
            onSelectOption = { mediaType ->
                showAttachmentSheet = false
                val label = when (mediaType) {
                    "IMAGE" -> "📷 Shared photo"
                    "VIDEO" -> "🎥 Shared video"
                    "DOCUMENT" -> "📄 Shared document file"
                    else -> "Media file"
                }
                viewModel.sendMessage(
                    text = label,
                    type = mediaType,
                    mediaUri = "android.resource://${context.packageName}/${com.example.R.drawable.img_empty_chats}"
                )
            }
        )
    }

    if (showEmojiModal) {
        EmojiPickerModal(
            onDismiss = { showEmojiModal = false },
            onEmojiSelected = { selectedEmoji ->
                inputText += selectedEmoji
            }
        )
    }
}

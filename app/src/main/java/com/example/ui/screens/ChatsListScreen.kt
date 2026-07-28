package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.border
import com.example.R
import com.example.data.local.entities.ChatEntity
import com.example.data.local.entities.StatusEntity
import com.example.ui.components.UserAvatar
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.UnreadBadge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatsListScreen(
    chats: List<ChatEntity>,
    statuses: List<StatusEntity> = emptyList(),
    currentUserCode: String = "",
    currentUserName: String = "",
    currentUserAvatarHex: String = "",
    onChatSelected: (chatId: String, peerCode: String) -> Unit,
    onOpenNewChatByCode: () -> Unit,
    onOpenQrCodeModal: () -> Unit,
    onOpenUploadStatus: () -> Unit = {},
    onStatusClick: (status: StatusEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All Chats", "Unread", "Favorites")

    val filteredChats = remember(chats, searchQuery, selectedTab) {
        chats.filter { chat ->
            val matchesQuery = chat.peerName.contains(searchQuery, ignoreCase = true) ||
                    chat.peerCode.contains(searchQuery, ignoreCase = true) ||
                    chat.lastMessageText.contains(searchQuery, ignoreCase = true)

            val matchesTab = when (selectedTab) {
                1 -> chat.unreadCount > 0
                2 -> chat.isPinned
                else -> true
            }

            matchesQuery && matchesTab
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("chats_list_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search chats or 8-char codes...", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = onOpenQrCodeModal,
                            modifier = Modifier.testTag("qr_scan_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "QR Code",
                                tint = EmeraldLight
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chat_search_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedBorderColor = EmeraldLight,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            // Horizontal Status Stories Carousel
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                val myStatus = statuses.firstOrNull { it.userCode == currentUserCode }
                val contactStatuses = statuses.filter { it.userCode != currentUserCode }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("status_stories_carousel"),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    // My status avatar with add button
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    if (myStatus != null) onStatusClick(myStatus) else onOpenUploadStatus()
                                }
                                .testTag("my_status_story_circle")
                        ) {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = if (myStatus != null) 2.5.dp else 0.dp,
                                            color = EmeraldLight,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    UserAvatar(
                                        name = currentUserName.ifEmpty { "My Status" },
                                        colorHex = currentUserAvatarHex.ifEmpty { "#00A884" },
                                        size = 50.dp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldLight)
                                        .border(1.5.dp, Color(0xFF111B21), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Status",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "My Status",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    // Contacts statuses
                    items(contactStatuses, key = { it.statusId }) { status ->
                        val ringColor = if (status.isViewed) Color.Gray else EmeraldLight

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onStatusClick(status) }
                                .testTag("status_story_circle_${status.statusId}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .border(width = 2.5.dp, color = ringColor, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                UserAvatar(
                                    name = status.userName,
                                    colorHex = status.userAvatarHex,
                                    size = 50.dp
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = status.userName.take(8),
                                fontSize = 11.sp,
                                fontWeight = if (!status.isViewed) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Tab Filters
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = EmeraldLight,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = EmeraldLight
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.testTag("chats_tab_$index")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredChats.isEmpty()) {
                // Empty State Illustration
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_empty_chats),
                        contentDescription = "No Chats",
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No chats found matching '$searchQuery'" else "No conversations yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap the + button below to add someone's 8-character code and start chatting instantly!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(filteredChats, key = { it.chatId }) { chat ->
                        ChatItemCard(
                            chat = chat,
                            onClick = { onChatSelected(chat.chatId, chat.peerCode) }
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onOpenNewChatByCode,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_new_chat_fab"),
            containerColor = EmeraldLight,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "New Chat by Code",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun ChatItemCard(
    chat: ChatEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("chat_card_${chat.peerCode}"),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(
                name = chat.peerName,
                colorHex = chat.peerAvatarHex,
                size = 52.dp,
                isOnline = true
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = chat.peerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = formatChatTime(chat.lastMessageTimestamp),
                        fontSize = 12.sp,
                        color = if (chat.unreadCount > 0) EmeraldLight else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Badge showing the unique code
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldLight.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = chat.peerCode,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = EmeraldLight
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = chat.lastMessageText,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (chat.unreadCount > 0) {
                        Badge(
                            containerColor = UnreadBadge,
                            contentColor = Color.White,
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            Text(
                                text = chat.unreadCount.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    } else if (chat.isPinned) {
                        Icon(
                            imageVector = Icons.Default.Pin,
                            contentDescription = "Pinned",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatChatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diffDays = (now - timestamp) / (1000 * 60 * 60 * 24)
    return if (diffDays == 0L) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
    } else if (diffDays == 1L) {
        "Yesterday"
    } else {
        SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
    }
}

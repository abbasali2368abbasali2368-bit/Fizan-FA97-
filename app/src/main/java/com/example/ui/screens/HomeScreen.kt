package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.StatusEntity
import com.example.ui.components.QRCodeModal
import com.example.ui.components.StatusViewerDialog
import com.example.ui.components.UploadStatusDialog
import com.example.ui.components.UserAvatar
import com.example.ui.theme.EmeraldLight
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onChatSelected: (chatId: String, peerCode: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedNavIndex by remember { mutableIntStateOf(0) }
    var showNewChatDialog by remember { mutableStateOf(false) }
    var showQrCodeModal by remember { mutableStateOf(false) }
    var showUploadStatusDialog by remember { mutableStateOf(false) }
    var selectedStatusToView by remember { mutableStateOf<StatusEntity?>(null) }

    val userCode = viewModel.userCode
    val userName by viewModel.userName.collectAsState()
    val avatarColorHex by viewModel.avatarColorHex.collectAsState()
    val chats by viewModel.chats.collectAsState()
    val statuses by viewModel.statuses.collectAsState()

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.testTag("home_top_bar_title")
                    ) {
                        UserAvatar(
                            name = userName,
                            colorHex = avatarColorHex,
                            size = 36.dp,
                            isOnline = true,
                            modifier = Modifier.clickable { selectedNavIndex = 1 }
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "FA97 Chat",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ID: ",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(EmeraldLight.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = userCode,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = EmeraldLight
                                    )
                                }
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showQrCodeModal = true },
                        modifier = Modifier.testTag("qr_modal_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "My QR Code",
                            tint = EmeraldLight
                        )
                    }

                    IconButton(
                        onClick = { showNewChatDialog = true },
                        modifier = Modifier.testTag("add_chat_by_code_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "New Chat by Code",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF111B21)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF111B21),
                contentColor = EmeraldLight,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("home_bottom_navigation")
            ) {
                NavigationBarItem(
                    selected = selectedNavIndex == 0,
                    onClick = { selectedNavIndex = 0 },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Chats") },
                    label = { Text("Chats") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = EmeraldLight,
                        indicatorColor = EmeraldLight
                    ),
                    modifier = Modifier.testTag("nav_chats_tab")
                )

                NavigationBarItem(
                    selected = selectedNavIndex == 1,
                    onClick = { selectedNavIndex = 1 },
                    icon = { Icon(Icons.Default.CameraAlt, contentDescription = "Status") },
                    label = { Text("Status") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = EmeraldLight,
                        indicatorColor = EmeraldLight
                    ),
                    modifier = Modifier.testTag("nav_status_tab")
                )

                NavigationBarItem(
                    selected = selectedNavIndex == 2,
                    onClick = { selectedNavIndex = 2 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = EmeraldLight,
                        indicatorColor = EmeraldLight
                    ),
                    modifier = Modifier.testTag("nav_profile_tab")
                )

                NavigationBarItem(
                    selected = selectedNavIndex == 3,
                    onClick = { selectedNavIndex = 3 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = EmeraldLight,
                        indicatorColor = EmeraldLight
                    ),
                    modifier = Modifier.testTag("nav_settings_tab")
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag("home_screen_scaffold")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedNavIndex) {
                0 -> ChatsListScreen(
                    chats = chats,
                    statuses = statuses,
                    currentUserCode = userCode,
                    currentUserName = userName,
                    currentUserAvatarHex = avatarColorHex,
                    onChatSelected = onChatSelected,
                    onOpenNewChatByCode = { showNewChatDialog = true },
                    onOpenQrCodeModal = { showQrCodeModal = true },
                    onOpenUploadStatus = { showUploadStatusDialog = true },
                    onStatusClick = { status ->
                        viewModel.markStatusAsViewed(status.statusId)
                        selectedStatusToView = status
                    }
                )
                1 -> StatusListScreen(
                    statuses = statuses,
                    currentUserCode = userCode,
                    currentUserName = userName,
                    currentUserAvatarHex = avatarColorHex,
                    onOpenUploadStatus = { showUploadStatusDialog = true },
                    onStatusClick = { status ->
                        viewModel.markStatusAsViewed(status.statusId)
                        selectedStatusToView = status
                    }
                )
                2 -> ProfileScreen(
                    viewModel = viewModel,
                    onShowQrCodeModal = { showQrCodeModal = true }
                )
                3 -> SettingsScreen(
                    viewModel = viewModel
                )
            }
        }
    }

    if (showUploadStatusDialog) {
        UploadStatusDialog(
            onDismiss = { showUploadStatusDialog = false },
            onUploadStatus = { textContent, mediaUri, bgGradientHex, statusType ->
                viewModel.uploadStatus(textContent, mediaUri, bgGradientHex, statusType)
            }
        )
    }

    if (selectedStatusToView != null) {
        StatusViewerDialog(
            status = selectedStatusToView!!,
            currentUserCode = userCode,
            onDismiss = { selectedStatusToView = null },
            onDeleteStatus = { statusId ->
                viewModel.deleteStatus(statusId)
            },
            onReplyToStatus = { peerCode, replyText ->
                viewModel.sendReplyToStatus(peerCode, replyText)
            }
        )
    }

    if (showNewChatDialog) {
        NewChatByCodeDialog(
            onDismiss = { showNewChatDialog = false },
            myCode = userCode,
            onSubmitCode = { peerCode ->
                scope.launch {
                    val chatId = viewModel.createChatByCode(peerCode)
                    onChatSelected(chatId, peerCode)
                }
            }
        )
    }

    if (showQrCodeModal) {
        QRCodeModal(
            userCode = userCode,
            onDismiss = { showQrCodeModal = false }
        )
    }
}

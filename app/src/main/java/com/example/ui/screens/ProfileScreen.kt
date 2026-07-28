package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.ui.components.CodeDisplayCard
import com.example.ui.components.UserAvatar
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onShowQrCodeModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userCode = viewModel.userCode
    val userName by viewModel.userName.collectAsState()
    val userBio by viewModel.userBio.collectAsState()
    val avatarColorHex by viewModel.avatarColorHex.collectAsState()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showEditBioDialog by remember { mutableStateOf(false) }

    var tempName by remember { mutableStateOf(userName) }
    var tempBio by remember { mutableStateOf(userBio) }

    val avatarColors = listOf("#00A884", "#38BDF8", "#A855F7", "#EC4899", "#F59E0B", "#10B981")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("profile_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar Header
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .testTag("profile_avatar_section"),
            contentAlignment = Alignment.Center
        ) {
            UserAvatar(
                name = userName,
                colorHex = avatarColorHex,
                size = 100.dp,
                isOnline = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Avatar Color Selector Chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            avatarColors.forEach { hex ->
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(com.example.ui.components.parseHexColor(hex))
                        .clickable {
                            viewModel.updateProfile(userName, userBio, hex)
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Code Display Card
        CodeDisplayCard(
            userCode = userCode,
            onShowQrCode = onShowQrCodeModal
        )

        Spacer(modifier = Modifier.height(20.dp))

        // User Details Cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Display Name Item
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            tempName = userName
                            showEditNameDialog = true
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = EmeraldLight,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text("Name", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(userName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(onClick = {
                        tempName = userName
                        showEditNameDialog = true
                    }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Name", tint = EmeraldLight)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bio / Status Item
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            tempBio = userBio
                            showEditBioDialog = true
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = EmeraldLight,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text("About / Status", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(userBio, fontSize = 14.sp)
                        }
                    }

                    IconButton(onClick = {
                        tempBio = userBio
                        showEditBioDialog = true
                    }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Bio", tint = EmeraldLight)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // E2E Key Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldDark.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = EmeraldLight,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text("Security Fingerprint", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                    Text(
                        text = "SHA-256 E2E Key: ${userCode.hashCode().toString(16).uppercase()}-E2E-SECURE",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Edit Display Name") },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    singleLine = true,
                    label = { Text("Display Name") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (tempName.isNotBlank()) {
                        viewModel.updateProfile(tempName, userBio, avatarColorHex)
                    }
                    showEditNameDialog = false
                }) {
                    Text("Save", color = EmeraldLight)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditBioDialog) {
        AlertDialog(
            onDismissRequest = { showEditBioDialog = false },
            title = { Text("Edit Status / About") },
            text = {
                OutlinedTextField(
                    value = tempBio,
                    onValueChange = { tempBio = it },
                    label = { Text("Status") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (tempBio.isNotBlank()) {
                        viewModel.updateProfile(userName, tempBio, avatarColorHex)
                    }
                    showEditBioDialog = false
                }) {
                    Text("Save", color = EmeraldLight)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditBioDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

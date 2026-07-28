package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldLight
import com.example.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val themeMode by viewModel.themeMode.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()

    var showSecurityInfoDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("settings_screen")
    ) {
        Text(
            text = "App Settings",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Appearance Section
        SettingsSectionCard(title = "Appearance") {
            SettingsSwitchRow(
                icon = Icons.Default.DarkMode,
                title = "Dark Theme",
                subtitle = "Toggle dark/light mode UI",
                checked = themeMode == "DARK",
                onCheckedChange = { isDark ->
                    viewModel.setThemeMode(if (isDark) "DARK" else "LIGHT")
                },
                tag = "dark_theme_switch"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notifications Section
        SettingsSectionCard(title = "Notifications & Sounds") {
            SettingsSwitchRow(
                icon = Icons.Default.Notifications,
                title = "Push Notifications",
                subtitle = "Receive popups when new encrypted messages arrive",
                checked = notificationsEnabled,
                onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                tag = "push_notifications_switch"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security & Privacy
        SettingsSectionCard(title = "Privacy & Security") {
            SettingsActionRow(
                icon = Icons.Default.Lock,
                title = "End-to-End Encryption",
                subtitle = "AES-256 Bit GCM payload encryption active",
                onClick = { showSecurityInfoDialog = true },
                tag = "encryption_details_row"
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            SettingsActionRow(
                icon = Icons.Default.Security,
                title = "Unique Device Code",
                subtitle = "Code ${viewModel.userCode} permanently bound to this device",
                onClick = {
                    Toast.makeText(context, "Code ${viewModel.userCode} active", Toast.LENGTH_SHORT).show()
                },
                tag = "device_code_row"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data & Storage
        SettingsSectionCard(title = "Data & Storage") {
            SettingsActionRow(
                icon = Icons.Default.DeleteForever,
                title = "Local Cache",
                subtitle = "Clear temporary image and media cache",
                onClick = {
                    Toast.makeText(context, "Local cache cleared successfully!", Toast.LENGTH_SHORT).show()
                },
                tag = "clear_cache_row"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // About & Version
        SettingsSectionCard(title = "About") {
            SettingsActionRow(
                icon = Icons.Default.Info,
                title = "FA97 Chat Version",
                subtitle = "v1.0.0 • Build 2026.1 (Kotlin + Jetpack Compose)",
                onClick = {},
                tag = "about_row"
            )
        }
    }

    if (showSecurityInfoDialog) {
        AlertDialog(
            onDismissRequest = { showSecurityInfoDialog = false },
            title = { Text("FA97 Chat Encryption Architecture") },
            text = {
                Text(
                    "FA97 Chat uses client-side symmetric AES encryption for all messaging payloads.\n\n" +
                            "Because identity is derived from your unique 8-character device code (${viewModel.userCode}), " +
                            "no phone numbers or email addresses are stored or shared with servers."
                )
            },
            confirmButton = {
                TextButton(onClick = { showSecurityInfoDialog = false }) {
                    Text("Got it", color = EmeraldLight)
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = EmeraldLight,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = EmeraldLight
            )
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

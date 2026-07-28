package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.EmeraldLight

data class ColorOption(val hex: String, val name: String)

val statusColorOptions = listOf(
    ColorOption("#00A884", "Emerald"),
    ColorOption("#3B82F6", "Blue"),
    ColorOption("#8B5CF6", "Purple"),
    ColorOption("#EC4899", "Pink"),
    ColorOption("#10B981", "Teal"),
    ColorOption("#F59E0B", "Amber"),
    ColorOption("#1E293B", "Slate")
)

val sampleImageOptions = listOf(
    "https://images.unsplash.com/photo-1518770660439-4636190af475?w=500&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=500&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=500&auto=format&fit=crop",
    "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=500&auto=format&fit=crop"
)

@Composable
fun UploadStatusDialog(
    onDismiss: () -> Unit,
    onUploadStatus: (textContent: String, mediaUri: String?, bgGradientHex: String, statusType: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Text, 1: Photo
    var statusText by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf(statusColorOptions[0].hex) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("upload_status_dialog"),
            color = Color(0xFF1F2C34)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upload Status",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_upload_status")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF111B21),
                    contentColor = EmeraldLight
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.TextFields,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Text Status")
                            }
                        },
                        selectedContentColor = EmeraldLight,
                        unselectedContentColor = Color.Gray,
                        modifier = Modifier.testTag("tab_text_status")
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Image,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Photo Status")
                            }
                        },
                        selectedContentColor = EmeraldLight,
                        unselectedContentColor = Color.Gray,
                        modifier = Modifier.testTag("tab_photo_status")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Preview Container
                val parseBgColor = remember(selectedColorHex) {
                    try {
                        Color(android.graphics.Color.parseColor(selectedColorHex))
                    } catch (e: Exception) {
                        EmeraldLight
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(parseBgColor)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = statusText.ifEmpty { if (selectedTab == 0) "Type a status update..." else "Add a caption for your photo..." },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // Color selector
                    Text(
                        text = "Choose Background Color:",
                        fontSize = 13.sp,
                        color = Color.LightGray,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(statusColorOptions) { colorOpt ->
                            val parsedColor = try {
                                Color(android.graphics.Color.parseColor(colorOpt.hex))
                            } catch (e: Exception) {
                                Color.Green
                            }

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(parsedColor)
                                    .border(
                                        width = if (selectedColorHex == colorOpt.hex) 3.dp else 0.dp,
                                        color = if (selectedColorHex == colorOpt.hex) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColorHex = colorOpt.hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedColorHex == colorOpt.hex) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Image selector
                    Text(
                        text = "Select Photo Background:",
                        fontSize = 13.sp,
                        color = Color.LightGray,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(sampleImageOptions) { imageUri ->
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF2A3942))
                                    .border(
                                        width = if (selectedImageUri == imageUri) 3.dp else 0.dp,
                                        color = if (selectedImageUri == imageUri) EmeraldLight else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedImageUri = imageUri },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Photo Option",
                                    tint = EmeraldLight
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = statusText,
                    onValueChange = { statusText = it },
                    placeholder = { Text("Enter status message...", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("status_text_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldLight,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF111B21),
                        unfocusedContainerColor = Color(0xFF111B21)
                    ),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (statusText.isNotBlank()) {
                            val type = if (selectedTab == 0) "TEXT" else "IMAGE"
                            onUploadStatus(
                                statusText.trim(),
                                selectedImageUri,
                                selectedColorHex,
                                type
                            )
                            onDismiss()
                        }
                    },
                    enabled = statusText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldLight,
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_upload_status_button")
                ) {
                    Text(
                        text = "Share Status",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

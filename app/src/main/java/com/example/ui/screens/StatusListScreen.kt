package com.example.ui.screens

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.StatusEntity
import com.example.ui.components.UserAvatar
import com.example.ui.theme.EmeraldLight

@Composable
fun StatusListScreen(
    statuses: List<StatusEntity>,
    currentUserCode: String,
    currentUserName: String,
    currentUserAvatarHex: String,
    onOpenUploadStatus: () -> Unit,
    onStatusClick: (status: StatusEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val myStatus = statuses.firstOrNull { it.userCode == currentUserCode }
    val contactStatuses = statuses.filter { it.userCode != currentUserCode }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("status_list_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // My Status Section
            item {
                Text(
                    text = "My Status",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldLight,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (myStatus != null) {
                                onStatusClick(myStatus)
                            } else {
                                onOpenUploadStatus()
                            }
                        }
                        .testTag("my_status_card"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2C34)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = if (myStatus != null) 3.dp else 0.dp,
                                        color = EmeraldLight,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                UserAvatar(
                                    name = currentUserName,
                                    colorHex = currentUserAvatarHex,
                                    size = 48.dp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldLight)
                                    .clickable { onOpenUploadStatus() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Status",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "My Status",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            val subtitleText = if (myStatus != null) {
                                val timeAgo = DateUtils.getRelativeTimeSpanString(
                                    myStatus.timestamp,
                                    System.currentTimeMillis(),
                                    DateUtils.MINUTE_IN_MILLIS
                                ).toString()
                                "Tap to view • $timeAgo"
                            } else {
                                "Tap to add status update"
                            }

                            Text(
                                text = subtitleText,
                                fontSize = 13.sp,
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "New Photo Status",
                            tint = EmeraldLight,
                            modifier = Modifier
                                .clickable { onOpenUploadStatus() }
                                .padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Recent Updates Section
            item {
                Text(
                    text = "Recent Updates (${contactStatuses.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldLight,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (contactStatuses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No recent status updates from your contacts.",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(contactStatuses, key = { it.statusId }) { status ->
                    StatusItemRow(
                        status = status,
                        onClick = { onStatusClick(status) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onOpenUploadStatus,
            containerColor = EmeraldLight,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("upload_status_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Upload Status"
            )
        }
    }
}

@Composable
fun StatusItemRow(
    status: StatusEntity,
    onClick: () -> Unit
) {
    val ringColor = if (status.isViewed) Color.Gray else EmeraldLight

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("status_item_${status.statusId}"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2C34)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .border(
                        width = 3.dp,
                        color = ringColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                UserAvatar(
                    name = status.userName,
                    colorHex = status.userAvatarHex,
                    size = 46.dp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = status.userName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                val timeAgo = DateUtils.getRelativeTimeSpanString(
                    status.timestamp,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
                ).toString()

                Text(
                    text = "$timeAgo • \"${status.textContent}\"",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

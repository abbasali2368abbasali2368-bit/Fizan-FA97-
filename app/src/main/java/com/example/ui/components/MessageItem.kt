package com.example.ui.components

import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entities.MessageEntity
import com.example.ui.theme.ReadTickBlue
import com.example.ui.theme.ReceiverBubbleDark
import com.example.ui.theme.SenderBubbleDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageItem(
    message: MessageEntity,
    isFromMe: Boolean,
    modifier: Modifier = Modifier
) {
    val bubbleShape = if (isFromMe) {
        RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp)
    } else {
        RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp)
    }

    val bubbleBg = if (isFromMe) {
        SenderBubbleDark
    } else {
        ReceiverBubbleDark
    }

    val alignment = if (isFromMe) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(bubbleShape)
                .background(bubbleBg)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("message_bubble_${message.messageId}")
        ) {
            when (message.messageType) {
                "IMAGE" -> ImageMessageContent(message)
                "VIDEO" -> VideoMessageContent(message)
                "DOCUMENT" -> DocumentMessageContent(message)
                "VOICE" -> VoiceMessageContent(message)
                "EMOJI" -> EmojiMessageContent(message)
                else -> TextMessageContent(message)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Footer with timestamp, encryption badge, and delivery ticks
            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (message.isEncrypted) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Text(
                    text = formatTime(message.timestamp),
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )

                if (isFromMe) {
                    Spacer(modifier = Modifier.width(4.dp))
                    DeliveryTickIcon(status = message.deliveryStatus)
                }
            }
        }
    }
}

@Composable
private fun TextMessageContent(message: MessageEntity) {
    Text(
        text = message.text,
        color = Color.White,
        fontSize = 15.sp,
        lineHeight = 20.sp
    )
}

@Composable
private fun EmojiMessageContent(message: MessageEntity) {
    Text(
        text = message.text,
        fontSize = 32.sp
    )
}

@Composable
private fun ImageMessageContent(message: MessageEntity) {
    Column {
        message.mediaUri?.let { uri ->
            AsyncImage(
                model = Uri.parse(uri),
                contentDescription = "Attachment Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
        if (message.text.isNotBlank() && message.text != "📷 Photo") {
            Text(
                text = message.text,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun VideoMessageContent(message: MessageEntity) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = null,
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text("Video Attachment", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
            Text(message.text, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun DocumentMessageContent(message: MessageEntity) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.InsertDriveFile,
                contentDescription = null,
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text("Document File", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
            Text(message.text, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun VoiceMessageContent(message: MessageEntity) {
    var isPlaying by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        IconButton(
            onClick = { isPlaying = !isPlaying },
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Play Voice Message",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Voice Note", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Text("${message.voiceDurationSec}s", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { if (isPlaying) 0.6f else 0.0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun DeliveryTickIcon(status: String) {
    when (status) {
        "READ" -> Icon(
            imageVector = Icons.Default.DoneAll,
            contentDescription = "Read",
            tint = ReadTickBlue,
            modifier = Modifier.size(16.dp)
        )
        "DELIVERED" -> Icon(
            imageVector = Icons.Default.DoneAll,
            contentDescription = "Delivered",
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
        )
        else -> Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Sent",
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
        )
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

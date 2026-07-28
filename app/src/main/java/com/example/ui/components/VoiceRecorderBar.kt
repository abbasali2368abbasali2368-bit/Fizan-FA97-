package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldLight
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun VoiceRecorderBar(
    onCancel: () -> Unit,
    onSend: (durationSec: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var seconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            seconds++
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF202C33))
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("voice_recorder_bar"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onCancel,
            modifier = Modifier.testTag("cancel_recording_button")
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Cancel Recording",
                tint = Color(0xFFEF4444)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formatDuration(seconds),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Animated waveform bars simulation
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                repeat(8) {
                    val heightFactor = remember { Random.nextFloat() * 0.7f + 0.3f }
                    val animatedHeight by animateFloatAsState(
                        targetValue = if (seconds % 2 == 0) heightFactor else 1f - heightFactor,
                        label = "wave"
                    )
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height((24 * animatedHeight).dp)
                            .clip(CircleShape)
                            .background(EmeraldLight)
                    )
                }
            }
        }

        IconButton(
            onClick = { onSend(seconds) },
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(EmeraldLight)
                .testTag("send_voice_recording_button")
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send Voice Note",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun formatDuration(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return String.format("%02d:%02d", m, s)
}

package com.zello.sdk.example.app.screens.channels

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.GestureCancellationException
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.zello.sdk.ZelloChannel
import com.zello.sdk.ZelloOutgoingVoiceMessage

@Composable
fun ChannelsScreen(viewModel: ChannelsViewModel) {
    val channels = viewModel.channels.observeAsState().value ?: emptyList()
    val selectedContact = viewModel.selectedContact.observeAsState().value
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(channels) { channel ->
            ChannelItem(
                channel = channel,
                isSelected = selectedContact?.isSameAs(channel) ?: false,
                onClick = {viewModel.setSelectedContact(channel)},
                viewModel = viewModel
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ChannelItem(
    channel: ZelloChannel,
    isSelected: Boolean,
    onClick: () -> Unit, viewModel: ChannelsViewModel
) {
    val outgoingVoiceMessageViewState = viewModel.outgoingVoiceMessageViewState.observeAsState().value
    val incomingVoiceMessageViewState = viewModel.incomingVoiceMessageViewState.observeAsState().value
    val isSameContact = outgoingVoiceMessageViewState?.contact?.isSameAs(channel) == true
    val isConnecting = isSameContact && outgoingVoiceMessageViewState?.state == ZelloOutgoingVoiceMessage.State.CONNECTING
    val isTalking = isSameContact && outgoingVoiceMessageViewState?.state == ZelloOutgoingVoiceMessage.State.SENDING
    val isReceiving = incomingVoiceMessageViewState?.contact?.isSameAs(channel) == true
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Sign In",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = channel.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${channel.usersOnline} user${if (channel.usersOnline > 1) "s" else ""} connected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                IconButton(
                    onClick = {},
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Mute Channel",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            if (isSelected) {
                BoxWithConstraints (
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    val parentWidth = maxWidth
                    Box(
                        modifier = Modifier
                            .size(parentWidth)
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .pointerInput(Unit){
                                    detectTapGestures(
                                        onPress = {
                                            try {
                                                viewModel.startVoiceMessage(channel)
                                                tryAwaitRelease()
                                                viewModel.stopVoiceMessage()
                                            } catch (e: GestureCancellationException) {
                                                viewModel.stopVoiceMessage()
                                            }
                                        }
                                    )
                                }
                                .size(parentWidth * 0.6f)
                                .shadow(12.dp, shape = CircleShape)
                                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                                .border(8.dp, color = if (isTalking) Color.Red else if (isReceiving) Color.Green else MaterialTheme.colorScheme.primary, CircleShape)
                                .clip(CircleShape)
                            ,contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Microphone",
                                modifier = Modifier.size(parentWidth * 0.3f)
                            )
                        }
                    }
                }


            }
        }
    }
}

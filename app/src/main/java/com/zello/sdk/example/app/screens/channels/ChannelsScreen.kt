package com.zello.sdk.example.app.screens.channels

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.zello.sdk.ZelloChannel

@Composable
fun ChannelsScreen(viewModel: ChannelsViewModel) {
    val channels = viewModel.channels.observeAsState().value ?: emptyList()
    LazyColumn( modifier = Modifier.fillMaxSize() ) {
        items(channels) { channel ->
            ChannelItem(channel)
        }
    }
}

@Composable
fun ChannelItem(channel: ZelloChannel) {
    Text(text = channel.name)
}
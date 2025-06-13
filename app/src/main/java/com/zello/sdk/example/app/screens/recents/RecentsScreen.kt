package com.zello.sdk.example.app.screens.recents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zello.sdk.ZelloGroupConversation
import com.zello.sdk.ZelloRecentEntry
import com.zello.sdk.example.app.utils.TimeUtils

@Composable
fun RecentsScreen(viewModel: RecentsViewModel) {
    val recents = viewModel.recents.observeAsState().value ?: emptyList()
    LazyColumn( modifier = Modifier.fillMaxSize() ) {
        items(recents) { recent ->
            RecentItem(recent)
        }
    }
}

@Composable
fun RecentItem(recent: ZelloRecentEntry) {
    val contactName = when (val contact = recent.contact) {
        is ZelloGroupConversation -> contact.displayName
        else -> contact.name
    }
    val title = recent.channelUser?.displayName?.let { "$it : $contactName"} ?: contactName

    Row(modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = if (recent.incoming) Icons.AutoMirrored.Default.ArrowBack else Icons.AutoMirrored.Default.ArrowForward, contentDescription = "")
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title)
            Text(text = recent.type.toString())
            Text(text = TimeUtils.timestampToString(recent.timestamp))
        }
    }
}
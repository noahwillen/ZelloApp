package com.zello.sdk.example.app.screens.channels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.zello.sdk.ZelloChannel
import com.zello.sdk.ZelloRecentEntry
import com.zello.sdk.example.app.repositories.ZelloRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChannelsViewModel @Inject constructor(
    val zelloRepository: ZelloRepository
) : ViewModel() {
    private val _channels = zelloRepository.onChannelsUpdated.asLiveData()
    val channels: LiveData<List<ZelloChannel>> = _channels
}

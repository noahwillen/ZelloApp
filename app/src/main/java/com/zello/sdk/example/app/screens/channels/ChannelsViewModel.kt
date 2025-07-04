package com.zello.sdk.example.app.screens.channels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.zello.sdk.ZelloChannel
import com.zello.sdk.ZelloContact
import com.zello.sdk.ZelloRecentEntry
import com.zello.sdk.example.app.repositories.ZelloRepository
import com.zello.sdk.example.app.ui.shared.types.IncomingVoiceMessageViewState
import com.zello.sdk.example.app.ui.shared.types.OutgoingVoiceMessageViewState
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

    private val _selectedContact = zelloRepository.selectedContact.asLiveData()
    val selectedContact: LiveData<ZelloContact?> = _selectedContact

    fun setSelectedContact(channel: ZelloChannel) {
        zelloRepository.zello.setSelectedContact(channel)
    }

    fun startVoiceMessage(channel: ZelloChannel) {
        stopVoiceMessage()
        zelloRepository.zello.startVoiceMessage(channel)
    }

    fun stopVoiceMessage() {
        zelloRepository.zello.stopVoiceMessage()
    }

    private val _outgoingVoiceMessageViewState = MediatorLiveData<OutgoingVoiceMessageViewState>().apply {
        addSource(zelloRepository.outgoingVoiceMessage.asLiveData()) { outgoingMessage ->
            value = if (outgoingMessage != null) OutgoingVoiceMessageViewState(contact = outgoingMessage.contact, state = outgoingMessage.state)
            else null
        }
    }
    val outgoingVoiceMessageViewState: LiveData<OutgoingVoiceMessageViewState> = _outgoingVoiceMessageViewState

    private val _incomingVoiceMessageViewState = MediatorLiveData<IncomingVoiceMessageViewState>().apply {
        addSource(zelloRepository.incomingVoiceMessage.asLiveData()) { incomingMessage ->
            value = if (incomingMessage != null) IncomingVoiceMessageViewState(contact = incomingMessage.contact)
            else null
        }
    }
    val incomingVoiceMessageViewState: LiveData<IncomingVoiceMessageViewState> = _incomingVoiceMessageViewState
}

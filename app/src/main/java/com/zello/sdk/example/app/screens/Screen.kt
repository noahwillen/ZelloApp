package com.zello.sdk.example.app.screens

sealed class Screen(val rout: String) {
    object Recents: Screen("recents_screen")
    object Channels: Screen("channels_screen")
}
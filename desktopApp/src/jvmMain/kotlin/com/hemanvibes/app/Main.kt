package com.hemanvibes.app

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.hemanvibes.app.ui.App

fun main() = application {
    val windowState = rememberWindowState(width = 1100.dp, height = 750.dp)

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "HeMan Vibes"
    ) {
        App()
    }
}

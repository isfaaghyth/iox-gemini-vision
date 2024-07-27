package ui.uimodel

import ui.screen.IsAdded

sealed class AppEvent {
    data class AddPromptInChatStack(val command: String): AppEvent()
    data class RequestWithAttachment(val command: String, val image: ByteArray) : AppEvent()
    data class StartToListen(val isAdded: IsAdded): AppEvent()
    data object Reset : AppEvent()
}
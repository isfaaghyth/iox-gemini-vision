package ui.uimodel

sealed class AppEvent {
    data class BasicRequest(val command: String) : AppEvent()
    data class RequestWithAttachment(val command: String, val image: ByteArray) : AppEvent()
    data object Reset : AppEvent()
}
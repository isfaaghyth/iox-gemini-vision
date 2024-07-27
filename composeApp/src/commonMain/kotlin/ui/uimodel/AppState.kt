package ui.uimodel

data class AppStateModel(
    val chats: List<AppState> = emptyList()
)

interface AppState {
    val content: String
    val isModel: Boolean
}

class DefaultAppState(
    override val content: String = "",
    override val isModel: Boolean = true
) : AppState

data object LoadingAppState : AppState by DefaultAppState("...")
data object ErrorAppState : AppState by DefaultAppState()
data object ListeningAppState : AppState by DefaultAppState(
    content = "Saya mendengar...",
    isModel = false
)

data class ChatItemModel(
    override val content: String = "",

    /**
     * isModel uses for chat alignment, where:
     * true -> comes from Gemini response
     * false -> prompt input from user
     */
    override val isModel: Boolean = true
) : AppState
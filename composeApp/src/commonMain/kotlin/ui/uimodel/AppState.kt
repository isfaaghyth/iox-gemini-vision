package ui.uimodel

const val LOADING_CONTENT = "..."

interface AppState {
    val content: String
    val isModel: Boolean
}

data class AppStateModel(
    val chats: List<AppState> = emptyList()
)

data object LoadingAppState : AppState {
    override val content: String get() = LOADING_CONTENT
    override val isModel: Boolean get() = true
}

data object ErrorAppState : AppState {
    override val content: String get() = ""
    override val isModel: Boolean get() = true
}

data class ChatItemModel(
    override val content: String = "",

    /**
     * isModel uses for chat alignment, where:
     * true -> comes from Gemini response
     * false -> prompt input from user
     */
    override val isModel: Boolean = true
) : AppState
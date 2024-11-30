package ui

data class GeminiUiModel(
    val chats: List<UiState>,
    val networkIssue: String
) {

    companion object {
        val Default get() = GeminiUiModel(
            chats = listOf(),
            networkIssue = ""
        )
    }
}

interface UiState {
    val content: String
    val isModel: Boolean
}

class DefaultUiState(
    override val content: String = "",
    override val isModel: Boolean = true
) : UiState

data object PrepareUiState : UiState by DefaultUiState(
    content = "Speech Recognition is starting...",
    isModel = false
)

data object LoadingUiState : UiState by DefaultUiState("...")

data class ErrorUiState(val message: String) : UiState by DefaultUiState(
    content = message,
    isModel = false
) {

    companion object {
        val EmptySpeak get() = ErrorUiState("I couldn't hear you 🥲")
        val Network get() = ErrorUiState("Network issue! Please try again.")
    }
}

data object ListeningUiState : UiState by DefaultUiState(
    content = "I'm listening...",
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
) : UiState
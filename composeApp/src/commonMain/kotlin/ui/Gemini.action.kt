package ui

sealed class GeminiEvent {
    data object StartToSpeakClicked : GeminiEvent()
}

sealed class GeminiGlobalEffect {
    // -- Global --
    data class TriggerTextToSpeech(val text: String) : GeminiGlobalEffect()
    data object None : GeminiGlobalEffect()
}

sealed class GeminiEffect {
    // -- Internal --
    data object ListenStackAdded : GeminiEffect()
    data object ListenStackRemoved : GeminiEffect()

    data class PromptStackAdded(
        val prompt: String
    ) : GeminiEffect()

    data class PromptRequested(
        val prompt: String,
        val image: ByteArray
    ) : GeminiEffect()
}
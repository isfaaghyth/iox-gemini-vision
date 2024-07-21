package utils

import androidx.compose.runtime.Composable
import io.ktor.client.plugins.logging.Logger

@Composable
expect fun HideKeyboard()

@Composable
expect fun TextToSpeech(content: String)

expect object KtorLogging : Logger
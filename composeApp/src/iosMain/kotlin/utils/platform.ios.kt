package utils

import androidx.compose.runtime.Composable
import io.ktor.client.plugins.logging.Logger
import platform.AVFAudio.AVSpeechSynthesisVoice
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance

@Composable
actual fun HideKeyboard() {} // TODO

@Composable
actual fun TextToSpeech(content: String) {
    val utterance = AVSpeechUtterance(content)

    utterance.voice = AVSpeechSynthesisVoice.voiceWithLanguage("id")
    utterance.rate = 0.1f

    val synthesizer = AVSpeechSynthesizer()
    synthesizer.speakUtterance(utterance)
}

actual object KtorLogging : Logger {

    override fun log(message: String) {
        println(message)
    }
}
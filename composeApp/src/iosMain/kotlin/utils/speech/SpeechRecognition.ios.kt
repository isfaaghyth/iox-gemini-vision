package utils.speech

import androidx.compose.runtime.Composable

actual class SpeechRecognition actual constructor(
    private val listener: SpeechRecognitionListener
) : SpeechRecognitionHandler {

    @Composable
    override fun onStartToSpeech() {

    }
}
package utils.speech

import androidx.compose.runtime.Composable

expect class SpeechRecognition(listener: SpeechRecognitionListener) : SpeechRecognitionHandler

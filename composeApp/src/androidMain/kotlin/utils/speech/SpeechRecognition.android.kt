package utils.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

actual class SpeechRecognition actual constructor(
    private val listener: SpeechRecognitionListener
) : SpeechRecognitionHandler {

    private var recognizer: SpeechRecognizer? = null
    private var intent: Intent? = null

    init {
        setupRecognizerIntent()
    }

    @Composable
    override fun onStartToSpeech() {
        val context = LocalContext.current

        onResetSpeechRecognizer(context)
        startListening()
    }

    private fun startListening() {
        recognizer?.startListening(intent)
    }

    private fun setupRecognizerIntent() {
        intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, LOCALE)
        intent?.putExtra(RecognizerIntent.EXTRA_LANGUAGE, LOCALE)
        intent?.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent?.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
    }

    private fun onResetSpeechRecognizer(context: Context) {
        if (recognizer != null) recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(context)

        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            recognizer?.setRecognitionListener(recognitionListener)
        } else {
            listener.onError("Failed to instance Speech Recognizer," +
                    "please check permission nor audio input in your device.")
        }
    }

    private val recognitionListener = object : RecognitionListener {

        override fun onBeginningOfSpeech() {
            sendDebugLog("onBeginningOfSpeech")
            listener.onSpeechReady()
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            sendDebugLog(buffer.toString())
        }

        override fun onEndOfSpeech() {
            sendDebugLog("onEndOfSpeech")
            recognizer?.stopListening()
            listener.onSpeechEnd()
        }

        override fun onError(error: Int) {
            listener.onError(getErrorMessageBy(error))
        }

        override fun onResults(results: Bundle?) {
            if (results == null) {
                listener.onError("Failed to get the voice result")
                return
            }

            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: listOf()

            var resultAsString = ""
            for (result in matches) resultAsString += "$result ".trim()
            sendDebugLog(resultAsString)
            listener.onResult(resultAsString)
        }

        override fun onPartialResults(partialResults: Bundle?) = Unit
        override fun onEvent(eventType: Int, params: Bundle?) = Unit
        override fun onReadyForSpeech(params: Bundle?) = Unit
        override fun onRmsChanged(rmsdB: Float) = Unit
    }

    private fun getErrorMessageBy(code: Int): String {
        val message: String = when (code) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED -> "Language Not supported"
            SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE -> "Language Unavailable"
            else -> "Didn't understand, please try again."
        }
        return message
    }

    private fun sendDebugLog(message: String) {
        println("Gemini-App: $message")
    }

    companion object {
        private const val LOCALE = "id"
    }
}
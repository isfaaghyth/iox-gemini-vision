package utils.speech

interface SpeechRecognitionListener {
    fun onSpeechReady()
    fun onSpeechEnd()
    fun onResult(result: String)
    fun onError(message: String)
}
package utils.speech

interface SpeechRecognitionHandler {
    fun onStartToSpeech()
    fun observeListener(listener: SpeechRecognitionListener)
}

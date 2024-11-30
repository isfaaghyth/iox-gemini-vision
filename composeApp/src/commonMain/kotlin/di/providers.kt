package di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import domain.domain.GetContentWithImageUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ui.GeminiViewModel
import utils.AppCoroutineDispatchers
import utils.speech.SpeechRecognition

object Providers : KoinComponent {

    private val getContentWithImageUseCase: GetContentWithImageUseCase by inject()
    private val speechRecognition: SpeechRecognition by inject()

    fun speechRecognitionInstance() = speechRecognition

    @Composable
    fun viewModel() = viewModel {
        GeminiViewModel(
            getContentWithImageUseCase,
            AppCoroutineDispatchers
        )
    }
}
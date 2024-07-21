package di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import domain.usecase.GetContentUseCase
import domain.usecase.GetContentWithImageUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ui.AppViewModel

object Providers : KoinComponent {

    private val getContentUseCase: GetContentUseCase by inject()
    private val getContentWithImageUseCase: GetContentWithImageUseCase by inject()

    @Composable
    fun viewModel() = viewModel { AppViewModel(getContentUseCase, getContentWithImageUseCase) }
}
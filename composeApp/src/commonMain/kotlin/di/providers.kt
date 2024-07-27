package di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import domain.domain.GetContentWithImageUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ui.AppViewModel
import utils.AppCoroutineDispatchers

object Providers : KoinComponent {

    private val getContentWithImageUseCase: GetContentWithImageUseCase by inject()

    @Composable
    fun viewModel() = viewModel {
        AppViewModel(
            getContentWithImageUseCase,
            AppCoroutineDispatchers
        )
    }
}
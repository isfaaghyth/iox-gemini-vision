package ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.ContentModel
import domain.usecase.GetContentUseCase
import domain.usecase.GetContentWithImageUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.uimodel.AppEvent
import ui.uimodel.AppState
import ui.uimodel.AppStateModel
import ui.uimodel.ChatItemModel
import ui.uimodel.ErrorAppState
import ui.uimodel.LoadingAppState
import utils.CoroutineDispatchers
import utils.lruAdd

class AppViewModel(
    private val getContentUseCase: GetContentUseCase,
    private val getContentWithImageUseCase: GetContentWithImageUseCase,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    private val _event = MutableSharedFlow<AppEvent>(replay = 50)
    private val _chatList = mutableStateListOf<AppState>()

    private val _state = MutableStateFlow(AppStateModel())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _event.distinctUntilChanged()
                .collect(::update)
        }
    }

    fun sendAction(event: AppEvent) {
        _event.tryEmit(event)
    }

    private fun update(event: AppEvent) {
        when (event) {
            is AppEvent.AddPromptInChatStack -> {
                setAndUpdateChatList(event.command, false)
                setAndUpdateList(LoadingAppState)
            }
            is AppEvent.BasicRequest -> onBasicRequest(event.command)
            is AppEvent.RequestWithAttachment -> onRequestWithImageAttachment(event.command, event.image)
            is AppEvent.Reset -> disposeLastState()
        }
    }

    private fun onBasicRequest(command: String) {
        viewModelScope.launch {
            val result = getContentUseCase(command)

            withContext(dispatchers.main) {
                onRequestProceed(result)
            }
        }
    }

    private fun onRequestWithImageAttachment(command: String, image: ByteArray) {
        viewModelScope.launch {
            val result = getContentWithImageUseCase(command, image)

            withContext(dispatchers.main) {
                onRequestProceed(result)
            }
        }
    }

    private fun onRequestProceed(result: ContentModel) {
        removeLoadingAndErrorUiModel()

        if (result.succeed) {
            setAndUpdateChatList(result.text, true)
        } else {
            setAndUpdateList(ErrorAppState)
        }
    }

    private fun setAndUpdateChatList(content: String, isModel: Boolean) {
        val model = ChatItemModel(
            content = content,
            isModel = isModel
        )

        setAndUpdateList(model)
    }

    private fun setAndUpdateList(item: AppState) {
        _chatList.lruAdd(item)
        _state.update { it.copy(chats = _chatList) }
    }

    private fun removeLoadingAndErrorUiModel() {
        _chatList.removeAll { it is LoadingAppState || it is ErrorAppState }
    }

    private fun disposeLastState() {
        _state.update { AppStateModel() }
    }
}
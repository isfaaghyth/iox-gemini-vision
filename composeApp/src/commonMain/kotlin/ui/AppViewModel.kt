package ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.ContentModel
import domain.usecase.GetContentUseCase
import domain.usecase.GetContentWithImageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.uimodel.ErrorAppState
import ui.uimodel.AppEvent
import ui.uimodel.AppStateModel
import ui.uimodel.ChatItemModel
import ui.uimodel.LoadingAppState
import ui.uimodel.AppState
import utils.lruAdd

class AppViewModel(
    private val getContentUseCase: GetContentUseCase,
    private val getContentWithImageUseCase: GetContentWithImageUseCase
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
            is AppEvent.BasicRequest -> {
                setAndUpdateList(LoadingAppState)
                basicRequestWithCommand(event.command)
            }
            is AppEvent.RequestWithAttachment -> {
                setAndUpdateList(LoadingAppState)
                setAndUpdateChatList(event.command, false)
                imageAttachmentRequestWithCommand(event.command, event.image)
            }
            is AppEvent.Reset -> disposeLastState()
        }
    }

    private fun basicRequestWithCommand(command: String) {
        viewModelScope.launch {
            val result = getContentUseCase(command)

            withContext(Dispatchers.Main) {
                onRequestProceed(result)
            }
        }
    }

    private fun imageAttachmentRequestWithCommand(command: String, image: ByteArray) {
        viewModelScope.launch {
            val result = getContentWithImageUseCase(command, image)

            withContext(Dispatchers.Main) {
                onRequestProceed(result)
            }
        }
    }

    private fun onRequestProceed(result: ContentModel) {
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

        removeLoadingAndErrorUiModel()
        setAndUpdateList(model, isModel)
    }

    private fun setAndUpdateList(item: AppState, isModel: Boolean = false) {
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
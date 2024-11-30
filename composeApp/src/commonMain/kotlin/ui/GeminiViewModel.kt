package ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.domain.GetContentWithImageUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.CoroutineDispatchers
import utils.lruAdd

class GeminiViewModel(
    private val getContentWithImageUseCase: GetContentWithImageUseCase,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    private val _event = MutableSharedFlow<GeminiEvent>(replay = 50)
    private val _chatList = mutableStateListOf<UiState>()

    private val _effect = Channel<GeminiEffect>(Channel.UNLIMITED)

    private val _globalEffect = Channel<GeminiGlobalEffect>(Channel.UNLIMITED)
    val globalEffect: StateFlow<GeminiGlobalEffect>
        get() = _globalEffect
            .receiveAsFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = GeminiGlobalEffect.None
            )

    private val _state = MutableStateFlow(GeminiUiModel.Default)
    val state: StateFlow<GeminiUiModel> get() = _state.asStateFlow()

    private var requestJob: Job? = null

    init {
        viewModelScope.launch {
            _event
                .distinctUntilChanged()
                .collect(::event)
        }

        viewModelScope.launch {
            _effect
                .receiveAsFlow()
                .collect(::effect)
        }
    }

    fun sideEffect(effect: GeminiEffect) {
        _effect.trySend(effect)
    }

    fun sendAction(event: GeminiEvent) {
        _event.tryEmit(event)
    }

    private fun event(event: GeminiEvent) {
        when (event) {
            is GeminiEvent.StartToSpeakClicked -> {
                createStateAndAddChatStack(PrepareUiState)
            }
        }
    }

    private fun effect(effect: GeminiEffect) {
        when (effect) {
            is GeminiEffect.ListenStackAdded -> {
                removeStateByType<PrepareUiState>()
                createStateAndAddChatStack(ListeningUiState)
            }

            is GeminiEffect.ListenStackRemoved -> {
                removeStateByType<ListeningUiState>()
            }

            is GeminiEffect.PromptStackAdded -> {
                createOrUpdateChatItem(effect.prompt, false)
            }

            is GeminiEffect.PromptRequested -> {
                onRequestWithImageAttachment(
                    effect.prompt,
                    effect.image
                )
            }
        }
    }

    private fun onRequestWithImageAttachment(command: String, image: ByteArray) {
        if (command.isEmpty()) {
            createStateAndAddChatStack(ErrorUiState.EmptySpeak)
            return
        }

        // Show Loading state
        createStateAndAddChatStack(LoadingUiState)

        // Request prompt
        requestJob?.cancel()
        requestJob = viewModelScope.launch(dispatchers.io) {
            val result = getContentWithImageUseCase(command, image)

            withContext(dispatchers.main) {
                // Remove Loading or Error state
                removeLoadingOrErrorStateOnChatStack()

                if (result.succeed) {
                    _globalEffect.trySend(GeminiGlobalEffect.TriggerTextToSpeech(result.text))
                    createOrUpdateChatItem(result.text, true)
                } else {
                    createStateAndAddChatStack(ErrorUiState.Network)
                }
            }
        }
    }

    private fun createOrUpdateChatItem(chat: String, isModel: Boolean) {
        val model = ChatItemModel(
            content = chat,
            isModel = isModel
        )

        createStateAndAddChatStack(model)
    }

    private inline fun <reified T : UiState> removeStateByType() {
        val element = _chatList.find { it is T }
        val index = _chatList.indexOf(element)

        if (index != -1) _chatList.removeAt(index)

        _state.update { it.copy(chats = _chatList) }
    }

    private fun createStateAndAddChatStack(state: UiState) {
        _chatList.lruAdd(state)
        _state.update { it.copy(chats = _chatList) }
    }

    private fun removeLoadingOrErrorStateOnChatStack() {
        _chatList.removeAll {
            it is LoadingUiState ||
                    it is PrepareUiState ||
                    it is ErrorUiState ||
                    it is ListeningUiState
        }
    }
}
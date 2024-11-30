package ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import ui.UiState
import ui.component.ChatItem
import ui.component.PromptBoxContainer
import utils.speech.SpeechRecognition
import utils.speech.SpeechRecognitionListener

@Composable
fun MainScreen(
    chats: List<UiState>,
    modifier: Modifier = Modifier,
    speechRecognition: SpeechRecognition,
    onStartToSpeakClicked: () -> Unit,
    onPromptChatAdded: (String) -> Unit,
    onListeningState: (Boolean) -> Unit,
    onPromptRequest: (ByteArray?, String) -> Unit
) {
    /**
     * These variables handled the speech recognition.
     */
    var speechResult by rememberSaveable { mutableStateOf("") }

    // Camera state to get ByteArray result
    val state = rememberPeekabooCameraState(
        onCapture = { onPromptRequest(it, speechResult) }
    )

    // Speech state for voice recognition
    LaunchedEffect(Unit) {
        speechRecognition.observeListener(
            object : SpeechRecognitionListener {

                override fun onSpeechReady() {
                    onListeningState(true)
                }

                override fun onSpeechEnd() {
                    state.capture()
                    onListeningState(false)
                }

                override fun onResult(result: String) {
                    speechResult = result
                    onPromptChatAdded(result)
                }

                override fun onError(message: String) {}
            }
        )
    }

    val listState = rememberLazyListState()

    LaunchedEffect(chats.size) {
        listState.animateScrollToItem(chats.size)
    }

    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (promptBox, camera, messages) = createRefs()

        // Camera
        PeekabooCamera(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .scale(2f)
                .constrainAs(camera) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            permissionDeniedContent = {},
        )

        // Chat
        LazyColumn(
            state = listState,
            reverseLayout = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(800.dp)
                .constrainAs(messages) {
                    bottom.linkTo(promptBox.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentPadding = PaddingValues(
                horizontal = 18.dp
            )
        ) {
            items(chats) { ChatItem(it) }
        }

        // PromptBox
        PromptBoxContainer(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(promptBox) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                onClick = {
                    speechRecognition.onStartToSpeech()
                    onStartToSpeakClicked()
                },
            ) {
                Text(text = "Click to talk")
            }
        }
    }
}

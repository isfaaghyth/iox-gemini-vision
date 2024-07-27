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
import ui.screen.component.ChatItem
import ui.screen.component.ChatWaitingDots
import ui.screen.component.PromptBoxContainer
import ui.uimodel.AppState
import ui.uimodel.ChatItemModel
import ui.uimodel.LoadingAppState
import utils.TextToSpeech
import utils.speech.SpeechRecognition
import utils.speech.SpeechRecognitionListener
import kotlin.jvm.JvmInline

@JvmInline
value class IsAdded(val value: Boolean)

@Composable
fun MainScreen(
    chats: List<AppState>,
    modifier: Modifier = Modifier,
    onPromptChatAdded: (String) -> Unit,
    onListeningState: (IsAdded) -> Unit,
    onPromptRequest: (ByteArray?, String) -> Unit
) {
    /**
     * Both variables area to handled the speech recognition.
     */
    var triggerStartToSpeech by rememberSaveable { mutableStateOf(false) }
    var speechResult by rememberSaveable { mutableStateOf("") }

    // A state used to trigger to text-to-speech
    var lastRespond by rememberSaveable { mutableStateOf("") }

    // Camera state to get ByteArray result
    val state = rememberPeekabooCameraState(
        onCapture = {
            onPromptRequest(it, speechResult)
        }
    )

    // Speech state for voice recognition
    val speech = SpeechRecognition(object : SpeechRecognitionListener {

        override fun onSpeechReady() {
            onListeningState(IsAdded(true))
        }

        override fun onSpeechEnd() {
            onListeningState(IsAdded(false))
        }

        override fun onResult(result: String) {
            speechResult = result
            triggerStartToSpeech = false

            onPromptChatAdded(result)
            state.capture()
        }

        override fun onError(message: String) {
            triggerStartToSpeech = false
        }
    })

    if (lastRespond.isNotEmpty()) {
        TextToSpeech(lastRespond)
        lastRespond = ""
    }

    if (triggerStartToSpeech) {
        speech.onStartToSpeech()
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
            items(chats) {
                ChatItem(it)

                // trigger text to speech
                if (it.isModel && it is ChatItemModel) {
                    lastRespond = it.content
                }
            }
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
                onClick = {
                    triggerStartToSpeech = true
                }
            ) {
                Text("Click to talk")
            }
        }
    }
}

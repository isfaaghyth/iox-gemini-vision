package ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import ui.uimodel.LOADING_CONTENT
import ui.uimodel.AppState
import utils.HideKeyboard
import utils.TextToSpeech

@Composable
fun MainScreen(
    chats: List<AppState>,
    onPromptRequest: (ByteArray?, String) -> Unit
) {
    var prompt by rememberSaveable { mutableStateOf("") }
    var latestContentToSpeech by rememberSaveable { mutableStateOf("") }
    var keyboardVisibility by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val state = rememberPeekabooCameraState(
        onCapture = {
            onPromptRequest(it, prompt)
        }
    )

    if (keyboardVisibility) {
        HideKeyboard()
        keyboardVisibility = false
    }

    if (latestContentToSpeech.isNotEmpty() && latestContentToSpeech != LOADING_CONTENT) {
        TextToSpeech(latestContentToSpeech)
        latestContentToSpeech = ""
    }

    LaunchedEffect(chats.size) {
        listState.animateScrollToItem(chats.size)
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (overlay, promptBox, camera, messages) = createRefs()

        // Cemara
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
            items(chats) { chat ->
                latestContentToSpeech = chat.content
                ChatItem(chat)
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
            state.capture()
            keyboardVisibility = !keyboardVisibility
            prompt = it
        }
    }
}

@Composable
fun PromptBoxContainer(
    modifier: Modifier = Modifier,
    onSendChatClickListener: (String) -> Unit
) {
    Row(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PromptBox(
            onSendChatClickListener = { onSendChatClickListener(it) }
        )
    }
}

@Composable
fun ChatItem(chat: AppState) {
    AnimatedVisibility(chat.content.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(if (chat.isModel) Alignment.End else Alignment.Start)
                    .clip(
                        RoundedCornerShape(
                            topStart = 48f,
                            topEnd = 48f,
                            bottomStart = if (chat.isModel) 48f else 0f,
                            bottomEnd = if (chat.isModel) 0f else 48f
                        )
                    )
                    .background(if (chat.isModel) Color.Magenta else Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = chat.content,
                    fontSize = 14.sp,
                    color = if (chat.isModel) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
fun PromptBox(
    onSendChatClickListener: (String) -> Unit
) {
    var chatBoxValue by remember { mutableStateOf(TextFieldValue("")) }

    Row {
        TextField(
            value = chatBoxValue,
            onValueChange = { newText -> chatBoxValue = newText },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = "Write your prompt here...") },
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
        )
        IconButton(
            onClick = {
                val msg = chatBoxValue.text
                if (msg.isBlank()) return@IconButton

                onSendChatClickListener(chatBoxValue.text)
                chatBoxValue = TextFieldValue("")
            },
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.Magenta)
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .padding(start = 4.dp)
            )
        }
    }
}
package ui.screen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.uimodel.AppState
import ui.uimodel.LoadingAppState

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
                    .background(if (chat.isModel) Color(0xFF2f2f2f) else Color.White)
                    .padding(16.dp)
            ) {
                if (chat is LoadingAppState) {
                    ChatWaitingDots()
                } else {
                    ChatText(chat.isModel, chat.content)
                }
            }
        }
    }
}

@Composable
fun ChatText(
    isModel: Boolean,
    chat: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = chat,
        fontSize = 14.sp,
        modifier = modifier,
        color = if (isModel) Color.White else Color.Black
    )
}
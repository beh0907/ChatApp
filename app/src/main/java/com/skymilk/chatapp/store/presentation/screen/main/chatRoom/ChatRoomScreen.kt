package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import BottomSheetImagePicker
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components.ChatMessageList
import com.skymilk.chatapp.ui.theme.Black
import com.skymilk.chatapp.ui.theme.HannaPro
import com.skymilk.chatapp.utils.ComposeFileProvider
import com.skymilk.chatapp.utils.PermissionUtil
import kotlinx.coroutines.launch

@Composable
fun ChatRoomScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatRoomViewModel,
    chatRoom: ChatRoomWithUsers,
    currentUser: User,
    onNavigateToBack: () -> Unit
) {
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        TopSection(
            onNavigateToBack = onNavigateToBack, chatRoom = chatRoom, currentUser = currentUser
        )

        // ChatRoomMessages 영역
        ChatMessageList(
            modifier = Modifier
                .weight(1f) // 키보드가 올라오면 이 영역이 줄어듬
                .fillMaxWidth(),
            chatRoom = chatRoom,
            chatMessages = chatMessages,
            currentUser = currentUser
        )

        BottomSection(
            modifier = Modifier.imePadding(),
            onSendMessage = viewModel::sendMessage,
            onSendImageMessage = viewModel::sendImageMessage,
            userId = currentUser.id,
        )
    }

}


//상단 타이틀
@Composable
fun TopSection(onNavigateToBack: () -> Unit, chatRoom: ChatRoomWithUsers, currentUser: User) {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Black

    val title = when (chatRoom.participants.size) {
        1 -> ""
        2 -> chatRoom.participants.find { it.id != currentUser.id }?.username ?: ""
        else -> "그룹채팅"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            onNavigateToBack()
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null,
                tint = uiColor
            )
        }

        Text(
            modifier = Modifier.weight(1f),
            text = title,
            fontFamily = HannaPro,
            style = MaterialTheme.typography.titleLarge,
            color = uiColor
        )
    }
}


//하단 텍스트 입력
@Composable
fun BottomSection(
    modifier: Modifier = Modifier,
    onSendMessage: (String, String) -> Unit,
    onSendImageMessage: (String, Uri) -> Unit,
    userId: String,
) {
    var message by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val uiColor = if (isSystemInDarkTheme()) Color.White else Black

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    //이미지 갤러리에서 가져오기
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onSendImageMessage(userId, uri)
        }
    }

    //카메라 이미지 가져오기
    val cameraUri = remember { mutableStateOf(ComposeFileProvider.getImageUri(context)) }
    val cameraCapture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            Log.d(
                "rememberLauncherForActivityResult",
                ComposeFileProvider.getImageUri(context).toString()
            )
            onSendImageMessage(userId, cameraUri.value)
        }
    }

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

        IconButton(onClick = { showBottomSheet = true }) {
            Icon(
                modifier = Modifier.size(36.dp),
                imageVector = Icons.Default.AttachFile,
                contentDescription = null,
                tint = uiColor
            )
        }

        OutlinedTextField(
            modifier = Modifier.weight(1f),
            singleLine = true,
            value = message,
            onValueChange = { message = it },
            label = {
                Text(
                    text = "채팅을 입력해주세요.",
                    style = MaterialTheme.typography.labelMedium,
                    color = uiColor,
                )
            },
            keyboardActions = KeyboardActions(onDone = {
                //메시지 전송
                onSendMessage(userId, message)

                //텍스트 필드 초기화
                message = ""

                //키보드 숨기기
                keyboardController?.hide()
            })
        )

        IconButton(
            onClick = {
                //메시지 전송
                onSendMessage(userId, message)

                //텍스트 필드 초기화
                message = ""

                //키보드 숨기기
                keyboardController?.hide()
            }, enabled = message.isNotBlank()
        ) {
            Icon(
                modifier = Modifier.size(36.dp),
                imageVector = Icons.AutoMirrored.Default.Send,
                contentDescription = null,
                tint = uiColor
            )
        }
    }

    //바텀시트
    BottomSheetImagePicker(isVisible = showBottomSheet,
        onDismiss = { showBottomSheet = false },
        onImagePicker = {
            scope.launch {
                if (PermissionUtil.requestStoragePermissions()) {
                    imagePicker.launch("image/*")
                }
            }

        },
        onCameraCapture = {
            scope.launch {
                if (PermissionUtil.requestCameraPermissions()) {
                    cameraCapture.launch(cameraUri.value)
                }
            }
        })
}
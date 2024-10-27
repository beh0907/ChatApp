package com.skymilk.chatapp.store.presentation.screen.main.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PersonRemoveAlt1
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.skymilk.chatapp.R
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.CustomAlertDialog
import com.skymilk.chatapp.store.presentation.common.squircleClip

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel,
    onEvent: (ProfileEvent) -> Unit,
    user: User,
    loginUserId: String,
    onNavigateToBack: () -> Unit,
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToChatRoom: (String) -> Unit,
    onNavigateToImageViewer: (String) -> Unit,
    onSignOut: () -> Unit
) {
    var visibleSignOutDialog by rememberSaveable { mutableStateOf(false) }

    val chatRoomId by viewModel.chatRoomId.collectAsStateWithLifecycle()

    LaunchedEffect(chatRoomId) {
        chatRoomId?.let {
            onNavigateToChatRoom(it)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {

        //상단 아이콘
        TopSection(
            modifier = Modifier, onNavigateToBack = onNavigateToBack
        )

        //프로필 정보
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            UserProfileSection(
                user = user, onNavigateToImageViewer = onNavigateToImageViewer
            )

            HorizontalDivider(
                color = Color.White
            )

            // 로그인한 아이디와 선택한 유저 아이디를 비교
            // 이벤트 UI과 동작을 별개로 구현
            when (user.id == loginUserId) {
                true -> {
                    MyProfileEventSection(onNavigateToMyChatRoom = {
                        onEvent(
                            ProfileEvent.GetChatRoomId(
                                listOf(user.id)
                            )
                        )
                    },
                        onNavigateToProfileEdit = onNavigateToProfileEdit,
                        visibleSignOutDialog = {
                            visibleSignOutDialog = true
                        })
                }

                else -> {
                    OtherProfileEventSection(
                        viewModel = viewModel,
                        onNavigateToMyChatRoom = {
                            onEvent(ProfileEvent.GetChatRoomId(listOf(user.id, loginUserId)))
                        },
                        getFriendState = {
                            onEvent(ProfileEvent.GetFriendState(loginUserId, user.id))
                        },
                        setFriendState = { state ->
                            onEvent(ProfileEvent.SetFriendState(loginUserId, user.id, state))
                        }
                    )
                }
            }
        }
    }

    if (visibleSignOutDialog)
        CustomAlertDialog(
            message = "로그아웃 하시겠습니까?",
            onConfirm = { onSignOut() },
            onDismiss = { visibleSignOutDialog = false }
        )
}

@Composable
fun TopSection(
    modifier: Modifier = Modifier, onNavigateToBack: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { onNavigateToBack() }) {
            Icon(
                imageVector = Icons.Rounded.Close, contentDescription = null, tint = Color.White
            )
        }
    }
}

@Composable
fun UserProfileSection(
    user: User,
    onNavigateToImageViewer: (String) -> Unit,
) {
    //프로필 이미지
    Surface(
        shadowElevation = 4.dp, modifier = Modifier.squircleClip()
    ) {
        AsyncImage(
            modifier = Modifier
                .size(120.dp)
                .squircleClip()
                .clickable {
                    //프로필 이미지가 있다면 이동
                    user.profileImageUrl?.let {
                        onNavigateToImageViewer(it)
                    }
                },
            model = ImageRequest.Builder(LocalContext.current).data(
                if (user.profileImageUrl.isBlank()) R.drawable.bg_default_profile
                else user.profileImageUrl
            ).decoderFactory(SvgDecoder.Factory()).build(),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    //유저 이름
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            style = MaterialTheme.typography.titleLarge,
            text = user.username,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Icon(
            modifier = Modifier.align(Alignment.CenterEnd),
            imageVector = Icons.Rounded.Edit,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )

    }

    Spacer(modifier = Modifier.height(34.dp))


    //상태 메시지
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
    ) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            style = MaterialTheme.typography.titleMedium,
            text = user.statusMessage,
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Icon(
            modifier = Modifier.align(Alignment.CenterEnd),
            imageVector = Icons.Rounded.Edit,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun MyProfileEventSection(
    onNavigateToMyChatRoom: () -> Unit,
    onNavigateToProfileEdit: () -> Unit,
    visibleSignOutDialog: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        //나와의 채팅
        Column(modifier = Modifier
            .fillMaxHeight()
            .clickable {
                onNavigateToMyChatRoom()
            }
            .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.ChatBubble,
                contentDescription = null,
                tint = Color.White
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "나와의 채팅",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                color = Color.White
            )
        }

        //프로필 편집
        Column(modifier = Modifier
            .fillMaxHeight()
            .clickable {
                onNavigateToProfileEdit()
            }
            .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.Edit, contentDescription = null, tint = Color.White
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "프로필 편집",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                color = Color.White
            )
        }

        //로그아웃
        Column(modifier = Modifier
            .fillMaxHeight()
            .clickable {
                visibleSignOutDialog()
            }
            .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Logout,
                contentDescription = null,
                tint = Color.White
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "로그아웃",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                color = Color.White
            )
        }
    }
}

@Composable
fun OtherProfileEventSection(
    viewModel: ProfileViewModel,
    onNavigateToMyChatRoom: () -> Unit,
    getFriendState: () -> Unit,
    setFriendState: (Boolean) -> Unit,
) {
    //친구 상태 정보
    val isFriendState by viewModel.isFriendState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        getFriendState()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        //나와의 채팅
        Column(modifier = Modifier
            .fillMaxHeight()
            .clickable {
                onNavigateToMyChatRoom()
            }
            .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.ChatBubble,
                contentDescription = null,
                tint = Color.White
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "1:1 채팅",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                color = Color.White
            )
        }

        when (isFriendState) {
            is ProfileState.Success -> {
                val isFriend = (isFriendState as ProfileState.Success).isFriend

                //친구 추가
                Column(modifier = Modifier
                    .fillMaxHeight()
                    .clickable {
                        setFriendState(isFriend)
                    }
                    .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (isFriend) Icons.Rounded.PersonRemoveAlt1 else Icons.Default.PersonAddAlt1,
                        contentDescription = null,
                        tint = Color.White
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = if (isFriend) "친구 삭제" else "친구 추가",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        color = Color.White
                    )
                }
            }

            else -> {
                // 로딩 중 또는 오류 처리
                CircularProgressIndicator(color = Color.White)
            }
        }

        //미구현
//        Column(
//            modifier = Modifier
//                .fillMaxHeight()
//                .clickable {
//                }
//                .padding(horizontal = 10.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Icon(
//                imageVector = Icons.Rounded.DeviceUnknown,
//                contentDescription = null,
//                tint = Color.White
//            )
//
//            Spacer(Modifier.height(10.dp))
//
//            Text(
//                text = "미구현",
//                style = MaterialTheme.typography.titleSmall,
//                maxLines = 1,
//                fontFamily = SamsungOneFont,
//                color = Color.White
//            )
//        }
    }
}

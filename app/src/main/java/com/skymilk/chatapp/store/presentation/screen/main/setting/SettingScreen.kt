package com.skymilk.chatapp.store.presentation.screen.main.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.MapsUgc
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsItem

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
    onEvent: (SettingEvent) -> Unit,
    currentUser: User,
    onNavigateToProfile: (User) -> Unit
) {
    val settingState = viewModel.settingState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        //헤더
        TopSection()

        //내 정보
        FriendsItem(
            user = currentUser,
            profileSize = 60.dp,
            onUserItemClick = {
                onNavigateToProfile(currentUser)
            }
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))

        //설정 섹션
        SettingSection(
            settingState = settingState.value,
            toggleAlarmSetting = { onEvent(SettingEvent.ToggleAlarmSetting) },
            toggleDarkModeSetting = { onEvent(SettingEvent.ToggleDarkModeSetting) }
        )
    }
}


//상단 타이틀
@Composable
fun TopSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "설정",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        //사이즈 맞추기 용 임시 이미지
        IconButton(enabled = false, onClick = { }) {
            Icon(
                imageVector = Icons.Rounded.MapsUgc,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surface
            )
        }
    }

    HorizontalDivider()
}

//설정 섹션
@Composable
fun SettingSection(
    settingState: SettingState,
    toggleAlarmSetting: () -> Unit,
    toggleDarkModeSetting: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        //알림
        SettingToggleItem(
            modifier = Modifier.fillMaxWidth(),
            title = "알림",
            icon = Icons.Outlined.Notifications,
            value = settingState.isAlarmEnabled,
            onToggle = toggleAlarmSetting
        )

        //어두운 모드
        SettingToggleItem(
            modifier = Modifier.fillMaxWidth(),
            title = "어두운 모드",
            icon = Icons.Outlined.DarkMode,
            value = settingState.isDarkModeEnabled,
            onToggle = toggleDarkModeSetting
        )
    }
}
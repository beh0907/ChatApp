package com.skymilk.chatapp.store.presentation.screen.main.setting

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.domain.model.User
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
        //설정 상태
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { toggleAlarmSetting() }
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = null
            )

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                text = "알림",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Switch(
                checked = settingState.isAlarmEnabled,
                onCheckedChange = { toggleAlarmSetting() }
            )
        }

        //설정 상태
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { toggleDarkModeSetting() }
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.DarkMode,
                contentDescription = null
            )

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                text = "어두운 모드",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Switch(
                checked = settingState.isDarkModeEnabled,
                onCheckedChange = { toggleDarkModeSetting() }
            )
        }


    }
}
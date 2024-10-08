package com.skymilk.chatapp.store.presentation.screen.main.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsItem
import com.skymilk.chatapp.ui.theme.LeeSeoYunFont

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
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
        FriendsItem(user = currentUser, onUserItemClick = {
            onNavigateToProfile(currentUser)
        })

        HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))

        //설정 섹션
        SettingSection(
            settingState = settingState.value,
            toggleAlarmSetting = viewModel::toggleAlarmSetting
        )
    }
}


//상단 타이틀
@Composable
fun TopSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "설정",
            fontFamily = LeeSeoYunFont,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    HorizontalDivider()
}

//설정 섹션
@Composable
fun SettingSection(
    settingState: SettingState,
    toggleAlarmSetting: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //설정 상태
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { toggleAlarmSetting(!settingState.isAlarmEnabled) }
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
                fontFamily = LeeSeoYunFont,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Switch(
                checked = settingState.isAlarmEnabled,
                onCheckedChange = { toggleAlarmSetting(it) }
            )
        }


    }
}
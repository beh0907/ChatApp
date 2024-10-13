package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.ui.theme.CookieRunFont

@Composable
fun SystemMessageItem(
    content: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                .padding(vertical = 8.dp, horizontal = 20.dp),
            text = content,
            fontFamily = CookieRunFont,
            color = MaterialTheme.colorScheme.surface,
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
fun PreviewSystemMessageItem() {
    SystemMessageItem(content = "~~~님이 퇴장하였습니다.")
}
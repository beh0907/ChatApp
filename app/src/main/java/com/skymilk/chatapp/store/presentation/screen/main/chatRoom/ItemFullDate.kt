package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.ui.theme.HannaPro

@Composable
fun ItemFullDate(
    fullDate: String
) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), contentAlignment = Alignment.Center) {
        Text(
            text = fullDate,
            fontFamily = HannaPro,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
    }

}
package com.skymilk.chatapp.store.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.skymilk.chatapp.ui.theme.HannaPro

@Composable
fun CustomProgressDialog(message: String) {
    Dialog(onDismissRequest = { /* 사용자가 다이얼로그 외부를 탭해도 닫히지 않도록 함 */ }) {
        Card(
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(text = message, fontSize = MaterialTheme.typography.titleMedium.fontSize, fontFamily = HannaPro)
            }
        }
    }
}

@Composable
fun CustomErrorConfirmDialog(message: String, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        text = {
            Text(text = message, fontSize = MaterialTheme.typography.titleMedium.fontSize, fontFamily = HannaPro)
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm() }
            ) {
                Text("확인", fontSize = MaterialTheme.typography.titleMedium.fontSize, fontFamily = HannaPro)
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
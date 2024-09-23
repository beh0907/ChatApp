package com.skymilk.chatapp.store.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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

                Text(
                    text = message,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontFamily = HannaPro
                )
            }
        }
    }
}

@Composable
fun CustomConfirmDialog(message: String, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        text = {
            Text(
                text = message,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontFamily = HannaPro
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm() }
            ) {
                Text(
                    "확인",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontFamily = HannaPro
                )
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun CustomAlertDialog(message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        text = {
            Text(
                text = message,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontFamily = HannaPro
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm() }
            ) {
                Text(
                    "확인",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontFamily = HannaPro
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text(
                    "취소",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontFamily = HannaPro
                )
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun CustomFullScreenEditDialog(
    initText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var inputText by remember { mutableStateOf(initText) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 상단 바
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // X 버튼과 타이틀
                    Row(
                        modifier = Modifier
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "프로필 편집",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = HannaPro,
                            color = Color.White
                        )
                    }

                    // 확인 버튼
                    TextButton(
                        onClick = { onConfirm(inputText) }
                    ) {
                        Text(
                            text = "확인",
                            fontFamily = HannaPro,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 텍스트 입력 창
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = {
                            //20자로 이름 제한
                            if (it.length <= 20) inputText = it
                        },
                        textStyle = TextStyle(
                            fontFamily = HannaPro,
                            fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
                            color = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = {
                            Text(
                                "텍스트를 입력하세요",
                                fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
                                fontFamily = HannaPro,
                                color = Color.White
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { inputText = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color.White
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )

                    Text("${inputText.length} / 20", fontFamily = HannaPro, color = Color.White)
                }
            }
        }
    }
}
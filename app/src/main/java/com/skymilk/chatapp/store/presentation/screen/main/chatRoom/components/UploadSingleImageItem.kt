package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.skymilk.chatapp.store.domain.model.ImageUploadInfo
import com.skymilk.chatapp.store.presentation.utils.DateUtil
import com.skymilk.chatapp.store.presentation.utils.FileSizeUtil


//이미지가 하나일떈 용량 정보를 보여준다
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun UploadSingleImageItem(
    imageUploadInfo: ImageUploadInfo
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        val maxWidth = maxWidth * 0.6f

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 4.dp,
                color = Color(0xFF90CAF9),
                modifier = Modifier.widthIn(max = maxWidth)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // 이미지
                    AsyncImage(
                        model = imageUploadInfo.imageUri,
                        contentDescription = "Uploading Image",
                        modifier = Modifier.size(maxWidth),
                        contentScale = ContentScale.Crop
                    )

                    // 반투명 오버레이
                    Box(
                        modifier = Modifier
                            .size(maxWidth)
                            .background(Color.Black.copy(alpha = 0.5f))
                    )

                    // 프로그레스 및 텍스트
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { imageUploadInfo.progress / 100f },
                            modifier = Modifier.size(40.dp),
                            color = Color.White,
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "${FileSizeUtil.formatFileSize(imageUploadInfo.bytesTransferred)} / ${
                                FileSizeUtil.formatFileSize(
                                    imageUploadInfo.totalBytes
                                )
                            }",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
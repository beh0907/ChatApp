package com.skymilk.chatapp.store.domain.usecase.chat

import android.content.Context
import android.net.Uri
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.MessageEvent
import com.skymilk.chatapp.store.presentation.utils.FileSizeUtil
import kotlinx.coroutines.flow.Flow

class CompressImagesUseCase(
    private val context: Context,
) {
    suspend operator fun invoke(imageUris:List<Uri>): List<Uri> {
        return FileSizeUtil.resizeAndCompressImages(context, imageUris)
    }
}
package com.skymilk.chatapp.store.data.repository

import android.net.Uri
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.skymilk.chatapp.store.data.utils.Constants
import com.skymilk.chatapp.store.domain.model.ImageUploadInfo
import com.skymilk.chatapp.store.domain.repository.StorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storageReference: StorageReference
) : StorageRepository {

    //프로필 이미지 저장
    override fun saveProfileImage(userid: String, byteArray: ByteArray): Flow<ImageUploadInfo> {
        return saveImage("${Constants.FirebaseReferences.PROFILE_IMAGES}/$userid", byteArray)
    }

    //채팅방 이미지 저장
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun saveChatMessageImage(
        chatRoomId: String,
        uris: List<Uri>
    ): Flow<List<ImageUploadInfo>> = flow {
        //이미지 업로드 목록 생성
        val uploadInfoList = uris.map { ImageUploadInfo(imageUri = it) }.toMutableList()
        emit(uploadInfoList)

        //이미지 업로드
        val uploadFlows = uris.mapIndexed { index, uri ->
            saveImage(
                "${Constants.FirebaseReferences.CHAT_IMAGES}/$chatRoomId/${System.currentTimeMillis()}-${UUID.randomUUID()}",
                uri
            ).map { uploadInfo ->
                index to uploadInfo
            }
        }

        //이미지 업로드 상태 처리
        uploadFlows.asFlow().flattenMerge().collect { (index, uploadInfo) ->
            uploadInfoList[index] = uploadInfo.copy(imageUri = uris[index])
            emit(uploadInfoList.toList())
        }
    }.flowOn(Dispatchers.IO)

    private fun <T> saveImage(path: String, imageData: T): Flow<ImageUploadInfo> = callbackFlow {
        val storageRef = storageReference.child(path)

        val uploadTask = when (imageData) {
            is ByteArray -> storageRef.putBytes(imageData)
            is Uri -> storageRef.putFile(imageData)
            else -> throw IllegalArgumentException("지원하지 않는 이미지 타입입니다.")
        }

        val progressListener = OnProgressListener<UploadTask.TaskSnapshot> { taskSnapshot ->
            val progress = ImageUploadInfo(
                bytesTransferred = taskSnapshot.bytesTransferred,
                totalBytes = taskSnapshot.totalByteCount,
                progress = ((100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount).toInt()
            )
            trySend(progress)
        }

        uploadTask.addOnProgressListener(progressListener)

        try {
            uploadTask.await()
            val downloadUrl = uploadTask.snapshot.storage.downloadUrl.await().toString()
            trySend(
                ImageUploadInfo(
                    downloadUrl = downloadUrl,
                    progress = 100,
                    bytesTransferred = uploadTask.snapshot.totalByteCount,
                    totalBytes = uploadTask.snapshot.totalByteCount
                )
            )
        } catch (e: Exception) {
            trySend(ImageUploadInfo(error = e.message))
        } finally {
            uploadTask.removeOnProgressListener(progressListener)
        }

        awaitClose()
    }
}
package com.skymilk.chatapp.store.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.skymilk.chatapp.store.domain.model.UploadProgress
import com.skymilk.chatapp.store.domain.repository.StorageRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storageReference: StorageReference
) : StorageRepository {

    override suspend fun saveProfileImage(userid: String, uri: Uri): Flow<UploadProgress> {
        return saveImage("profile_images/$userid", uri)
    }

    override suspend fun saveChatMessageImage(chatRoomId: String, uri: Uri): Flow<UploadProgress> {
        return saveImage(
            "chat_images/$chatRoomId/${System.currentTimeMillis()}-${UUID.randomUUID()}",
            uri
        )
    }

    override fun saveImage(path: String, uri: Uri): Flow<UploadProgress> = callbackFlow {
        val storageRef = storageReference.child(path)
        val uploadTask = storageRef.putFile(uri)

        val progressListener = OnProgressListener<UploadTask.TaskSnapshot> { taskSnapshot ->
            //상태 현황 업데이트
            val progress = UploadProgress(
                bytesTransferred = taskSnapshot.bytesTransferred,
                totalBytes = taskSnapshot.totalByteCount,
                progress = ((100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount).toInt()
            )

            trySend(progress)
        }

        //업로드 리스너 등록 후 실행
        uploadTask.addOnProgressListener(progressListener)
        uploadTask.await()

        //다운로드 정보 출력
        val downloadUrl = uploadTask.snapshot.storage.downloadUrl.await().toString()
        send(UploadProgress(downloadUrl = downloadUrl, progress = 100))

        //리스너 제거
        awaitClose {
            uploadTask.removeOnProgressListener(progressListener)
        }
    }

}
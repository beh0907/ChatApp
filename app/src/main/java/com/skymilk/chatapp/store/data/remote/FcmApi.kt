package com.skymilk.chatapp.store.data.remote

import com.skymilk.chatapp.store.data.dto.FcmMessage
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


interface FcmApi {
    @Headers("Content-Type:application/json")
    @POST("v1/projects/chatapp-9273a/messages:send")
    suspend fun postNotification(
        @Header("Authorization") token: String,
        @Body fcmMessage: FcmMessage
    ): retrofit2.Response<ResponseBody>
}
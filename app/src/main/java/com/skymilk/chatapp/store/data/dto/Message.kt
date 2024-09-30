package com.skymilk.chatapp.store.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    @SerialName("topic")
    val topic: String,
    @SerialName("data")
    val data: Map<String, String>,
    @SerialName("android")
    val android: FcmAndroidSetting
)
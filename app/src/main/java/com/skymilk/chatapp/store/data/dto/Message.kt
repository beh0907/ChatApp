package com.skymilk.chatapp.store.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    @SerialName("notification")
    val notification: Notification,
    @SerialName("topic")
    val topic: String,
    @SerialName("data")
    val data: Map<String, String>
)
package com.skymilk.chatapp.store.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmMessage(
    @SerialName("message")
    val message: Message
)
package com.skymilk.chatapp.store.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    @SerialName("body")
    val body: String,
    @SerialName("title")
    val title: String
)
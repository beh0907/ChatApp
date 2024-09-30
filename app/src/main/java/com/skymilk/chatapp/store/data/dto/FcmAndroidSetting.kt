package com.skymilk.chatapp.store.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FcmAndroidSetting(
    @SerialName("direct_boot_ok")
    val directBootOk: Boolean
)
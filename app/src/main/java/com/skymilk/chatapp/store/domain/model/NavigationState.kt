package com.skymilk.chatapp.store.domain.model

import android.os.Parcelable
import com.skymilk.chatapp.store.domain.model.User
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class NavigationState(
    val destination: String = "",
    val params: Map<String, String> = emptyMap()
) : Parcelable {
    constructor() : this("", emptyMap())
}

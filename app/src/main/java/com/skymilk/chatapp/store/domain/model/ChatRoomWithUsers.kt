package com.skymilk.chatapp.store.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ChatRoomWithUsers(
    val id: String,
    val name: String,
    val participants: List<User>,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
    val createdTimestamp: Long
) : Parcelable
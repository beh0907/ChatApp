package com.skymilk.chatapp.store.domain.model

import com.skymilk.chatapp.store.data.dto.ParticipantStatus

data class Participant(
    val user: User,
    val participantStatus: ParticipantStatus
) {
    constructor() : this(User(), ParticipantStatus())
}
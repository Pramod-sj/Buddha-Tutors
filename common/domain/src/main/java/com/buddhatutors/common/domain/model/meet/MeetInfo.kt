package com.buddhatutors.common.domain.model.meet

import kotlinx.serialization.Serializable

@Serializable
data class MeetInfo(
    val eventId: String = "",
    val meetUrl: String = ""
)

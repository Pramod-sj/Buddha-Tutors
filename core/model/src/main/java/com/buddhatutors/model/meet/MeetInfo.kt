package com.buddhatutors.model.meet

import kotlinx.serialization.Serializable

@Serializable
data class MeetInfo(
    val eventId: String = "",
    val meetUrl: String = ""
)

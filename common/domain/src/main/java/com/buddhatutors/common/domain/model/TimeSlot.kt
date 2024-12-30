package com.buddhatutors.common.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TimeSlot(
    val start: String? = null,
    val end: String? = null,
)
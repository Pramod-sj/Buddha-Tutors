package com.buddhatutors.domain.model.registration

import kotlinx.serialization.Serializable

@Serializable
data class TimeSlot(
    val start: String? = null,
    val end: String? = null,
)
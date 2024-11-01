package com.buddhatutors.domain.model.registration

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class TimeSlot(
    val start: String? = null,
    val end: String? = null,
)
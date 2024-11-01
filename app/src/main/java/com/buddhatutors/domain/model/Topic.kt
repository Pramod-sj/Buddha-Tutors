package com.buddhatutors.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Topic(
    val id: String? = null,
    val label: String = "",
    val isVisible: Boolean = false
)

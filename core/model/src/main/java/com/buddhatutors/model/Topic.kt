package com.buddhatutors.model

import kotlinx.serialization.Serializable

@Serializable
data class Topic(
    val id: String = "", val label: String = "", val isVisible: Boolean = false
)
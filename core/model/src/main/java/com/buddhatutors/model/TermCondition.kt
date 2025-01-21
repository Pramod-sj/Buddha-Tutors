package com.buddhatutors.model

import kotlinx.serialization.Serializable

@Serializable
data class TermCondition(
    val title: String,
    val desc: String
)

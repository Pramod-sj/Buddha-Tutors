package com.buddhatutors.common.domain.model

data class FilterOption(
    val topics: List<Topic>? = null,
    val languages: List<String>? = null
)
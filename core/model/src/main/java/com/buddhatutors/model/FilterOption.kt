package com.buddhatutors.model

data class FilterOption(
    val topics: List<Topic>? = null,
    val languages: List<String>? = null
)
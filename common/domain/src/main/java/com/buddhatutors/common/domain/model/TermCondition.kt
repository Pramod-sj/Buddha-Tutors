package com.buddhatutors.common.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TermCondition(
    val title: String,
    val desc: String
) : Parcelable

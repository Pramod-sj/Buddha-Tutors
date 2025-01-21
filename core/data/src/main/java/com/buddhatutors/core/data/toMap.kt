package com.buddhatutors.core.data

import kotlin.reflect.full.memberProperties

fun Any.toMap(): Map<String, Any?> {
    return this::class.memberProperties.associate { prop -> prop.name to prop.call(this) }
}
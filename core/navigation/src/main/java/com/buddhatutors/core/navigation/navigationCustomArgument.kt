package com.buddhatutors.core.navigation

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf

inline fun <reified T : Any> navigationCustomArgument(isNullable: Boolean = false): Pair<KType, CustomNavType<T>> {
    val serializer: KSerializer<T> = serializer()
    return typeOf<T>() to CustomNavType(serializer, isNullable)
}

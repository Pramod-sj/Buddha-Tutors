@file:OptIn(ExperimentalSerializationApi::class)

package com.buddhatutors.core.navigation

import android.os.Bundle
import androidx.navigation.NavType
import android.net.Uri
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class CustomNavType<T : Any>(
    private val serializer: KSerializer<T>,
    override val isNullableAllowed: Boolean = false
) : NavType<T>(isNullableAllowed) {

    override fun get(bundle: Bundle, key: String): T? =
        bundle.getString(key)?.let {
            val decodedValue = Uri.decode(it)
            Json.decodeFromString(serializer, decodedValue)
        }

    override fun put(bundle: Bundle, key: String, value: T) =
        bundle.putString(key, serializeAsValue(value))

    override fun parseValue(value: String): T {
        val decodedValue = Uri.decode(value)
        return Json.decodeFromString(serializer, decodedValue)
    }

    override fun serializeAsValue(value: T): String {
        val jsonString = Json.encodeToString(serializer, value)
        return Uri.encode(jsonString)
    }

    override val name: String = serializer.descriptor.serialName
}
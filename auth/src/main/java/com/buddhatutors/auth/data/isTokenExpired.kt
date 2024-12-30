package com.buddhatutors.auth.data

import android.util.Base64
import org.json.JSONObject

// Function to check if token has expired
internal fun isTokenExpired(token: String?): Boolean {
    if (token == null) return true

    // Decode the token to extract expiration time (JWT token)
    try {
        val splitToken = token.split(".")
        if (splitToken.size == 3) {
            val payload = String(Base64.decode(splitToken[1], Base64.DEFAULT))
            val jsonObject = JSONObject(payload)
            val expirationTime = jsonObject.getLong("exp") // expiration time (in seconds)

            // Compare expiration time with current time
            val currentTime = System.currentTimeMillis() / 1000  // current time in seconds
            return expirationTime < currentTime
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return true
}
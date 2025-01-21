package com.buddhatutors.core.auth.google_oauth.oauth

import android.app.Activity
import com.buddhatutors.model.Resource

internal typealias AuthIdToken = String

interface OAuthHandler {

    suspend fun authenticate(activity: Activity): Resource<AuthIdToken>

}
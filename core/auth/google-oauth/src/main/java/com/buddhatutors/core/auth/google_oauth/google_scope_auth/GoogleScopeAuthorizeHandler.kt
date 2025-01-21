package com.buddhatutors.core.auth.google_oauth.google_scope_auth

import android.app.PendingIntent
import com.buddhatutors.model.Resource


internal typealias ScopeAccessToken = String

class GoogleScopeResolutionException(val pendingIntent: PendingIntent) : Throwable()

interface GoogleScopeAuthorizeHandler {

    suspend fun authorize(): Resource<ScopeAccessToken>

}
package com.buddhatutors.auth.domain

import android.app.PendingIntent
import com.buddhatutors.common.domain.model.Resource

interface ContextWrapper

typealias AuthIdToken = String

typealias ScopeAccessToken = String

interface OAuthHandler {

    suspend fun authenticate(contextWrapper: ContextWrapper): Resource<AuthIdToken>

}


interface IntentWrapper

class GoogleScopeResolutionException(val pendingIntent: PendingIntent) : Throwable()

interface GoogleScopeAuthorizeHandler {

    suspend fun authorize(): Resource<ScopeAccessToken>

}
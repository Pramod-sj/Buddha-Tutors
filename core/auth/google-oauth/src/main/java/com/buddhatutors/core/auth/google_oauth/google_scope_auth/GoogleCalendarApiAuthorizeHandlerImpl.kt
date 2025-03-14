package com.buddhatutors.core.auth.google_oauth.google_scope_auth

import android.content.Context
import android.content.IntentSender
import androidx.activity.ComponentActivity
import com.buddhatutors.core.auth.domain.UserSessionPreference
import com.buddhatutors.core.auth.domain.UserSessionPreference.Companion.KEY_CALENDAR_SCOPE_ACCESS_TOKEN
import com.buddhatutors.model.Resource
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.api.services.calendar.CalendarScopes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


internal class GoogleCalendarApiAuthorizeHandlerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userSessionDataSource: UserSessionPreference
) : GoogleScopeAuthorizeHandler {

    private val identity = Identity.getAuthorizationClient(context)

    override suspend fun authorize(): Resource<ScopeAccessToken> {

        val existingIdToken =
            userSessionDataSource.getUserToken(KEY_CALENDAR_SCOPE_ACCESS_TOKEN)

        if (existingIdToken != null && !isTokenExpired(existingIdToken)) {
            return Resource.Success(existingIdToken)
        }

        val resource = suspendCoroutine { continuation ->

            val requestedScopes = listOf(Scope(CalendarScopes.CALENDAR))

            val authorizationRequest = AuthorizationRequest.Builder()
                .setRequestedScopes(requestedScopes)
                .build()

            identity.authorize(authorizationRequest)
                .addOnCompleteListener { task ->

                    val authorizationResult = task.result

                    if (authorizationResult.hasResolution()) {
                        val pendingIntent = authorizationResult.pendingIntent
                        try {
                            listOf<Int>().count { it == 1 }
                            continuation.resume(
                                Resource.Error(
                                    com.buddhatutors.core.auth.google_oauth.google_scope_auth.GoogleScopeResolutionException(
                                        pendingIntent!!
                                    )
                                )
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            continuation.resume(
                                Resource.Error(
                                    Throwable("Couldn't start Authorization UI: " + e.localizedMessage)
                                )
                            )
                        }
                    } else {
                        // Send googleIdTokenCredential to your server for validation and authentication
                        val accessToken = authorizationResult.accessToken
                        if (accessToken.isNullOrEmpty()) {
                            continuation.resume(
                                Resource.Error(
                                    Throwable("Access token returned was empty!")
                                )
                            )
                        } else {
                            continuation.resume(
                                Resource.Success(
                                    accessToken
                                )
                            )
                        }
                    }

                }

        }

        return resource.also {
            if (it is Resource.Success) {
                userSessionDataSource.saveUserTokens(
                    map = mapOf(KEY_CALENDAR_SCOPE_ACCESS_TOKEN to it.data)
                )
            }
        }
    }

}

fun scanForActivity(cont: Context?): ComponentActivity? {
    return when (cont) {
        is ComponentActivity -> cont
        is android.content.ContextWrapper -> scanForActivity(cont.baseContext)
        else -> null
    }
}
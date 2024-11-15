package com.buddhatutors.data.datasourceimpl

import android.app.Activity.RESULT_OK
import android.app.PendingIntent
import android.content.Context
import android.content.IntentSender
import androidx.activity.ComponentActivity
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.buddhatutors.domain.ContextWrapper
import com.buddhatutors.domain.GoogleScopeAuthorizeHandler
import com.buddhatutors.domain.IntentWrapper
import com.buddhatutors.domain.KEY_CALENDAR_SCOPE_ACCESS_TOKEN
import com.buddhatutors.domain.ScopeAccessToken
import com.buddhatutors.domain.UserSessionDataSource
import com.buddhatutors.domain.model.Resource
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.api.services.calendar.CalendarScopes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class PendingIndentWrapper(val pendingIntent: PendingIntent) : IntentWrapper

internal class GoogleCalendarApiAuthorizeHandlerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userSessionDataSource: UserSessionDataSource
) : GoogleScopeAuthorizeHandler {

    private val identity = Identity.getAuthorizationClient(context)

    override suspend fun authorize(): Resource<ScopeAccessToken> {

        val existingIdToken = userSessionDataSource.getUserToken(KEY_CALENDAR_SCOPE_ACCESS_TOKEN)

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
                            //launcher.launch(
                            IntentSenderRequest
                                .Builder(pendingIntent!!.intentSender)
                                .build()
                            //)
                        } catch (e: IntentSender.SendIntentException) {
                            continuation.resume(Resource.Error(Throwable("Couldn't start Authorization UI: " + e.localizedMessage)))
                        }
                    } else {
                        // Send googleIdTokenCredential to your server for validation and authentication
                        val accessToken = authorizationResult.accessToken
                        if (accessToken.isNullOrEmpty()) {
                            continuation.resume(Resource.Error(Throwable("Access token returned was empty!")))
                        } else {
                            continuation.resume(Resource.Success(accessToken))
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
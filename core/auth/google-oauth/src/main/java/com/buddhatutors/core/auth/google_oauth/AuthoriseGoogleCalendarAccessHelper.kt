package com.buddhatutors.core.auth.google_oauth

import android.app.Activity
import com.buddhatutors.core.auth.google_oauth.google_scope_auth.GoogleScopeAuthorizeHandler
import com.buddhatutors.core.auth.google_oauth.oauth.OAuthHandler
import com.buddhatutors.model.Resource
import javax.inject.Inject

class AuthoriseGoogleCalendarAccessHelper @Inject constructor(
    private val oAuthHandler: OAuthHandler,
    private val googleScopeAuthorizeHandler: GoogleScopeAuthorizeHandler
) {

    suspend fun startAuthProcess(activity: Activity): Resource<Boolean> {
        return when (val authenticationResource = oAuthHandler.authenticate(activity)) {

            is Resource.Error -> authenticationResource

            is Resource.Success -> {

                when (val authorizationResource =
                    googleScopeAuthorizeHandler.authorize()) {

                    is Resource.Error -> authorizationResource

                    is Resource.Success -> Resource.Success(true)
                }

            }
        }

    }

}
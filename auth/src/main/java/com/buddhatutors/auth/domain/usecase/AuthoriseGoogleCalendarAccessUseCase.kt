package com.buddhatutors.auth.domain.usecase

import com.buddhatutors.auth.domain.ContextWrapper
import com.buddhatutors.auth.domain.GoogleScopeAuthorizeHandler
import com.buddhatutors.auth.domain.OAuthHandler
import com.buddhatutors.common.domain.model.Resource
import javax.inject.Inject

class AuthoriseGoogleCalendarAccessUseCase @Inject constructor(
    private val oAuthHandler: OAuthHandler,
    private val googleScopeAuthorizeHandler: GoogleScopeAuthorizeHandler
) {

    suspend operator fun invoke(contextWrapper: ContextWrapper): Resource<Boolean> {
        return when (val authenticationResource = oAuthHandler.authenticate(contextWrapper)) {

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
package com.buddhatutors.domain.usecase

import com.buddhatutors.domain.ContextWrapper
import com.buddhatutors.domain.GoogleScopeAuthorizeHandler
import com.buddhatutors.domain.OAuthHandler
import com.buddhatutors.domain.model.Resource
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
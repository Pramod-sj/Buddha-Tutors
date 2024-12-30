package com.buddhatutors.auth.domain.usecase

import com.buddhatutors.common.domain.model.Resource
import javax.inject.Inject

class AuthoriseGoogleCalendarAccessUseCase @Inject constructor(
    private val oAuthHandler: com.buddhatutors.auth.domain.OAuthHandler,
    private val googleScopeAuthorizeHandler: com.buddhatutors.auth.domain.GoogleScopeAuthorizeHandler
) {

    suspend operator fun invoke(contextWrapper: com.buddhatutors.auth.domain.ContextWrapper): Resource<Boolean> {
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
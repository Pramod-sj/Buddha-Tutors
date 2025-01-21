package com.buddhatutors.core.auth.google_oauth.di

import com.buddhatutors.core.auth.google_oauth.google_scope_auth.GoogleCalendarApiAuthorizeHandlerImpl
import com.buddhatutors.core.auth.google_oauth.google_scope_auth.GoogleScopeAuthorizeHandler
import com.buddhatutors.core.auth.google_oauth.oauth.GoogleSignInOAuthHandlerImpl
import com.buddhatutors.core.auth.google_oauth.oauth.OAuthHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface GoogleAuthModule {

    @Binds
    fun bindOAuthHandler(googleSignInOAuthHandlerImpl: GoogleSignInOAuthHandlerImpl): OAuthHandler

    @Binds
    fun bindGoogleCalendarApiAuthorizeHandler(googleCalendarApiAuthorizeHandlerImpl: GoogleCalendarApiAuthorizeHandlerImpl): GoogleScopeAuthorizeHandler

}
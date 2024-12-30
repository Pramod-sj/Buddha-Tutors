package com.buddhatutors.auth.di

import com.buddhatutors.auth.data.GoogleCalendarApiAuthorizeHandlerImpl
import com.buddhatutors.auth.data.GoogleSignInOAuthHandlerImpl
import com.buddhatutors.auth.data.datasourceimpl.AuthDataSourceImpl
import com.buddhatutors.auth.data.datasourceimpl.UserSessionDataSourceImpl
import com.buddhatutors.auth.domain.datasource.UserSessionDataSource
import com.buddhatutors.auth.domain.datasource.AuthDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface AuthDataSourceModule {

    @Binds
    fun bindAuthDataSource(authDataSourceImpl: AuthDataSourceImpl): AuthDataSource

    @Binds
    fun bindUserSessionDataSource(userSessionDataSourceImpl: UserSessionDataSourceImpl): UserSessionDataSource


    @Binds
    fun bindOAuthHandler(googleSignInOAuthHandlerImpl: GoogleSignInOAuthHandlerImpl): com.buddhatutors.auth.domain.OAuthHandler

    @Binds
    fun bindGoogleScopeAuthorizeHandler(googleCalendarApiAuthorizeHandlerImpl: GoogleCalendarApiAuthorizeHandlerImpl): com.buddhatutors.auth.domain.GoogleScopeAuthorizeHandler

}
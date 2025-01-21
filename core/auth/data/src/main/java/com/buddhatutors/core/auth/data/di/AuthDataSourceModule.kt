package com.buddhatutors.core.auth.data.di

import com.buddhatutors.core.auth.data.datasourceimpl.AuthServiceImpl
import com.buddhatutors.core.auth.data.datastore.UserSessionDataSourceImpl
import com.buddhatutors.core.auth.domain.AuthService
import com.buddhatutors.core.auth.domain.UserSessionPreference
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface AuthDataSourceModule {

    @Binds
    fun bindAuthDataSource(authDataSourceImpl: AuthServiceImpl): AuthService

    @Binds
    fun bindUserSessionPreference(userSessionDataSourceImpl: UserSessionDataSourceImpl): UserSessionPreference

}
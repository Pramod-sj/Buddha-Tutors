package com.buddhatutors.framework.di

import com.buddhatutors.domain.datasource.AuthDataSource
import com.buddhatutors.domain.datasource.FirebaseRemoteConfigManager
import com.buddhatutors.domain.datasource.RemoteConfigSource
import com.buddhatutors.domain.datasource.TopicDataSource
import com.buddhatutors.domain.datasource.UserDataSource
import com.buddhatutors.framework.data.datasourceimpl.UserDataSourceImpl
import com.buddhatutors.framework.data.AuthDataSourceImpl
import com.buddhatutors.framework.data.datasourceimpl.TopicDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataSourceModule {

    @Binds
    fun bindAuthDataSource(authDataSourceImpl: AuthDataSourceImpl): AuthDataSource

    @Binds
    fun bindUserDataSource(userDataSourceImpl: UserDataSourceImpl): UserDataSource

    @Binds
    fun bindTopicDataSource(topicDataSourceImpl: TopicDataSourceImpl): TopicDataSource

    @Binds
    fun bindRemoteConfigSource(firebaseRemoteConfigManager: FirebaseRemoteConfigManager): RemoteConfigSource

}
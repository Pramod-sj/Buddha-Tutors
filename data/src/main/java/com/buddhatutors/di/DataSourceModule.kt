package com.buddhatutors.di

import com.buddhatutors.data.FirebaseRemoteConfigManager
import com.buddhatutors.data.datasourceimpl.AuthDataSourceImpl
import com.buddhatutors.data.datasourceimpl.TopicDataSourceImpl
import com.buddhatutors.data.datasourceimpl.TutorListingDataSourceImpl
import com.buddhatutors.data.datasourceimpl.UserDataSourceImpl
import com.buddhatutors.data.datasourceimpl.UserSessionDataSourceImpl
import com.buddhatutors.domain.UserSessionDataSource
import com.buddhatutors.domain.datasource.AuthDataSource
import com.buddhatutors.domain.datasource.RemoteConfigSource
import com.buddhatutors.domain.datasource.TopicDataSource
import com.buddhatutors.domain.datasource.TutorListingDataSource
import com.buddhatutors.domain.datasource.UserDataSource
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
    fun bindUserSessionDataSource(userSessionDataSourceImpl: UserSessionDataSourceImpl): UserSessionDataSource

    @Binds
    fun bindUserDataSource(userDataSourceImpl: UserDataSourceImpl): UserDataSource

    @Binds
    fun bindTopicDataSource(topicDataSourceImpl: TopicDataSourceImpl): TopicDataSource

    @Binds
    fun bindRemoteConfigSource(firebaseRemoteConfigManager: FirebaseRemoteConfigManager): RemoteConfigSource

    @Binds
    fun bindTutorListingDataSource(tutorListingDataSourceImpl: TutorListingDataSourceImpl): TutorListingDataSource

}
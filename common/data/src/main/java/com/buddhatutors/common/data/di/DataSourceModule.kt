package com.buddhatutors.common.data.di

import com.buddhatutors.common.data.data.FirebaseRemoteConfigManager
import com.buddhatutors.common.data.data.datasourceimpl.BookedSlotDataSourceImpl
import com.buddhatutors.common.data.data.datasourceimpl.MeetingDataSourceImpl
import com.buddhatutors.common.data.data.datasourceimpl.TopicDataSourceImpl
import com.buddhatutors.common.data.data.datasourceimpl.TutorListingDataSourceImpl
import com.buddhatutors.common.data.data.datasourceimpl.UserDataSourceImpl
import com.buddhatutors.common.domain.datasource.RemoteConfigSource
import com.buddhatutors.common.domain.datasource.TopicDataSource
import com.buddhatutors.common.domain.datasource.UserDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataSourceModule {

    @Binds
    fun bindUserDataSource(userDataSourceImpl: UserDataSourceImpl): UserDataSource

    @Binds
    fun bindTopicDataSource(topicDataSourceImpl: TopicDataSourceImpl): TopicDataSource

    @Binds
    fun bindRemoteConfigSource(firebaseRemoteConfigManager: FirebaseRemoteConfigManager): RemoteConfigSource

    @Binds
    fun bindTutorListingDataSource(tutorListingDataSourceImpl: TutorListingDataSourceImpl): com.buddhatutors.common.domain.datasource.TutorListingDataSource

    @Binds
    fun bindMeetingDataSource(meetingDataSourceImpl: MeetingDataSourceImpl): com.buddhatutors.common.domain.datasource.MeetingDataSource

    @Binds
    fun bindBookedSlotDataSource(bookedSlotDataSourceImpl: BookedSlotDataSourceImpl): com.buddhatutors.common.domain.datasource.BookedSlotDataSource

}
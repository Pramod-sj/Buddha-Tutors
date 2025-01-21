package com.buddhatutors.core.data.di

import com.buddhatutors.core.data.firebase.firestore.FSBookedSlotDataSourceImpl
import com.buddhatutors.core.data.firebase.firestore.FSTopicDataSourceImpl
import com.buddhatutors.core.data.firebase.firestore.FSTutorListingDataSourceImpl
import com.buddhatutors.core.data.firebase.firestore.FSUserDataSourceImpl
import com.buddhatutors.core.data.firebase.remote_config.FirebaseRemoteConfigManager
import com.buddhatutors.core.data.google_calendar.MeetingDataSourceImpl
import com.buddhatutors.domain.datasource.BookedSlotDataSource
import com.buddhatutors.domain.datasource.MeetingDataSource
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
    fun bindUserDataSource(userDataSourceImpl: FSUserDataSourceImpl): UserDataSource

    @Binds
    fun bindTopicDataSource(topicDataSourceImpl: FSTopicDataSourceImpl): TopicDataSource

    @Binds
    fun bindRemoteConfigSource(firebaseRemoteConfigManager: FirebaseRemoteConfigManager): RemoteConfigSource

    @Binds
    fun bindTutorListingDataSource(tutorListingDataSourceImpl: FSTutorListingDataSourceImpl): TutorListingDataSource

    @Binds
    fun bindMeetingDataSource(meetingDataSourceImpl: MeetingDataSourceImpl): MeetingDataSource

    @Binds
    fun bindBookedSlotDataSource(bookedSlotDataSourceImpl: FSBookedSlotDataSourceImpl): BookedSlotDataSource

}
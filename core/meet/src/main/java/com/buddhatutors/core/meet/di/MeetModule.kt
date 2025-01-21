package com.buddhatutors.core.meet.di

import com.buddhatutors.core.meet.GoogleMeetOpenerImpl
import com.buddhatutors.core.meet.MeetOpener
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface MeetModule {

    @Binds
    fun bindsMeetOpener(meetOpenerImpl: GoogleMeetOpenerImpl): MeetOpener

}
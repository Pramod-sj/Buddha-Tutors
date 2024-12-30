package com.buddhatutors.common.navigation.di

import com.buddhatutors.common.navigation.meet.GoogleMeetOpenerImpl
import com.buddhatutors.common.navigation.meet.MeetOpener
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface NavigationModule {


    @Binds
    fun bindsMeetOpener(meetOpenerImpl: GoogleMeetOpenerImpl): MeetOpener

}
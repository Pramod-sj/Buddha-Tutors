package com.buddhatutors.core.data.google_calendar


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import javax.inject.Inject

internal class CalendarServiceFactory @Inject constructor() {

    fun create(accessToken: String): Calendar {
        val jsonFactory = GsonFactory.getDefaultInstance()
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

        val credentials: GoogleCredentials =
            GoogleCredentials.create(AccessToken.newBuilder().setTokenValue(accessToken).build())

        return Calendar.Builder(httpTransport, jsonFactory, HttpCredentialsAdapter(credentials))
            .setApplicationName("BuddhaTutors")
            .build()
    }

}
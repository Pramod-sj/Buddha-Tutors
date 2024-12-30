package com.buddhatutors.common.navigation.meet

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import javax.inject.Inject


internal class GoogleMeetOpenerImpl @Inject constructor() : MeetOpener {

    @SuppressLint("QueryPermissionsNeeded")
    override fun open(context: Context, meetUrl: String) {

        // Create an intent to open Google Meet
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meetUrl))
        intent.setPackage("com.google.android.apps.meetings")

        // Check if the Google Meet app is installed
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Handle the case where Google Meet is not installed
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(meetUrl))
            context.startActivity(browserIntent)
        }
    }

}
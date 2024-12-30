package com.buddhatutors.common.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    const val DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss"

    const val TIME_FORMAT = "hh:mm a"

    fun isToday(calendar: Calendar?): Boolean {
        if (calendar == null) {
            return false
        }
        val today: Calendar = Calendar.getInstance() // Get the current date

        return (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
    }

    fun convertDateStringToSpecifiedDateString(
        dateString: String?,
        dateFormat: String = DATE_TIME_FORMAT,
        requiredDateFormat: String = DATE_TIME_FORMAT,
        inputLocale: Locale = Locale.getDefault(),
        outputLocale: Locale = Locale.getDefault()
    ): String? {
        if (dateString.isNullOrEmpty()) return null

        return try {
            val date = SimpleDateFormat(dateFormat, inputLocale).parse(dateString)
            date?.let {
                SimpleDateFormat(requiredDateFormat, outputLocale).format(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun convertTimeInMillisToSpecifiedDateString(
        timeInMillis: Long,
        dateFormat: String = DATE_TIME_FORMAT,
        locale: Locale = Locale.getDefault()
    ): String {
        val date = Date(timeInMillis)
        val simpleDateFormat = SimpleDateFormat(dateFormat, locale)
        return simpleDateFormat.format(date)
    }

    fun convertSpecifiedDateStringToTimeInMillis(
        dateString: String,
        dateFormat: String = DATE_TIME_FORMAT,
        locale: Locale = Locale.getDefault()
    ): Long {
        val simpleDateFormat = SimpleDateFormat(dateFormat, locale)
        return try {
            simpleDateFormat.parse(dateString)?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    fun getFutureRelativeTime(timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val difference = timestamp - currentTime

        val seconds = difference / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "in a few seconds"
            minutes < 60 -> "in $minutes minutes"
            hours < 24 -> "in $hours hours"
            days == 1L -> "tomorrow at ${
                SimpleDateFormat(
                    /* pattern = */ "hh:mm a",
                    /* locale = */ Locale.getDefault()
                ).format(Date(timestamp))
            }"

            else -> "on ${
                SimpleDateFormat(
                    /* pattern = */ "MMM d, yyyy",
                    /* locale = */ Locale.getDefault()
                ).format(Date(timestamp))
            } at ${
                SimpleDateFormat(
                    /* pattern = */ "hh:mm a",
                    /* locale = */ Locale.getDefault()
                ).format(Date(timestamp))
            }"
        }
    }
}
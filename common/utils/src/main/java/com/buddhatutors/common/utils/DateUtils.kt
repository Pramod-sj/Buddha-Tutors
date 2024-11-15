package com.buddhatutors.common.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    const val DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss"

    const val TIME_FORMAT = "hh:mm a"

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

}
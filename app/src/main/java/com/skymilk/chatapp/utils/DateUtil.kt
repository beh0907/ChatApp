package com.skymilk.chatapp.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtil {

    fun getDate(time: Long): String {

        val now = Calendar.getInstance()
        val date = Calendar.getInstance().apply {
            timeInMillis = time
        }

        //오늘 체크
        val isToday = now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)

        // 어제 체크
        val isYesterday = now.apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
        
        //올해 체크
        val isThisYear = now.get(Calendar.YEAR) == date.get(Calendar.YEAR)

        return when {
            // 오늘 날짜일 경우 현재 시간을 반환
            isToday -> getCurrentTime()
            // 어제 날짜일 경우 어제 반환
            isYesterday -> "어제"
            // 올해지만 오늘 이전 날짜일 경우 (월.일)을 반환
            isThisYear -> getCurrentDate()
            // 작년 혹은 그 이전 날짜일 경우 (연.월.일)을 반환
            else -> getPrevDate()
        }
    }

    //이전 해 날짜 정보
    private fun getPrevDate(): String {
        val date = formatNormalDate("yyyy.MM.dd", Date().time)
        return date
    }

    //올해 날짜 정보
    private fun getCurrentDate(): String {
        val date = formatNormalDate("MM.dd", Date().time)
        return date
    }

    //현재 시간 정보
    private fun getCurrentTime(): String {
        val isAM = Calendar.getInstance().get(Calendar.AM_PM) == Calendar.AM
        val pattern = if (isAM) "오전 hh:mm" else "오후 hh:mm"
        return formatNormalDate(pattern, Date().time)

    }


    private fun formatNormalDate(pattern: String, time: Long): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(time)
    }
}
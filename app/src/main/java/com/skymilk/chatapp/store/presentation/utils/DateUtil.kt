package com.skymilk.chatapp.store.presentation.utils

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
        val isToday = isToday(now.timeInMillis, date.timeInMillis)

        // 어제 체크
        val isYesterday = now.apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)

        //올해 체크
        val isThisYear = now.get(Calendar.YEAR) == date.get(Calendar.YEAR)

        return when {
            // 오늘 날짜일 경우 현재 시간을 반환
            isToday -> getTime(time)
            // 어제 날짜일 경우 어제 반환
            isYesterday -> "어제"
            // 올해지만 오늘 이전 날짜일 경우 (월.일)을 반환
            isThisYear -> getCurrentDate(time)
            // 작년 혹은 그 이전 날짜일 경우 (연.월.일)을 반환
            else -> getPrevYearDate(time)
        }
    }

    //이전 해 날짜 정보
    private fun getPrevYearDate(time: Long = Date().time): String {
        return formatNormalDate("yyyy년 MM월 dd일", time)
    }

    //올해 날짜 정보
    private fun getCurrentDate(time: Long = Date().time): String {
        val date = formatNormalDate("MM월 dd일", time)
        return date
    }

    //현재 시간 정보
    fun getTime(time: Long = Date().time): String {
        val isAM = Calendar.getInstance().apply {
            timeInMillis = time
        }.get(Calendar.AM_PM) == Calendar.AM

        val pattern = if (isAM) "오전 hh:mm" else "오후 hh:mm"
        return formatNormalDate(pattern, time)
    }

    //특정 날짜의 전체 날짜
    fun getFullDate(time: Long): String {
        return formatNormalDate("yyyy년 MM월 dd일 (E)", time)
    }

    //특절 날짜 및 시간
    fun getFullDateTime(time: Long): String {
        val isAM = Calendar.getInstance().apply {
            timeInMillis = time
        }.get(Calendar.AM_PM) == Calendar.AM

        val pattern = if (isAM) "yyyy. MM. dd. 오전 hh:mm" else "yyyy. MM. dd. 오후 hh:mm"
        return formatNormalDate(pattern, time)
    }

    fun isToday(time: Long, timeCompare: Long): Boolean {
        val date = Calendar.getInstance().apply {
            timeInMillis = time
        }
        val dateCompare = Calendar.getInstance().apply {
            timeInMillis = timeCompare
        }

        //오늘 체크
        val isToday = date.get(Calendar.YEAR) == dateCompare.get(Calendar.YEAR) &&
                date.get(Calendar.DAY_OF_YEAR) == dateCompare.get(Calendar.DAY_OF_YEAR)

        return isToday
    }


    private fun formatNormalDate(pattern: String, time: Long): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(time))
    }
}
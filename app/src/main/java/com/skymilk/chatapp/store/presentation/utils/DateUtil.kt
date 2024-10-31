package com.skymilk.chatapp.store.presentation.utils

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

object DateUtil {
    private val zoneId = ZoneId.systemDefault()
    private val koreanLocale = Locale.KOREAN

    //날짜 정보 가져오기
    fun getDate(time: Long): String {
        val dateTime = Instant.ofEpochMilli(time).atZone(zoneId)
        val now = ZonedDateTime.now(zoneId)

        return when {
            isToday(now, dateTime) -> getTime(time)
            isYesterday(now, dateTime) -> "어제"
            isSameYear(now, dateTime) -> getCurrentDate(time)
            else -> getPrevYearDate(time)
        }
    }

    //지난 해 날짜
    private fun getPrevYearDate(time: Long): String {
        return formatDate(time, "yyyy년 MM월 dd일")
    }

    //올해 날짜
    private fun getCurrentDate(time: Long): String {
        return formatDate(time, "MM월 dd일")
    }

    //현재 시간
    fun getTime(time: Long = Date().time): String {
        val dateTime = Instant.ofEpochMilli(time).atZone(zoneId)
        val pattern = if (dateTime.get(ChronoField.AMPM_OF_DAY) == 0) {
            "오전 hh:mm"
        } else {
            "오후 hh:mm"
        }
        return formatDate(time, pattern)
    }

    //총 날짜 정보 가져오기
    fun getFullDate(time: Long): String {
        return formatDate(time, "yyyy년 MM월 dd일 (E)")
    }

    // 총 날짜 및 시간 정보 가져오기
    fun getFullDateTime(time: Long): String {
        val dateTime = Instant.ofEpochMilli(time).atZone(zoneId)
        val pattern = if (dateTime.get(ChronoField.AMPM_OF_DAY) == 0) {
            "yyyy. MM. dd. 오전 hh:mm"
        } else {
            "yyyy. MM. dd. 오후 hh:mm"
        }
        return formatDate(time, pattern)
    }

    //오버로딩 오늘 여부 비교 체크
    fun isToday(time: Long, timeCompare: Long): Boolean {
        val date1 = Instant.ofEpochMilli(time).atZone(zoneId)
        val date2 = Instant.ofEpochMilli(timeCompare).atZone(zoneId)
        return isToday(date1, date2)
    }

    //오늘 여부 비교 체크
    private fun isToday(now: ZonedDateTime, dateTime: ZonedDateTime): Boolean {
        return now.toLocalDate() == dateTime.toLocalDate()
    }

    //어제 여부 비교 체크
    private fun isYesterday(now: ZonedDateTime, dateTime: ZonedDateTime): Boolean {
        return now.toLocalDate().minusDays(1) == dateTime.toLocalDate()
    }

    //올해 여부 비교 체크
    private fun isSameYear(now: ZonedDateTime, dateTime: ZonedDateTime): Boolean {
        return now.year == dateTime.year
    }

    //타임스탬프 변환
    private fun formatDate(time: Long, pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, koreanLocale)
        return Instant.ofEpochMilli(time)
            .atZone(zoneId)
            .format(formatter)
    }

    //분단위까지 동일한 시간대인지 체크
    fun isSameTimeMinute(time: Long, timeCompare: Long): Boolean {
        val dateTime1 = Instant.ofEpochMilli(time)
            .atZone(zoneId)
            .toLocalDateTime()
            .truncatedTo(ChronoUnit.MINUTES)

        val dateTime2 = Instant.ofEpochMilli(timeCompare)
            .atZone(zoneId)
            .toLocalDateTime()
            .truncatedTo(ChronoUnit.MINUTES)

        return dateTime1 == dateTime2
    }
}
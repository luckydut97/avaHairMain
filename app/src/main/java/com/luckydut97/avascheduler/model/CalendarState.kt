package com.luckydut97.avascheduler.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

/**
 * 달력 표시에 필요한 상태 정보를 담는 데이터 클래스 (주 계산 최적화)
 */
data class CalendarState(
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val todayDate: LocalDate = LocalDate.now()
) {
    // 현재 달의 첫 날
    val firstDayOfMonth: LocalDate = currentMonth.atDay(1)

    // 현재 달의 마지막 날
    val lastDayOfMonth: LocalDate = currentMonth.atEndOfMonth()

    // 달력에 표시되는 첫 번째 날 (이전 달의 날짜가 포함될 수 있음)
    val calendarStartDate: LocalDate = firstDayOfMonth.with(
        TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)
    )

    // 달력에 표시되는 마지막 날 (다음 달의 날짜가 포함될 수 있음)
    val calendarEndDate: LocalDate = lastDayOfMonth.with(
        TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)
    )

    // 달력에 표시할 주의 수 - 계산 개선
    val weeksInCalendar: Int = calculateWeeksInCalendar()

    // 현재 달력이 표시하는 모든 날짜 리스트
    val calendarDays: List<LocalDate> = generateCalendarDays()

    /**
     * 달력에 필요한 주의 수 계산 (개선된 버전)
     */
    private fun calculateWeeksInCalendar(): Int {
        val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(calendarStartDate, calendarEndDate) + 1
        return (daysBetween / 7).toInt()
    }

    /**
     * 달력에 표시할 모든 날짜를 생성
     */
    private fun generateCalendarDays(): List<LocalDate> {
        val days = mutableListOf<LocalDate>()
        val totalDays = weeksInCalendar * 7

        var currentDate = calendarStartDate
        repeat(totalDays) {
            days.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }

        return days
    }

    /**
     * 특정 날짜가 현재 달에 속하는지 확인
     */
    fun isInCurrentMonth(date: LocalDate): Boolean {
        return date.month == currentMonth.month
    }

    /**
     * 특정 날짜가 오늘인지 확인
     */
    fun isToday(date: LocalDate): Boolean {
        return date.equals(todayDate)
    }

    /**
     * 특정 날짜가 선택된 날짜인지 확인
     */
    fun isSelected(date: LocalDate): Boolean {
        return date.equals(selectedDate)
    }
}
package com.luckydut97.avascheduler.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

/**
 * 디자이너 정보와 스케줄을 나타내는 데이터 클래스
 */
data class Designer(
    val id: String,
    val name: String,
    val isIntern: Boolean = false,
    val color: Color
)

/**
 * 스케줄 시간대
 */
enum class ScheduleTimeSlot {
    MORNING,   // 오전
    AFTERNOON, // 오후
    ALL_DAY    // 종일
}

/**
 * 특정 날짜의 디자이너 스케줄 정보
 */
data class DesignerSchedule(
    val designer: Designer,
    val date: LocalDate,
    val timeSlot: ScheduleTimeSlot,
    val isAvailable: Boolean = true,  // 근무 가능 여부
    val note: String = ""             // 특이사항
)

/**
 * 특정 날짜의 모든 디자이너 스케줄 정보
 */
data class DailySchedule(
    val date: LocalDate,
    val designerSchedules: List<DesignerSchedule> = emptyList()
)
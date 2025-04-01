package com.luckydut97.avascheduler.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.luckydut97.avascheduler.model.CalendarState
import com.luckydut97.avascheduler.model.DailySchedule
import com.luckydut97.avascheduler.model.Designer
import com.luckydut97.avascheduler.model.DesignerSchedule
import com.luckydut97.avascheduler.model.ScheduleTimeSlot
import com.luckydut97.avascheduler.ui.theme.DesignerBlue
import com.luckydut97.avascheduler.ui.theme.DesignerGreen
import com.luckydut97.avascheduler.ui.theme.DesignerOrange
import com.luckydut97.avascheduler.ui.theme.DesignerPurple
import com.luckydut97.avascheduler.ui.theme.DesignerRed
import com.luckydut97.avascheduler.ui.theme.DesignerYellow
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID


class SchedulerViewModel : ViewModel() {
    // 달력 상태
    var calendarState by mutableStateOf(CalendarState())
        private set

    // 선택된 탭 인덱스
    var selectedTabIndex by mutableStateOf(0)
        private set

    // 디자이너 목록 (초기값 없이 시작)
    var designers by mutableStateOf<List<Designer>>(emptyList())
        private set

    // 모든 스케줄
    var schedules by mutableStateOf<Map<LocalDate, DailySchedule>>(emptyMap())
        private set

    // 휴무일 설정
    private var vacationDates by mutableStateOf<Map<String, List<LocalDate>>>(emptyMap())

    // 에이바헤어 이름
    var salonName by mutableStateOf("에이바헤어")
        private set

    init {
        // 초기화 시 빈 상태로 시작
        schedules = emptyMap()
    }

    /**
     * 날짜 선택 처리
     */
    fun selectDate(date: LocalDate) {
        calendarState = calendarState.copy(selectedDate = date)

        // 다른 달의 날짜를 선택한 경우 해당 달로 이동
        if (date.month != calendarState.currentMonth.month) {
            changeMonth(YearMonth.of(date.year, date.month))
        }
    }

    /**
     * 월 변경 처리
     */
    fun changeMonth(yearMonth: YearMonth) {
        calendarState = CalendarState(
            currentMonth = yearMonth,
            selectedDate = calendarState.selectedDate,
            todayDate = calendarState.todayDate
        )

        // 새로운 월로 변경 시 해당 월의 스케줄 초기화
        initializeSchedulesForMonth(yearMonth)
    }

    /**
     * 다음 달로 이동
     */
    fun nextMonth() {
        changeMonth(calendarState.currentMonth.plusMonths(1))
    }

    /**
     * 이전 달로 이동
     */
    fun previousMonth() {
        changeMonth(calendarState.currentMonth.minusMonths(1))
    }

    /**
     * 탭 선택 처리
     */
    fun selectTab(index: Int) {
        selectedTabIndex = index
    }

    /**
     * 특정 날짜의 디자이너 스케줄 가져오기
     */
    fun getSchedulesForDate(date: LocalDate): List<DesignerSchedule> {
        return schedules[date]?.designerSchedules ?: emptyList()
    }

    /**
     * 새 디자이너/인턴 추가
     */
    fun addDesigner(name: String, color: Color, isIntern: Boolean) {
        val newDesigner = Designer(
            id = UUID.randomUUID().toString(),
            name = name,
            isIntern = isIntern,
            color = color
        )

        designers = designers + newDesigner

        // 새 디자이너의 스케줄 초기화 - 현재 월의 날짜들에 대해 기본 패턴 생성
        val yearMonth = calendarState.currentMonth
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()

        val updatedSchedules = schedules.toMutableMap()

        // 해당 월의 모든 날짜에 대해 기본 스케줄 생성
        var currentDate = firstDay
        while (currentDate.isBefore(lastDay) || currentDate.isEqual(lastDay)) {
            // 기본 근무 패턴 설정 - 인턴은 주말, 디자이너는 주중
            val timeSlot = if (isIntern) {
                if (currentDate.dayOfWeek.value >= 5) ScheduleTimeSlot.AFTERNOON else null // 인턴은 주말에 근무
            } else {
                if (currentDate.dayOfWeek.value < 5) ScheduleTimeSlot.MORNING else null // 디자이너는 주중에 근무
            }

            if (timeSlot != null) {
                val dailySchedule = updatedSchedules[currentDate] ?: DailySchedule(date = currentDate)
                val newSchedule = DesignerSchedule(
                    designer = newDesigner,
                    date = currentDate,
                    timeSlot = timeSlot
                )

                val updatedDesignerSchedules = dailySchedule.designerSchedules + newSchedule
                updatedSchedules[currentDate] = dailySchedule.copy(designerSchedules = updatedDesignerSchedules)
            }

            currentDate = currentDate.plusDays(1)
        }

        schedules = updatedSchedules
    }

    /**
     * 디자이너 휴무일 설정
     */
    fun setVacationDates(designer: Designer, dates: List<LocalDate>) {
        // 현재 휴무일 목록 업데이트
        vacationDates = vacationDates + (designer.id to dates)

        // 스케줄 업데이트
        updateSchedulesWithVacations()
    }



    /**
     * 휴무일 설정에 따라 스케줄 업데이트
     */
    private fun updateSchedulesWithVacations() {
        val updatedSchedules = schedules.toMutableMap()

        // 모든 디자이너의 휴무일 적용
        for ((designerId, dates) in vacationDates) {
            val designer = designers.find { it.id == designerId } ?: continue

            // 휴무일에 대해 스케줄 비활성화
            for (date in dates) {
                val dailySchedule = updatedSchedules[date] ?: continue

                // 해당 디자이너의 스케줄만 업데이트
                val updatedDesignerSchedules = dailySchedule.designerSchedules.map { schedule ->
                    if (schedule.designer.id == designerId) {
                        schedule.copy(isAvailable = false, note = "휴무")
                    } else {
                        schedule
                    }
                }

                updatedSchedules[date] = dailySchedule.copy(designerSchedules = updatedDesignerSchedules)
            }
        }

        schedules = updatedSchedules
    }

    /**
     * 초기 스케줄 생성 - 새로운 디자이너를 위한 기본 근무 패턴 생성
     */
    private fun initializeSchedulesForMonth(yearMonth: YearMonth) {
        // 현재 선택된 월에 대한 스케줄 초기화
        val schedulesMap = schedules.toMutableMap()
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()

        // 해당 월의 모든 날짜에 대해 빈 스케줄 생성
        var currentDate = firstDay
        while (currentDate.isBefore(lastDay) || currentDate.isEqual(lastDay)) {
            if (!schedulesMap.containsKey(currentDate)) {
                schedulesMap[currentDate] = DailySchedule(date = currentDate, designerSchedules = emptyList())
            }
            currentDate = currentDate.plusDays(1)
        }

        schedules = schedulesMap
    }

    /**
     * 디자이너 삭제
     */
    fun deleteDesigner(designerId: String) {
        // 디자이너 목록에서 삭제
        designers = designers.filter { it.id != designerId }

        // 스케줄에서도 해당 디자이너의 모든 일정 삭제
        val updatedSchedules = schedules.toMutableMap()

        for ((date, dailySchedule) in updatedSchedules) {
            val updatedDesignerSchedules = dailySchedule.designerSchedules.filter {
                it.designer.id != designerId
            }

            updatedSchedules[date] = dailySchedule.copy(designerSchedules = updatedDesignerSchedules)
        }

        schedules = updatedSchedules

        // 휴무일 설정에서도 삭제
        vacationDates = vacationDates.filterKeys { it != designerId }
    }
}
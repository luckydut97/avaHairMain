package com.luckydut97.avascheduler.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luckydut97.avascheduler.model.CalendarState
import com.luckydut97.avascheduler.model.DesignerSchedule
import java.time.LocalDate

/**
 * 달력 컴포넌트 - 높이 조정 가능, 마지막 주 잘림 방지
 */
@Composable
fun CalendarComponent(
    calendarState: CalendarState,
    schedules: Map<LocalDate, List<DesignerSchedule>>,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    cellHeightFactor: Float = 1.0f // 셀 높이 조정 팩터
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp) // 패딩 약간 줄임
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 요일 헤더
            WeekdayHeader()

            // 달력 날짜 표시
            val baseHeight = if (calendarState.weeksInCalendar >= 6) 58f else 65f
            val adjustedHeight = baseHeight * cellHeightFactor

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 각 날짜를 7열의 그리드로 표시
                val chunkedDays = calendarState.calendarDays.chunked(7)

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    chunkedDays.forEach { weekDays ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            weekDays.forEach { date ->
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    CalendarDayItem(
                                        date = date,
                                        isCurrentMonth = calendarState.isInCurrentMonth(date),
                                        isToday = calendarState.isToday(date),
                                        isSelected = calendarState.isSelected(date),
                                        schedules = schedules[date] ?: emptyList(),
                                        onDateClick = onDateClick,
                                        cellHeight = adjustedHeight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 요일 헤더 컴포넌트
 */
@Composable
private fun WeekdayHeader() {
    val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp), // 여백 줄임
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        daysOfWeek.forEach { day ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (day == "일") Color.Red else Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
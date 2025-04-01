package com.luckydut97.avascheduler.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.luckydut97.avascheduler.model.Designer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

/**
 * 휴무일 선택을 위한 캘린더 다이얼로그
 */
@Composable
fun VacationCalendarDialog(
    designer: Designer,
    yearMonth: YearMonth,
    onConfirm: (List<LocalDate>) -> Unit,
    onDismiss: () -> Unit
) {
    // 현재 표시되는 년월
    var currentYearMonth by remember { mutableStateOf(yearMonth) }

    // 선택된 날짜들
    var selectedDates by remember { mutableStateOf<List<LocalDate>>(emptyList()) }

    // 포맷터
    val monthFormatter = remember { DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREAN) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // 헤더
                Text(
                    text = "${designer.name} 휴무 설정",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 월 선택 영역
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
                            currentYearMonth = currentYearMonth.minusMonths(1)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "이전 달",
                            tint = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = monthFormatter.format(currentYearMonth),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            currentYearMonth = currentYearMonth.plusMonths(1)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "다음 달",
                            tint = Color.Gray
                        )
                    }
                }

                // 요일 헤더
                WeekDayHeader()

                // 캘린더 그리드
                VacationCalendarGrid(
                    yearMonth = currentYearMonth,
                    selectedDates = selectedDates,
                    designer = designer, // designer 매개변수 전달
                    onDateSelected = { date ->
                        selectedDates = if (selectedDates.contains(date)) {
                            selectedDates - date
                        } else {
                            selectedDates + date
                        }
                    }
                )

                // 선택된 날짜 정보
                if (selectedDates.isNotEmpty()) {
                    Text(
                        text = "선택된 휴무일: ${selectedDates.size}일",
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )

                    // 스크롤 가능한 선택된 날짜 목록
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd (E)", Locale.KOREAN)

                        items(selectedDates.size) { index ->
                            val date = selectedDates.sorted()[index]
                            Text(
                                text = dateFormatter.format(date),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                // 버튼 영역
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("취소")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onConfirm(selectedDates) }
                    ) {
                        Text("저장")
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
private fun WeekDayHeader() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        val days = listOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )

        days.forEach { day ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                    color = when (day) {
                        DayOfWeek.SUNDAY -> Color.Red
                        DayOfWeek.SATURDAY -> Color.Blue
                        else -> Color.DarkGray
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * 휴무일 선택 캘린더 그리드
 */
@Composable
private fun VacationCalendarGrid(
    yearMonth: YearMonth,
    selectedDates: List<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    designer: Designer // designer 매개변수 추가
) {
    // 해당 월의 첫 날과 마지막 날
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()

    // 캘린더 시작일 (해당 월 첫날의 주의 일요일)
    val firstDayOfCalendar = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

    // 주 단위로 날짜 계산
    val weeks = mutableListOf<List<LocalDate>>()
    var currentDay = firstDayOfCalendar

    while (currentDay.isBefore(lastDayOfMonth) || currentDay.equals(lastDayOfMonth) || currentDay.dayOfWeek != DayOfWeek.SATURDAY) {
        val week = (0..6).map { currentDay.plusDays(it.toLong()) }
        weeks.add(week)
        currentDay = currentDay.plusWeeks(1)
    }

    // 오늘 날짜
    val today = LocalDate.now()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                week.forEach { date ->
                    // 날짜가 해당 월에 속하는지 체크
                    val isCurrentMonth = date.month == yearMonth.month

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    selectedDates.contains(date) -> designer.color.copy(alpha = 0.7f)
                                    date.equals(today) -> Color.LightGray.copy(alpha = 0.3f)
                                    else -> Color.Transparent
                                }
                            )
                            .clickable(enabled = isCurrentMonth) {
                                onDateSelected(date)
                            }
                            .border(
                                width = if (date.equals(today)) 1.dp else 0.dp,
                                color = if (date.equals(today)) Color.Blue else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            fontSize = 14.sp,
                            color = when {
                                !isCurrentMonth -> Color.LightGray
                                date.dayOfWeek == DayOfWeek.SUNDAY -> Color.Red
                                date.dayOfWeek == DayOfWeek.SATURDAY -> Color.Blue
                                selectedDates.contains(date) -> Color.White
                                else -> Color.Black
                            },
                            textAlign = TextAlign.Center,
                            fontWeight = if (selectedDates.contains(date)) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
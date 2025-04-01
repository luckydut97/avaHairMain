package com.luckydut97.avascheduler.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luckydut97.avascheduler.model.DesignerSchedule
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * 달력의 날짜 셀을 표시하는 컴포넌트 - 순번 표시로 개선
 */
@Composable
fun CalendarDayItem(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    schedules: List<DesignerSchedule>,
    onDateClick: (LocalDate) -> Unit,
    cellHeight: Float
) {
    val dayColor = when {
        !isCurrentMonth -> Color.LightGray
        date.dayOfWeek == DayOfWeek.SUNDAY -> Color.Red
        else -> MaterialTheme.colorScheme.onSurface
    }

    val alphaValue = if (isCurrentMonth) 1f else 0.4f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(cellHeight.dp)
            .alpha(alphaValue)
            .clickable { onDateClick(date) }
            .padding(1.dp)
    ) {
        // 선택된 날짜 배경
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Blue.copy(alpha = 0.1f))
            )
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 날짜 숫자 표시
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 2.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                if (isToday) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(Color.Blue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = dayColor,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 스케줄 항목들을 표시 (순번으로 표시)
            if (schedules.isNotEmpty()) {
                // 순번별로 정렬
                val sortedSchedules = schedules.sortedBy {
                    it.note.substringAfter("순번 ").toIntOrNull() ?: Int.MAX_VALUE
                }

                // 각 디자이너 순번 표시
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 1.dp)
                ) {
                    sortedSchedules.forEach { schedule ->
                        DesignerOrderItem(schedule = schedule)
                    }
                }
            }
        }
    }
}

/**
 * 개별 디자이너 순번 아이템
 */
@Composable
private fun DesignerOrderItem(
    schedule: DesignerSchedule
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 색상 표시
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(schedule.designer.color)
        )

        Spacer(modifier = Modifier.width(2.dp))

        // 디자이너 이름
        Text(
            text = schedule.designer.name,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
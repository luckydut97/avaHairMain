package com.luckydut97.avascheduler.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luckydut97.avascheduler.ui.components.CalendarComponent
import com.luckydut97.avascheduler.ui.components.TabBarComponent
import com.luckydut97.avascheduler.ui.components.VacationCalendarDialog
import com.luckydut97.avascheduler.ui.theme.GradientEnd
import com.luckydut97.avascheduler.ui.theme.GradientStart
import com.luckydut97.avascheduler.viewmodel.SchedulerViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 스케줄러 메인 화면 - 최종 통합
 */
@Composable
fun SchedulerScreen(
    viewModel: SchedulerViewModel,
    modifier: Modifier = Modifier
) {
    // 배경 그라데이션 정의
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            GradientStart,
            GradientEnd
        )
    )

    // 월별 포맷터
    val monthFormatter = remember { DateTimeFormatter.ofPattern("M월", Locale.KOREAN) }
    val yearFormatter = remember { DateTimeFormatter.ofPattern("yyyy년", Locale.KOREAN) }

    // 날짜별 스케줄 맵으로 변환
    val schedulesByDate = remember(viewModel.schedules) {
        viewModel.schedules.mapValues { it.value.designerSchedules }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {
        // 메인 콘텐츠 컬럼
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 12.dp)
                .padding(top = 8.dp) // 상단 여백 줄임
        ) {
            // 상단 앱바 - 탭에 따라 다르게 표시
            if (viewModel.selectedTabIndex == 0) {
                // 캘린더 탭의 상단바
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 메뉴 아이콘
                    IconButton(
                        onClick = { /* 메뉴 열기 */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "메뉴",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 중앙 날짜 표시 - 에이바헤어 제거
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 이전 달 버튼
                        IconButton(
                            onClick = { viewModel.previousMonth() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowLeft,
                                contentDescription = "이전 달",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // 간소화된 날짜 표시
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = yearFormatter.format(viewModel.calendarState.currentMonth.atDay(1)),
                                color = Color.Black,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = monthFormatter.format(viewModel.calendarState.currentMonth.atDay(1)),
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }

                        // 다음 달 버튼
                        IconButton(
                            onClick = { viewModel.nextMonth() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "다음 달",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 우측 더미 공간
                    Box(modifier = Modifier.size(36.dp))
                }
            } else {
                // 디자이너 관리 탭은 DesignerManagementScreen에서 자체적으로 상단 UI 처리
                Box(modifier = Modifier.height(44.dp)) // 캘린더 탭과 높이 맞추기
            }

            Spacer(modifier = Modifier.height(8.dp)) // 간격 줄임

            // 콘텐츠 영역
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 0.dp) // 하단 패딩 제거하여 탭바 바로 위까지 확장
            ) {
                if (viewModel.selectedTabIndex == 0) {
                    // 캘린더 탭
                    CalendarComponent(
                        calendarState = viewModel.calendarState,
                        schedules = schedulesByDate,
                        onDateClick = { viewModel.selectDate(it) },
                        modifier = Modifier.fillMaxSize(),
                        cellHeightFactor = 1.3f // 셀 높이 증가 - 마지막 주가 잘리지 않도록
                    )
                } else {
                    // 디자이너 관리 탭
                    DesignerManagementScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // 하단 탭 바
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            TabBarComponent(
                selectedTabIndex = viewModel.selectedTabIndex,
                onTabSelected = { viewModel.selectTab(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
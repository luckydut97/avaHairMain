package com.luckydut97.avascheduler.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.luckydut97.avascheduler.ui.theme.TabBarBackground

/**
 * 하단 탭 바 컴포넌트 - 텍스트 제거 버전
 */
@Composable
fun TabBarComponent(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp) // 약간 높이 줄임
            .background(TabBarBackground)
    ) {
        // 캘린더 탭
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { onTabSelected(0) }
                .background(if (selectedTabIndex == 0) TabBarBackground.copy(alpha = 0.8f) else TabBarBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.CalendarMonth,
                contentDescription = "캘린더",
                tint = if (selectedTabIndex == 0) Color.White else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
            // 텍스트 제거됨
        }

        // 프로필 탭
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { onTabSelected(1) }
                .background(if (selectedTabIndex == 1) TabBarBackground.copy(alpha = 0.8f) else TabBarBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "직원 관리",
                tint = if (selectedTabIndex == 1) Color.White else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
            // 텍스트 제거됨
        }
    }
}
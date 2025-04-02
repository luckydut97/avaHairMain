package com.luckydut97.avascheduler.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
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
import com.luckydut97.avascheduler.ui.components.DeleteConfirmationDialog
import com.luckydut97.avascheduler.ui.components.ScheduleGenerationDialog
import com.luckydut97.avascheduler.ui.components.VacationCalendarDialog
import com.luckydut97.avascheduler.viewmodel.SchedulerViewModel
import java.time.YearMonth


/**
 * 개선된 디자이너 관리 화면
 */
@Composable
fun DesignerManagementScreen(
    viewModel: SchedulerViewModel,
    modifier: Modifier = Modifier
) {
    // UI 상태 변수들
    var showAddDesignerDialog by remember { mutableStateOf(false) }
    var showAddInternDialog by remember { mutableStateOf(false) }
    var showVacationDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showScheduleGenerationDialog by remember { mutableStateOf(false) }
    var selectedDesigner by remember { mutableStateOf<Designer?>(null) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 직접 상단 헤더 구현 (공백 없음)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "직원 관리",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        // 디자이너/인턴 목록 - 스크롤 가능
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            item {
                // 디자이너 섹션 헤더
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "디자이너",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // 디자이너 추가 버튼 - 세련된 아이콘 버튼으로 변경
                    IconButton(
                        onClick = { showAddDesignerDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "디자이너 추가",
                            tint = Color.Black
                        )
                    }
                }
            }

            // 디자이너 목록
            val designerList = viewModel.designers.filter { !it.isIntern }
            if (designerList.isEmpty()) {
                item {
                    Text(
                        text = "디자이너가 없습니다. 디자이너를 추가해주세요.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(designerList) { designer ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        DesignerItem(
                            designer = designer,
                            onToggleAvailability = {
                                selectedDesigner = designer
                                showVacationDialog = true
                            },
                            onDelete = {
                                selectedDesigner = designer
                                showDeleteConfirmDialog = true
                            }
                        )
                    }
                }
            }

            item {
                // 인턴 섹션 헤더
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "인턴",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // 인턴 추가 버튼 - 세련된 아이콘 버튼으로 변경
                    IconButton(
                        onClick = { showAddInternDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "인턴 추가",
                            tint = Color.Black
                        )
                    }
                }
            }

            // 인턴 목록
            val internList = viewModel.designers.filter { it.isIntern }
            if (internList.isEmpty()) {
                item {
                    Text(
                        text = "인턴이 없습니다. 인턴을 추가해주세요.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(internList) { intern ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        DesignerItem(
                            designer = intern,
                            onToggleAvailability = {
                                selectedDesigner = intern
                                showVacationDialog = true
                            },
                            onDelete = {
                                selectedDesigner = intern
                                showDeleteConfirmDialog = true
                            }
                        )
                    }
                }
            }

            // 하단 여백
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // 스케줄 생성 버튼
        ElevatedButton(
            onClick = {
                showScheduleGenerationDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "스케줄 생성",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "스케줄 자동 생성",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    // 디자이너 추가 다이얼로그
    if (showAddDesignerDialog) {
        AddPersonDialog(
            title = "디자이너 추가",
            isIntern = false,
            onAdd = { name, color ->
                viewModel.addDesigner(name, color, false)
                showAddDesignerDialog = false
            },
            onDismiss = { showAddDesignerDialog = false }
        )
    }

    // 인턴 추가 다이얼로그
    if (showAddInternDialog) {
        AddPersonDialog(
            title = "인턴 추가",
            isIntern = true,
            onAdd = { name, color ->
                viewModel.addDesigner(name, color, true)
                showAddInternDialog = false
            },
            onDismiss = { showAddInternDialog = false }
        )
    }

    // 휴무 설정 다이얼로그
    if (showVacationDialog && selectedDesigner != null) {
        VacationCalendarDialog(
            designer = selectedDesigner!!,
            yearMonth = YearMonth.now(), // 현재 월 사용
            onConfirm = { dates ->
                viewModel.setVacationDates(selectedDesigner!!, dates)
                showVacationDialog = false
                selectedDesigner = null
            },
            onDismiss = {
                showVacationDialog = false
                selectedDesigner = null
            }
        )
    }

    // 삭제 확인 대화상자
    if (showDeleteConfirmDialog && selectedDesigner != null) {
        DeleteConfirmationDialog(
            designer = selectedDesigner!!,
            onConfirm = {
                viewModel.deleteDesigner(selectedDesigner!!.id)
                showDeleteConfirmDialog = false
                selectedDesigner = null
            },
            onDismiss = {
                showDeleteConfirmDialog = false
                selectedDesigner = null
            }
        )
    }

    // 스케줄 생성 확인 대화상자
    if (showScheduleGenerationDialog) {
        ScheduleGenerationDialog(
            onConfirm = {
                viewModel.generateSchedules()
                showScheduleGenerationDialog = false
            },
            onDismiss = {
                showScheduleGenerationDialog = false
            }
        )
    }
}

/**
 * 디자이너/인턴 아이템 컴포넌트 - 더 짧고 현대적인 UI
 */
@Composable
fun DesignerItem(
    designer: Designer,
    onToggleAvailability: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 디자이너 색상 표시
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(designer.color)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 디자이너 이름
        Text(
            text = designer.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        // 휴무 설정 버튼 - 아이콘만 사용
        IconButton(
            onClick = onToggleAvailability,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "휴무 설정",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }

        // 삭제 버튼
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "삭제",
                tint = Color.Red,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * 디자이너/인턴 추가 다이얼로그
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPersonDialog(
    title: String,
    isIntern: Boolean,
    onAdd: (name: String, color: Color) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color.Red) }
    var showColorPicker by remember { mutableStateOf(false) }

    // 기본 색상 목록
    val colorOptions = listOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Yellow,
        Color.Magenta,
        Color.Cyan,
        Color(0xFFFF9800), // Orange
        Color(0xFF9C27B0), // Purple
        Color(0xFF795548)  // Brown
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 이름 입력 필드
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("이름") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // 색상 선택
                Text(
                    text = "색상 선택",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 색상 표시 영역
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { showColorPicker = true }
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(selectedColor, CircleShape)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "선택된 색상",
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "색상 선택",
                            tint = Color.Gray
                        )
                    }

                    // 색상 드롭다운 메뉴
                    DropdownMenu(
                        expanded = showColorPicker,
                        onDismissRequest = { showColorPicker = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        colorOptions.forEach { color ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .background(color, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("색상")
                                    }
                                },
                                onClick = {
                                    selectedColor = color
                                    showColorPicker = false
                                }
                            )
                        }
                    }
                }

                // 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("취소")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onAdd(name, selectedColor) },
                        enabled = name.isNotBlank()
                    ) {
                        Text("추가")
                    }
                }
            }
        }
    }
}
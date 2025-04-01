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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.luckydut97.avascheduler.viewmodel.SchedulerViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 개선된 디자이너 관리 화면
 */
@Composable
fun DesignerManagementScreen(
    viewModel: SchedulerViewModel,
    modifier: Modifier = Modifier
) {
    // 현재 선택된 월
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }

    // 월 포맷터
    val monthFormatter = remember { DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREAN) }

    // UI 상태 변수들
    var showAddDesignerDialog by remember { mutableStateOf(false) }
    var showAddInternDialog by remember { mutableStateOf(false) }
    var showVacationDialog by remember { mutableStateOf(false) }
    var selectedDesigner by remember { mutableStateOf<Designer?>(null) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 상단바 - 월 선택 UI
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 이전 월 버튼
            IconButton(
                onClick = {
                    currentYearMonth = currentYearMonth.minusMonths(1)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "이전 월",
                    tint = Color.Black
                )
            }

            // 현재 월 표시
            Text(
                text = monthFormatter.format(currentYearMonth),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // 다음 월 버튼
            IconButton(
                onClick = {
                    currentYearMonth = currentYearMonth.plusMonths(1)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "다음 월",
                    tint = Color.Black
                )
            }
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
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "디자이너",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // 디자이너 추가 버튼
                    IconButton(
                        onClick = { showAddDesignerDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "디자이너 추가",
                            tint = Color.White
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
                    DesignerItem(
                        designer = designer,
                        onToggleAvailability = {
                            selectedDesigner = designer
                            showVacationDialog = true
                        }
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }

            item {
                // 인턴 섹션 헤더
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "인턴",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // 인턴 추가 버튼
                    IconButton(
                        onClick = { showAddInternDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.secondary, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "인턴 추가",
                            tint = Color.White
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
                    DesignerItem(
                        designer = intern,
                        onToggleAvailability = {
                            selectedDesigner = intern
                            showVacationDialog = true
                        }
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }

            // 하단 여백
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
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
            yearMonth = currentYearMonth,
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
}

/**
 * 디자이너/인턴 아이템 컴포넌트
 */
@Composable
fun DesignerItem(
    designer: Designer,
    onToggleAvailability: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 디자이너 색상 표시
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(designer.color)
                .border(1.dp, Color.LightGray, CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 디자이너 이름
        Text(
            text = designer.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        // 휴무 설정 버튼
        Button(
            onClick = onToggleAvailability,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text("휴무 설정")
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

/**
 * 휴무 설정용 캘린더 다이얼로그
 */
@Composable
fun VacationCalendarDialog(
    designer: Designer,
    yearMonth: YearMonth,
    onConfirm: (List<LocalDate>) -> Unit,
    onDismiss: () -> Unit
) {
    // 휴무 설정을 위한 캘린더 다이얼로그 구현
    // 실제 구현에서는 달력 그리드를 표시하고 날짜를 다중 선택할 수 있도록 함

    // 선택된 휴무일 목록 (상태 관리)
    var selectedDates by remember { mutableStateOf(emptyList<LocalDate>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${designer.name} 휴무 설정",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "휴무일로 설정할 날짜를 선택하세요.",
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 여기에 미니 캘린더 표시 (실제 구현 시 추가)
                Text(
                    text = "캘린더 구현 예정...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )

                // 선택된 날짜 표시
                if (selectedDates.isNotEmpty()) {
                    Text(
                        text = "선택된 휴무일: ${selectedDates.size}일",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedDates) }
            ) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("취소")
            }
        }
    )
}
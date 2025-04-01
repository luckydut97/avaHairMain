package com.luckydut97.avascheduler.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * 스케줄 자동 생성 확인 대화상자
 */
@Composable
fun ScheduleGenerationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "스케줄 자동 생성")
        },
        text = {
            Text(
                text = "현재 월의 스케줄을 자동으로 생성하시겠습니까?\n" +
                        "모든 디자이너와 인턴의 휴무일을 고려하여 일정이 생성됩니다.\n\n" +
                        "주의: 기존에 생성된 스케줄은 덮어씌워집니다."
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text("생성하기")
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
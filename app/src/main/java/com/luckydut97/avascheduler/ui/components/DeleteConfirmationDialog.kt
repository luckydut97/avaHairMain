package com.luckydut97.avascheduler.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.luckydut97.avascheduler.model.Designer

/**
 * 디자이너/인턴 삭제 확인 대화상자
 */
@Composable
fun DeleteConfirmationDialog(
    designer: Designer,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "삭제 확인")
        },
        text = {
            Text(
                text = "${designer.name}${if (designer.isIntern) " 인턴" else " 디자이너"}을(를) 삭제하시겠습니까?\n" +
                        "관련된 모든 스케줄 정보도 함께 삭제됩니다."
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text("삭제", color = Color.White)
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
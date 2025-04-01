package com.luckydut97.avascheduler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luckydut97.avascheduler.ui.screens.SchedulerScreen
import com.luckydut97.avascheduler.ui.theme.AvaschedulerTheme
import com.luckydut97.avascheduler.viewmodel.SchedulerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 시스템 UI(상태바, 네비게이션바)를 에지-투-에지로 표시하도록 설정
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AvaschedulerTheme {
                // Surface 컨테이너
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: SchedulerViewModel = viewModel()
                    // 앱 컨텍스트로 ViewModel 초기화
                    viewModel.initialize(applicationContext)

                    SchedulerScreen(viewModel = viewModel)
                }
            }
        }
    }
}
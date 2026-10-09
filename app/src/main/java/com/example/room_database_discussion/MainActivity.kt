package com.example.room_database_discussion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.room_database_discussion.ui.StudentScreen
import com.example.room_database_discussion.ui.theme.Room_database_discussionTheme
import com.example.room_database_discussion.viewmodel.StudentViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
        )
        window.isNavigationBarContrastEnforced = false
        setContent {
            val viewModel: StudentViewModel = viewModel()
            Room_database_discussionTheme {
                StudentScreen(viewModel = viewModel)
            }
        }
    }
}

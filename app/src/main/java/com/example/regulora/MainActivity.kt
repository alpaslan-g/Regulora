package com.example.regulora

import android.os.Bundle
import androidx.compose.runtime.remember
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.regulora.ui.screens.MainScreen
import com.example.regulora.ui.screens.RoutineManagerScreen
import com.example.regulora.ui.viewmodel.RoutineViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = remember { RoutineViewModel() }
            RoutineManagerScreen(viewModel = viewModel)
        }
    }
}

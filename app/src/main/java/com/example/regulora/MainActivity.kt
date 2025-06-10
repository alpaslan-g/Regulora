package com.example.regulora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.regulora.ui.screens.MainScreen
import com.example.regulora.ui.screens.RoutineManagerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //MainScreen()
            RoutineManagerScreen()
        }
    }
}

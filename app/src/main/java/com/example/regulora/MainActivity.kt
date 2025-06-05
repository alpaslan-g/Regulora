package com.example.regulora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
// Entfernen Sie dies, wenn Sie keine M2-Komponenten verwenden:
// import androidx.compose.material.*
import androidx.compose.material3.Scaffold // M3 Scaffold importieren
import androidx.compose.runtime.remember // Für rememberNavController benötigt
import androidx.compose.ui.Modifier // Für Modifier.padding, falls benötigt
import androidx.compose.foundation.layout.padding // Für Modifier.padding, falls benötigt
import androidx.navigation.compose.NavHost // Sicherstellen, dass dies vorhanden ist
import androidx.navigation.compose.composable // Sicherstellen, dass dies vorhanden ist
import androidx.navigation.compose.rememberNavController // Sicherstellen, dass dies vorhanden ist
import com.example.regulora.ui.screens.*
import com.example.regulora.ui.theme.ReguloraTheme // Importieren Sie Ihr M3 Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReguloraTheme { // Wenden Sie Ihr M3 Theme an
                val nav = rememberNavController()
                // Stellen Sie sicher, dass paddingValues von Scaffold verwendet wird, falls von NavHost benötigt
                Scaffold { paddingValues ->
                    // NavHost kann paddingValues verwenden, um Platz für AppBar etc. zu lassen
                    NavHost(
                        navController = nav,
                        startDestination = "actuatorConfig",
                        modifier = Modifier.padding(paddingValues) // Padding hier anwenden
                    ) {
                        composable("actuatorConfig") { ActuatorConfigScreen(nav) }
                        composable("routineEditor") { RoutineEditorScreen(nav) }
                        composable("logChart") { LogChartScreen(/*nav falls benötigt*/) }
                        composable("otaUpload") { OtaUploadScreen(/*nav falls benötigt*/) }
                    }
                }
            }
        }
    }
}
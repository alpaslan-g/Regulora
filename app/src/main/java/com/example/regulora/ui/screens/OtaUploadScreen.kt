package com.example.regulora.ui.screens

// Entfernen Sie den M2-Import, wenn Sie nur M3 verwenden wollen:
// import androidx.compose.material.*
import androidx.compose.material3.Text // WICHTIG: Import für Material 3 Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box // Beispiel für Layout
import androidx.compose.foundation.layout.fillMaxSize // Beispiel für Layout
import androidx.compose.ui.Alignment // Beispiel für Layout
import androidx.compose.ui.Modifier // Beispiel für Layout

@Composable
fun OtaUploadScreen() {
    // Beispielhaftes Layout, um den Text anzuzeigen
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("OTA-Upload-Modul kommt bald!")
    }
}
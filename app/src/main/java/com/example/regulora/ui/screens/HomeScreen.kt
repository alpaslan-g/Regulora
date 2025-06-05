package com.example.regulora.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.regulora.ui.screens.LogChartScreen

@Composable
fun HomeScreen() {
    var isConnected by remember { mutableStateOf(false) }

    Column(Modifier.padding(16.dp)) {
        Text("Willkommen bei Regulora", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(12.dp))

        if (!isConnected) {
            Text("❌ Kein Controller verbunden")
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                // Simuliere Verbindung
                isConnected = true
            }) {
                Text("Mit Controller verbinden")
            }
        } else {
            Text("✅ Verbunden mit Controller")
            Spacer(Modifier.height(8.dp))
            Text("Aktive Routine: Tagesbetrieb")
            Text("Temp: 23.1 °C, RH: 56%, Boden: 42%")
            Spacer(Modifier.height(12.dp))
            Text("Verlauf heute:")
            LogChartScreen()
        }
    }
}

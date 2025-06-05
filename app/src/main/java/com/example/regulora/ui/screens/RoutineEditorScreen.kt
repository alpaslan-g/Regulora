package com.example.regulora.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button // M3 Button
import androidx.compose.material3.MaterialTheme // M3 MaterialTheme
import androidx.compose.material3.OutlinedTextField // M3 OutlinedTextField
import androidx.compose.material3.Text // M3 Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
// com.example.regulora.data.* wird nicht direkt in diesem Snippet verwendet, aber ggf. für TODOs

@Composable
fun RoutineEditorScreen(nav: NavController) {
    var name by remember { mutableStateOf("Routine 1") }

    Column(
        Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Routine erstellen",
            style = MaterialTheme.typography.headlineSmall // M3 Typografie-Stil
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: hier Conditions + Regeln einbauen (z.B. mit ui/components/ConditionRow.kt)

        Button(onClick = {
            // TODO: Logik zum Speichern der Routine (Name, Conditions, Regeln)
            // Senden via MQTT etc.
            nav.navigate("logChart")
        }) {
            Text("Routine speichern & weiter")
        }
    }
}
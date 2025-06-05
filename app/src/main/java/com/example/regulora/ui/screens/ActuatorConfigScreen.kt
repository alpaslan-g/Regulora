package com.example.regulora.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button // M3 Button
import androidx.compose.material3.MaterialTheme // M3 MaterialTheme
import androidx.compose.material3.OutlinedTextField // M3 OutlinedTextField
import androidx.compose.material3.Text // M3 Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList // Für mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.regulora.data.ActuatorConfig

@Composable
fun ActuatorConfigScreen(nav: NavController) {
    // Es ist besser, die Liste außerhalb der Schleife zu modifizieren.
    // Das direkte Hinzufügen während der Komposition in einer Schleife kann zu unerwünschten
    // Rekompositionen und falschem Verhalten führen.
    // Betrachten Sie einen ViewModel-Ansatz oder eine stabilere Zustandsverwaltung.
    // Für dieses Beispiel vereinfachen wir es, aber seien Sie sich der Implikationen bewusst.

    val actuatorLabels = remember {
        mutableStateListOf("", "", "", "") // Eine Liste für die Labels der 4 Textfelder
    }
    // Die 'configs'-Liste sollte basierend auf den 'actuatorLabels' erstellt werden,
    // wenn die Daten tatsächlich verwendet oder gespeichert werden, nicht bei jeder Rekomposition.

    Column(
        Modifier
            .padding(16.dp)
            .fillMaxSize(), // Füllt oft den ganzen Bildschirm
        horizontalAlignment = Alignment.CenterHorizontally // Zentriert Inhalt horizontal
    ) {
        Text(
            text = "Aktuatoren konfigurieren",
            style = MaterialTheme.typography.headlineSmall // M3 Typografie-Stil
        )
        Spacer(modifier = Modifier.height(16.dp))

        for (i in 0..3) { // Indizes 0 bis 3 für die Liste
            OutlinedTextField(
                value = actuatorLabels[i],
                onValueChange = { actuatorLabels[i] = it },
                label = { Text("Steckdose ${i + 1}") }, // Anzeigen als Steckdose 1-4
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp)) // Abstand zwischen Textfeldern
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Hier würden Sie die 'configs' erstellen und speichern, z.B.:
            val configsToSave = actuatorLabels.mapIndexed { index, label ->
                ActuatorConfig(index + 1, label)
            }.filter { it.label.isNotBlank() } // Nur die mit Label speichern
            // TODO: configsToSave speichern (z.B. im ViewModel oder RoutineStore)
            nav.navigate("routineEditor")
        }) {
            Text("Weiter zur Routine")
        }
    }
}
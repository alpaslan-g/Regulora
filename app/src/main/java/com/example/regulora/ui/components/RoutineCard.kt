package com.example.regulora.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons // Neu für Icons, falls noch nicht oben
import androidx.compose.material.icons.filled.Edit // Neu
import androidx.compose.material.icons.filled.ToggleOff // Neu
import androidx.compose.material.icons.filled.ToggleOn // Neu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.regulora.data.model.PreconfiguredActuator
import com.example.regulora.data.model.Rule
import com.example.regulora.data.model.Routine

@Composable
fun RoutineCard(
    routine: Routine,
    allRules: List<Rule>, // Geändert: `rule: Rule?` wurde zu `allRules: List<Rule>`
    preconfiguredActuators: List<PreconfiguredActuator>,
    onEdit: (Routine) -> Unit,
    onToggleEnabled: (Routine, Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // Kleinere Anpassung für konsistentere Abstände
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Optionale Erhöhung für bessere Optik
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header-Zeile mit Name, Edit-Button und Toggle-Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Sorgt für Verteilung
            ) {
                Text(
                    text = routine.name.ifEmpty { "Unbenannte Routine" }, // Fallback für leeren Namen
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f) // Nimmt verfügbaren Platz ein
                )
                // Gruppe für Bearbeiten und Aktivieren/Deaktivieren Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Switch ersetzt durch IconButton für konsistenteres Design mit Edit
                    IconButton(onClick = { onToggleEnabled(routine, !routine.enabled) }) {
                        Icon(
                            imageVector = if (routine.enabled) Icons.Filled.ToggleOn else Icons.Filled.ToggleOff,
                            contentDescription = if (routine.enabled) "Routine deaktivieren" else "Routine aktivieren",
                            tint = if (routine.enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    IconButton(onClick = { onEdit(routine) }) { // TextButton zu IconButton geändert für bessere Optik
                        Icon(Icons.Filled.Edit, contentDescription = "Bearbeiten")
                    }
                }
            }

            // Anzeige von Start- und Endzeit, falls vorhanden
            if (routine.startTime.isNotBlank() || routine.endTime.isNotBlank()) { // Oder statt Und, falls nur eines gesetzt ist
                Spacer(modifier = Modifier.height(8.dp))
                val timeText = when {
                    routine.startTime.isNotBlank() && routine.endTime.isNotBlank() -> "${routine.startTime} – ${routine.endTime}"
                    routine.startTime.isNotBlank() -> "Start: ${routine.startTime}"
                    routine.endTime.isNotBlank() -> "Ende: ${routine.endTime}"
                    else -> ""
                }
                if (timeText.isNotEmpty()) {
                    Text(
                        text = timeText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp)) // Etwas mehr Platz vor den Aktionen

            // Sektion für Aktor-Aktionen
            if (routine.actuatorActions.isNotEmpty()) {
                Text(
                    text = "Aktionen:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                // Jede Aktor-Aktion wird hier einzeln behandelt
                routine.actuatorActions.forEachIndexed { actionIndex, actuatorAction ->
                    val actuator = preconfiguredActuators.find { it.id == actuatorAction.actuatorId }
                    val associatedRule = actuatorAction.ruleId?.let { ruleIdForAction ->
                        allRules.find { r -> r.id == ruleIdForAction }
                    }

                    // Eine kleine Einrückung für jede Aktion
                    Column(modifier = Modifier.padding(start = 8.dp, top = if (actionIndex > 0) 8.dp else 0.dp)) {
                        Text(
                            text = "${actuator?.name ?: "Aktor (ID: ${actuatorAction.actuatorId})"}: ${actuatorAction.actionValue}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Regelinformationen für diese spezifische Aktion
                        if (associatedRule != null) {
                            Text(
                                text = "Bedingung: ${associatedRule.name.ifEmpty { "(Regel ohne Namen)" }}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                            // Optional: Detaillierte Anzeige der Bedingungen der Regel
                            /*
                            associatedRule.conditionGroup.conditions.forEachIndexed { conditionIdx, condition ->
                                val operatorString = if (conditionIdx > 0 && associatedRule.conditionGroup.conditions.size > 1) {
                                    " ${associatedRule.conditionGroup.operator.name} "
                                } else ""
                                Text(
                                    text = "$operatorString${condition.sensorType.name} ${condition.comparator.symbol} ${condition.value}",
                                    style = MaterialTheme.typography.labelSmall, // Kleiner für Details
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                            */
                        } else if (actuatorAction.ruleId != null) {
                            // Regel-ID ist gesetzt, aber Regel nicht in allRules gefunden
                            Text(
                                text = "Bedingung: Regel (ID: ${actuatorAction.ruleId}) nicht gefunden.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        } else {
                            // Keine Regel für diese Aktion spezifiziert
                            Text(
                                text = "Bedingung: Immer aktiv (keine spezifische Regel).",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            } else {
                // Fallback, falls keine Aktionen definiert sind
                Text(
                    text = "Keine Aktionen für diese Routine definiert.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
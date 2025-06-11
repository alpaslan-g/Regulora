package com.example.regulora.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.regulora.data.model.*
import java.util.*

@Composable
fun RoutineDialog(
    onDismiss: () -> Unit,
    onSave: (TimeRoutine) -> Unit,
    availableActuators: List<String>
) {
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    var rules by remember { mutableStateOf(mutableListOf<ActuatorRule>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val routine = TimeRoutine(
                    startTime = startTime,
                    endTime = endTime,
                    actuatorRules = rules.toList()
                )
                onSave(routine)
            }) {
                Text("Speichern")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Abbrechen") }
        },
        title = { Text("Routine hinzufügen") },
        text = {
            Column {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Startzeit (z. B. 06:00)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("Endzeit (z. B. 12:00)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(rules.size) { index ->
                        RuleCard(
                            rule = rules[index],
                            onDelete = { rules.removeAt(index) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    rules.add(
                        ActuatorRule(
                            actuatorName = availableActuators.firstOrNull() ?: "Unbenannt",
                            action = "ON",
                            conditionGroup = ConditionGroup()
                        )
                    )
                }) {
                    Text("Regel hinzufügen")
                }
            }
        }
    )
}

@Composable
fun RuleCard(
    rule: ActuatorRule,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Aktuator: ${rule.actuatorName}")
            Text("Aktion: ${rule.action}")
            Text("Bedingungen: ${rule.conditionGroup.conditions.size}")
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = onDelete) { Text("Löschen") }
        }
    }
}

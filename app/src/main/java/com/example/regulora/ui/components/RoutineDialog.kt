package com.example.regulora.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.regulora.data.model.*
import com.example.regulora.data.model.TimeRoutine

@Composable
fun RoutineDialog(
    actuatorNames: List<String>,
    onConfirm: (TimeRoutine) -> Unit,
    onDismiss: () -> Unit
) {
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    // Zustand für Regeln je Aktuator
    val ruleConditions = remember {
        mutableStateMapOf<String, MutableList<SensorCondition>>()
    }

    val selectedOperators = remember {
        mutableStateMapOf<String, LogicalOperator>()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val rules = actuatorNames.associateWith { actuator ->
                    val conditions = ruleConditions[actuator] ?: emptyList()
                    val op = selectedOperators[actuator] ?: LogicalOperator.AND
                    ConditionGroup(conditions, op)
                }

                onConfirm(
                    TimeRoutine(
                        id = System.currentTimeMillis().toString(),
                        startTime = startTime,
                        endTime = endTime,
                        actuatorRules = rules.map { (actuatorName, conditionGroup) ->
                            ActuatorRule(actuatorName, conditionGroup, action = "ON") // oder "OFF" – falls konfigurierbar
                        }
                    )
                )
            }) {
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        },
        title = { Text("Routine erstellen") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Startzeit (z.B. 08:00)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("Endzeit (z.B. 12:00)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                actuatorNames.forEach { actuator ->
                    Text("Regeln für Aktuator: $actuator", style = MaterialTheme.typography.titleMedium)

                    val localConditions = ruleConditions.getOrPut(actuator) { mutableListOf() }
                    val selectedOperator = selectedOperators.getOrPut(actuator) { LogicalOperator.AND }

                    // Logik-Operator
                    Row(modifier = Modifier.fillMaxWidth()) {
                        LogicalOperator.values().forEach { op ->
                            Row {
                                RadioButton(
                                    selected = selectedOperator == op,
                                    onClick = { selectedOperators[actuator] = op }
                                )
                                Text(text = op.name)
                            }
                        }
                    }

                    localConditions.forEachIndexed { index, condition ->
                        Text("- ${condition.sensorType.name} ${condition.comparator.name} ${condition.value}")
                    }

                    var sensorType by remember { mutableStateOf(SensorType.TEMPERATURE) }
                    var comparator by remember { mutableStateOf(Comparator.LT) }
                    var value by remember { mutableStateOf("") }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DropdownMenuBox(title = "Sensor", options = SensorType.values().toList(), selected = sensorType) {
                            sensorType = it
                        }
                        Spacer(Modifier.width(4.dp))
                        DropdownMenuBox(title = "Vergleich", options = Comparator.values().toList(), selected = comparator) {
                            comparator = it
                        }
                        Spacer(Modifier.width(4.dp))
                        OutlinedTextField(
                            value = value,
                            onValueChange = { value = it },
                            label = { Text("Wert") },
                            modifier = Modifier.width(80.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Button(onClick = {
                            value.toFloatOrNull()?.let {
                                localConditions.add(
                                    SensorCondition(sensorType, comparator, it)
                                )
                            }
                        }) {
                            Text("+")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    )
}

@Composable
fun <T> DropdownMenuBox(
    title: String,
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = title)
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(selected.toString())
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach {
                    DropdownMenuItem(text = { Text(it.toString()) }, onClick = {
                        onSelect(it)
                        expanded = false
                    })
                }
            }
        }
    }
}

package com.example.regulora.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.regulora.data.model.*

@Composable
fun RoutineDialog(
    actuatorNames: List<String>,
    onConfirm: (TimeRoutine) -> Unit,
    onDismiss: () -> Unit
) {
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    // Pro Aktuator: Aktionsmodus
    val actionModes = remember {
        mutableStateMapOf<String, String>().apply {
            actuatorNames.forEach { put(it, "ALWAYS_ON") }
        }
    }

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
                val actuatorRules = actuatorNames.map { actuator ->
                    val mode = actionModes[actuator]
                    val conditionGroup = if (mode == "CONDITIONAL") {
                        ConditionGroup(
                            ruleConditions[actuator]?.toList() ?: emptyList(),
                            selectedOperators[actuator] ?: LogicalOperator.AND
                        )
                    } else {
                        ConditionGroup(emptyList())
                    }

                    val action = when (mode) {
                        "ALWAYS_ON" -> "ON"
                        "ALWAYS_OFF" -> "OFF"
                        else -> "ON" // Default
                    }

                    ActuatorRule(
                        actuatorName = actuator,
                        conditionGroup = conditionGroup,
                        action = action
                    )
                }

                onConfirm(
                    TimeRoutine(
                        startTime = startTime,
                        endTime = endTime,
                        actuatorRules = actuatorRules
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Startzeit (z. B. 08:00)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("Endzeit (z. B. 20:00)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                actuatorNames.forEach { actuator ->
                    Text(
                        "Aktuator: $actuator",
                        style = MaterialTheme.typography.titleMedium
                    )

                    var localMode by remember { mutableStateOf(actionModes[actuator] ?: "ALWAYS_ON") }
                    actionModes[actuator] = localMode

                    DropdownMenuBox(
                        title = "Modus wählen",
                        options = listOf("ALWAYS_ON", "ALWAYS_OFF", "CONDITIONAL"),
                        selected = localMode
                    ) {
                        localMode = it
                        actionModes[actuator] = it
                    }

                    if (localMode == "CONDITIONAL") {
                        val localConditions = ruleConditions.getOrPut(actuator) { mutableListOf() }
                        val usedSensors = localConditions.map { it.sensorType }
                        val availableSensors = SensorType.values().filter { it !in usedSensors }

                        val selectedOperator = selectedOperators.getOrPut(actuator) { LogicalOperator.AND }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            LogicalOperator.values().forEach { op ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = selectedOperator == op,
                                        onClick = { selectedOperators[actuator] = op }
                                    )
                                    Text(op.name)
                                }
                            }
                        }

                        localConditions.forEach {
                            Text("- ${it.sensorType} ${it.comparator} ${it.value}")
                        }

                        if (availableSensors.isNotEmpty()) {
                            var sensorType by remember { mutableStateOf(availableSensors.first()) }
                            var comparator by remember { mutableStateOf(Comparator.LT) }
                            var value by remember { mutableStateOf("") }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DropdownMenuBox(
                                    title = "Sensor",
                                    options = availableSensors,
                                    selected = sensorType
                                ) { sensorType = it }

                                Spacer(Modifier.width(4.dp))

                                DropdownMenuBox(
                                    title = "Vergleich",
                                    options = Comparator.values().toList(),
                                    selected = comparator
                                ) { comparator = it }

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
                                        localConditions.add(SensorCondition(sensorType, comparator, it))
                                    }
                                }) {
                                    Text("+")
                                }
                            }
                        } else {
                            Text("Alle Sensoren wurden verwendet.", style = MaterialTheme.typography.bodySmall)
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

    Column(modifier = Modifier.padding(bottom = 4.dp)) {
        Text(title)
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(selected.toString())
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach {
                    DropdownMenuItem(
                        text = { Text(it.toString()) },
                        onClick = {
                            onSelect(it)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

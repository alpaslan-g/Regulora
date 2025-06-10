package com.example.regulora.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.regulora.data.model.*
import com.example.regulora.data.model.Comparator
import com.example.regulora.data.*
import java.util.*

@Composable
fun RoutineDialog(
    initialRoutine: Routine? = null,
    onSave: (Routine) -> Unit,
    onDismiss: () -> Unit
) {
    val id = initialRoutine?.id ?: UUID.randomUUID().toString()
    var startTime by remember { mutableStateOf(initialRoutine?.startTime ?: "00:00") }
    var endTime by remember { mutableStateOf(initialRoutine?.endTime ?: "00:00") }
    var actuatorName by remember { mutableStateOf("Lüfter") }
    var sensorType by remember { mutableStateOf(SensorType.TEMPERATURE) }
    var comparator by remember { mutableStateOf(Comparator.GT) }
    var value by remember { mutableStateOf("25") }
    var action by remember { mutableStateOf("ON") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val rule = Rule(
                    actuatorName,
                    ConditionGroup(
                        conditions = listOf(
                            SensorCondition(sensorType, comparator, value.toFloat())
                        )
                    ),
                    action
                )
                val routine = Routine(id, startTime, endTime, listOf(rule))
                onSave(routine)
            }) {
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        },
        title = { Text("Routine konfigurieren") },
        text = {
            Column {
                OutlinedTextField(startTime, { startTime = it }, label = { Text("Startzeit") })
                OutlinedTextField(endTime, { endTime = it }, label = { Text("Endzeit") })
                OutlinedTextField(actuatorName, { actuatorName = it }, label = { Text("Aktuator") })
                Spacer(modifier = Modifier.height(8.dp))
                DropdownMenuBox("Sensor", SensorType.values(), sensorType) { sensorType = it }
                DropdownMenuBox("Vergleich", Comparator.values(), comparator) { comparator = it }
                OutlinedTextField(value, { value = it }, label = { Text("Schwellwert") })
                DropdownMenuBox("Aktion", listOf("ON", "OFF"), action) { action = it }
            }
        }
    )
}

@Composable
fun <T> DropdownMenuBox(label: String, items: Array<T>, selected: T, onSelect: (T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = selected.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach {
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

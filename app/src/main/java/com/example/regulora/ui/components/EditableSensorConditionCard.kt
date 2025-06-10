package com.example.regulora.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.regulora.data.model.Comparator
import com.example.regulora.data.model.SensorType

// Datenklasse bleibt hier oder in einem gemeinsamen "states" File.
// import com.example.regulora.ui.dialogs.EditableSensorConditionState // Falls es dort zentralisiert wird

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableSensorConditionCard(
    conditionState: EditableSensorConditionState, // Diese Datenklasse kommt von RuleCreatorDialog
    onConditionChange: (EditableSensorConditionState) -> Unit,
    onDeleteCondition: () -> Unit,
    isLastCondition: Boolean
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        var sensorDropdownExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = sensorDropdownExpanded, onExpandedChange = { sensorDropdownExpanded = !sensorDropdownExpanded }) {
            OutlinedTextField(
                value = conditionState.sensorType.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Sensor") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sensorDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = sensorDropdownExpanded, onDismissRequest = { sensorDropdownExpanded = false }) {
                SensorType.values().forEach { type ->
                    DropdownMenuItem(text = { Text(type.name) }, onClick = {
                        onConditionChange(conditionState.copy(sensorType = type, valueError = null)) // Reset error on change
                        sensorDropdownExpanded = false
                    })
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            var comparatorDropdownExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = comparatorDropdownExpanded,
                onExpandedChange = { comparatorDropdownExpanded = !comparatorDropdownExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = conditionState.comparator.symbol,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vergleich") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = comparatorDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = comparatorDropdownExpanded, onDismissRequest = { comparatorDropdownExpanded = false }) {
                    Comparator.values().forEach { comp ->
                        DropdownMenuItem(text = { Text(comp.symbol) }, onClick = {
                            onConditionChange(conditionState.copy(comparator = comp, valueError = null)) // Reset error
                            comparatorDropdownExpanded = false
                        })
                    }
                }
            }
            OutlinedTextField(
                value = conditionState.value,
                onValueChange = { onConditionChange(conditionState.copy(value = it, valueError = null)) },
                label = { Text("Wert") },
                isError = conditionState.valueError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        if (conditionState.valueError != null) {
            Text(conditionState.valueError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 8.dp))
        }

        if (!isLastCondition) {
            TextButton(onClick = onDeleteCondition, modifier = Modifier.align(Alignment.End)) {
                Text("Bedingung löschen", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
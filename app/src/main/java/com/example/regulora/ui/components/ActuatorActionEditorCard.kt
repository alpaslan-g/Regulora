package com.example.regulora.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue // Import kann bleiben, wird aber in dieser Datei nicht direkt genutzt
import androidx.compose.runtime.setValue // Import kann bleiben, wird aber in dieser Datei nicht direkt genutzt
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.regulora.data.model.PreconfiguredActuator

// Du importierst EditableActuatorActionState wahrscheinlich aus der Datei, in der RoutineEditorDialog ist
// z.B. com.example.regulora.ui.dialogs.EditableActuatorActionState oder ähnlich, je nachdem, wo du sie definiert hast.
// Stelle sicher, dass dieser Import korrekt ist, falls EditableActuatorActionState nicht in dieser Datei ist.
// Für dieses Beispiel gehe ich davon aus, dass sie entweder hier ist (was du kommentiert hast)
// oder korrekt importiert wird. Da sie im Fehlerlog nicht als "unresolved reference" für den Typ selbst
// auftauchte, sondern nur für die Eigenschaft 'action', ist der Typ-Import wahrscheinlich schon okay.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActuatorActionEditorCard(
    actionState: EditableActuatorActionState, // Diese Datenklasse kommt von RoutineEditorDialog
    preconfiguredActuators: List<PreconfiguredActuator>,
    onActionChange: (EditableActuatorActionState) -> Unit,
    onDeleteAction: () -> Unit,
    isLastAction: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            var actuatorDropdownExpanded by remember { mutableStateOf(false) }
            val selectedActuator = preconfiguredActuators.find { it.id == actionState.selectedActuatorId }

            ExposedDropdownMenuBox(
                expanded = actuatorDropdownExpanded,
                onExpandedChange = { actuatorDropdownExpanded = !actuatorDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedActuator?.name ?: "Aktor auswählen", // Kleiner UI Verbesserungsvorschlag für leeren Zustand
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Aktor") },
                    placeholder = { Text("Aktor auswählen") }, // Kann bleiben, aber value wird durch obige Änderung nie leer sein
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = actuatorDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    isError = actionState.actuatorError != null
                )
                ExposedDropdownMenu(
                    expanded = actuatorDropdownExpanded,
                    onDismissRequest = { actuatorDropdownExpanded = false }
                ) {
                    preconfiguredActuators.forEach { actuator ->
                        DropdownMenuItem(
                            text = { Text(actuator.name) },
                            onClick = {
                                onActionChange(actionState.copy(selectedActuatorId = actuator.id, actuatorError = null))
                                actuatorDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            if (actionState.actuatorError != null) {
                Text(actionState.actuatorError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(8.dp))

            var actionTypeDropdownExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = actionTypeDropdownExpanded,
                onExpandedChange = { actionTypeDropdownExpanded = !actionTypeDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = actionState.actionValue, // <<< KORREKTUR HIER
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Aktion") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = actionTypeDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = actionTypeDropdownExpanded, onDismissRequest = { actionTypeDropdownExpanded = false }) {
                    listOf("ON", "OFF").forEach { act ->
                        DropdownMenuItem(text = { Text(act) }, onClick = {
                            onActionChange(actionState.copy(actionValue = act)) // <<< KORREKTUR HIER
                            actionTypeDropdownExpanded = false
                        })
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Der Rest des Codes für den Löschen-Button scheint in Ordnung zu sein
            // und hatte keine Compiler-Fehler bezüglich 'action'
            if (!isLastAction || (isLastAction && actionState.selectedActuatorId == null)) {
                TextButton(
                    onClick = onDeleteAction,
                    modifier = Modifier.align(Alignment.End),
                    enabled = !isLastAction || (isLastAction && actionState.selectedActuatorId == null)
                ) {
                    Text(
                        "Diese Aktion löschen",
                        color = if(!isLastAction || (isLastAction && actionState.selectedActuatorId == null)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
            }
        }
    }
}
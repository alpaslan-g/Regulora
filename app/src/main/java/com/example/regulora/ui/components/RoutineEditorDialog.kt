package com.example.regulora.ui.components // Oder wo auch immer dein Dialog liegt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.regulora.data.model.*
import com.example.regulora.ui.dialogs.CustomTimePickerDialog
import com.example.regulora.ui.dialogs.RuleCreatorDialog // Stelle sicher, dass der Pfad korrekt ist
import java.text.SimpleDateFormat
import java.util.UUID
import java.util.Calendar // Expliziter Import für Calendar
import java.util.Locale


// --- ANGEPASSTE Hilfsdatenklassen für den Editor-Zustand ---

data class EditableSensorConditionState(
    val id: String = UUID.randomUUID().toString(),
    var sensorType: SensorType = SensorType.TEMPERATURE,
    var comparator: Comparator = Comparator.GT,
    var value: String = "25",
    var valueError: String? = null
)

data class EditableRuleDefinitionState(
    var id: String = UUID.randomUUID().toString(), // Kann eine temporäre ID oder die ID einer bestehenden Regel sein
    var name: String = "",
    var conditions: SnapshotStateList<EditableSensorConditionState> = mutableStateListOf(EditableSensorConditionState()),
    var operator: LogicalOperator = LogicalOperator.AND,
    var nameError: String? = null,
    var generalConditionError: String? = null
)

data class EditableActuatorActionState(
    val uniqueEditorId: String = UUID.randomUUID().toString(), // Für Compose LazyColumn Keys
    var selectedActuatorId: String? = null,
    var actionValue: String = "ON", // Umbenannt von 'action' zur Klarheit
    var actuatorError: String? = null,
    var assignedRuleId: String? = null, // ID der zugeordneten Regel aus allAvailableRules
    var currentEditableRuleForAction: EditableRuleDefinitionState? = null // Wenn eine Regel für DIESE Aktion bearbeitet/erstellt wird
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineEditorDialog(
    initialRoutine: Routine?,
    allAvailableRules: List<Rule>,
    preconfiguredActuators: List<PreconfiguredActuator>,
    onSaveRoutine: (updatedRoutine: Routine) -> Unit,
    onSaveRuleAndGet: (ruleToSave: Rule) -> Rule, // Gibt die gespeicherte Regel (mit finaler ID) zurück
    onDismiss: () -> Unit
) {
    var routineName by rememberSaveable(initialRoutine) { mutableStateOf(initialRoutine?.name ?: "") }
    var routineNameError by remember { mutableStateOf<String?>(null) }
    val routineId = initialRoutine?.id ?: "new_routine_placeholder_id" // Wird im ViewModel finalisiert

    val initialStartCalendar = Calendar.getInstance().apply {
        initialRoutine?.startTime?.let { timeString ->
            parseTimeStringToCalendar(timeString, this)
        } ?: run { set(Calendar.HOUR_OF_DAY, 6); set(Calendar.MINUTE, 0) }
    }
    var startHour by rememberSaveable(initialRoutine?.startTime) { mutableStateOf(initialStartCalendar.get(Calendar.HOUR_OF_DAY)) }
    var startMinute by rememberSaveable(initialRoutine?.startTime) { mutableStateOf(initialStartCalendar.get(Calendar.MINUTE)) }

    val initialEndCalendar = Calendar.getInstance().apply {
        initialRoutine?.endTime?.let { timeString ->
            parseTimeStringToCalendar(timeString, this)
        } ?: run { set(Calendar.HOUR_OF_DAY, 18); set(Calendar.MINUTE, 0) }
    }
    var endHour by rememberSaveable(initialRoutine?.endTime) { mutableStateOf(initialEndCalendar.get(Calendar.HOUR_OF_DAY)) }
    var endMinute by rememberSaveable(initialRoutine?.endTime) { mutableStateOf(initialEndCalendar.get(Calendar.MINUTE)) }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    var showRuleCreatorDialog by remember { mutableStateOf(false) }
    var ruleDefinitionForCreatorDialog by remember { mutableStateOf<EditableRuleDefinitionState?>(null) }
    var editingRuleForActionEditorId by remember { mutableStateOf<String?>(null) }

    val editableActuatorActions = remember(initialRoutine?.actuatorActions) {
        mutableStateListOf<EditableActuatorActionState>().apply {
            initialRoutine?.actuatorActions?.forEach { action ->
                add(
                    EditableActuatorActionState(
                        selectedActuatorId = action.actuatorId,
                        actionValue = action.actionValue,
                        assignedRuleId = action.ruleId
                    )
                )
            }
            if (isEmpty()) {
                add(EditableActuatorActionState())
            }
        }
    }
    var generalError by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    val formattedStartTime = formatTime(startHour, startMinute)
    val formattedEndTime = formatTime(endHour, endMinute)

    fun convertEditableRuleToRule(editableDef: EditableRuleDefinitionState): Rule? {
        if (editableDef.name.isBlank()) return null
        val ruleConditions = editableDef.conditions.mapNotNull { editableCond ->
            editableCond.value.toFloatOrNull()?.let { numValue ->
                SensorCondition(
                    id = editableCond.id,
                    sensorType = editableCond.sensorType,
                    comparator = editableCond.comparator,
                    value = numValue
                )
            }
        }
        if (ruleConditions.size != editableDef.conditions.size || ruleConditions.isEmpty()) {
            return null
        }
        return Rule(
            id = if (editableDef.id == "new_rule_placeholder_id" || allAvailableRules.none { it.id == editableDef.id})
                UUID.randomUUID().toString()
            else editableDef.id,
            name = editableDef.name.trim(),
            conditionGroup = ConditionGroup(
                conditions = ruleConditions,
                operator = editableDef.operator
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.95f)
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth() // Passt sich der Breite des Inhalts an
                .wrapContentHeight(), // Passt sich der Höhe des Inhalts an
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier.padding(16.dp).widthIn(max= 550.dp)) {
                Text("Routine konfigurieren", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = routineName,
                    onValueChange = { routineName = it; routineNameError = null },
                    label = { Text("Name der Routine") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = routineNameError != null,
                    singleLine = true
                )
                if (routineNameError != null) {
                    Text(routineNameError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = formattedStartTime,
                        onValueChange = { /* No-op */ },
                        label = { Text("Startzeit") },
                        modifier = Modifier.weight(1f)
                            .clickable { focusManager.clearFocus(true); showStartTimePicker = true }
                            .onFocusChanged { if (it.isFocused) { showStartTimePicker = true; focusManager.clearFocus(true) } },
                        readOnly = true
                    )
                    OutlinedTextField(
                        value = formattedEndTime,
                        onValueChange = { /* No-op */ },
                        label = { Text("Endzeit") },
                        modifier = Modifier.weight(1f)
                            .clickable { focusManager.clearFocus(true); showEndTimePicker = true }
                            .onFocusChanged { if (it.isFocused) { showEndTimePicker = true; focusManager.clearFocus(true) } },
                        readOnly = true
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Auszuführende Aktionen:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.weight(1f, fill = false).heightIn(min = 150.dp, max = 400.dp)) {
                    itemsIndexed(editableActuatorActions, key = { _, item -> item.uniqueEditorId }) { index, actionState ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(Modifier.padding(16.dp)) { // Mehr Padding in der Karte
                                // Aktor-Auswahl
                                var actuatorDropdownExpanded by remember { mutableStateOf(false) }
                                ExposedDropdownMenuBox(
                                    expanded = actuatorDropdownExpanded,
                                    onExpandedChange = { actuatorDropdownExpanded = !actuatorDropdownExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = preconfiguredActuators.find { it.id == actionState.selectedActuatorId }?.name ?: "Aktor auswählen",
                                        onValueChange = {}, readOnly = true, label = { Text("Aktor") },
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
                                                    editableActuatorActions[index] = actionState.copy(selectedActuatorId = actuator.id, actuatorError = null)
                                                    actuatorDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                if (actionState.actuatorError != null) {
                                    Text(actionState.actuatorError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                }

                                // Aktion (ON/OFF)
                                var actionDropdownExpanded by remember { mutableStateOf(false) }
                                ExposedDropdownMenuBox(
                                    expanded = actionDropdownExpanded,
                                    onExpandedChange = { actionDropdownExpanded = !actionDropdownExpanded },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = actionState.actionValue,
                                        onValueChange = {}, readOnly = true, label = { Text("Aktion") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = actionDropdownExpanded) },
                                        modifier = Modifier.menuAnchor().fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(expanded = actionDropdownExpanded, onDismissRequest = { actionDropdownExpanded = false }) {
                                        listOf("ON", "OFF").forEach { actVal ->
                                            DropdownMenuItem(text = { Text(actVal) }, onClick = {
                                                editableActuatorActions[index] = actionState.copy(actionValue = actVal)
                                                actionDropdownExpanded = false
                                            })
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Bedingung (Regel):", style = MaterialTheme.typography.titleSmall)

                                val currentRuleDisplay = actionState.assignedRuleId?.let { ruleId -> allAvailableRules.find { it.id == ruleId } }
                                val ruleNameToShow = actionState.currentEditableRuleForAction?.name?.takeIf { it.isNotBlank() }
                                    ?: currentRuleDisplay?.name

                                if (ruleNameToShow != null) {
                                    Text("Regel: $ruleNameToShow", style = MaterialTheme.typography.bodyMedium)
                                    // Details nur anzeigen, wenn es eine zugewiesene Regel ist und keine gerade bearbeitete (Details kommen vom Creator)
                                    currentRuleDisplay?.takeIf { actionState.currentEditableRuleForAction == null }?.let { rule ->
                                        if (rule.conditionGroup.conditions.isNotEmpty()){
                                            Text(
                                                "Details: ${rule.conditionGroup.conditions.joinToString(separator = " ${rule.conditionGroup.operator.name} ") { "${it.sensorType.name} ${it.comparator.symbol} ${it.value}" }}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                } else {
                                    Text("Keine spezifische Regel (immer aktiv im Zeitfenster)", style = MaterialTheme.typography.bodyMedium)
                                }
                                // Fehler für diese spezifische Regel (aus currentEditableRuleForAction)
                                actionState.currentEditableRuleForAction?.generalConditionError?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                }
                                actionState.currentEditableRuleForAction?.nameError?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                }


                                Row(Modifier.padding(top = 8.dp)) {
                                    TextButton(onClick = {
                                        editingRuleForActionEditorId = actionState.uniqueEditorId
                                        val existingRuleDef = actionState.assignedRuleId?.let { ruleId ->
                                            allAvailableRules.find { it.id == ruleId }
                                        }?.let { rule ->
                                            EditableRuleDefinitionState(
                                                id = rule.id, name = rule.name,
                                                conditions = rule.conditionGroup.conditions.map { cond -> // cond als Name für die Variable
                                                    EditableSensorConditionState(
                                                        id = cond.id,
                                                        sensorType = cond.sensorType,
                                                        comparator = cond.comparator,
                                                        value = cond.value.toString()
                                                    )
                                                }.let { normalList -> mutableStateListOf(*normalList.toTypedArray()) },
                                                operator = rule.conditionGroup.operator
                                            )
                                        } ?: actionState.currentEditableRuleForAction
                                        ?: EditableRuleDefinitionState(id = "new_rule_placeholder_id", conditions = mutableStateListOf(EditableSensorConditionState()))

                                        ruleDefinitionForCreatorDialog = existingRuleDef
                                        showRuleCreatorDialog = true
                                    }) {
                                        Text(if (ruleNameToShow != null) "Regel bearbeiten" else "Regel erstellen/zuweisen")
                                    }
                                    if (actionState.assignedRuleId != null || actionState.currentEditableRuleForAction != null) {
                                        Spacer(Modifier.width(8.dp))
                                        TextButton(onClick = {
                                            editableActuatorActions[index] = actionState.copy(assignedRuleId = null, currentEditableRuleForAction = null)
                                        }) { Text("Regel entfernen") }
                                    }
                                }
                            }
                        }
                        if (editableActuatorActions.size > 1) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                                IconButton(onClick = {
                                    editableActuatorActions.removeAt(index)
                                }) {
                                    Icon(Icons.Filled.Delete, "Aktion löschen", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        if (index < editableActuatorActions.size - 1) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { editableActuatorActions.add(EditableActuatorActionState()) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.AddCircle, contentDescription = "Aktion hinzufügen Icon", modifier = Modifier.size(ButtonDefaults.IconSize))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Weitere Aktion hinzufügen")
                }

                if (generalError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(generalError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("Abbrechen") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        generalError = null
                        routineNameError = null
                        var isOverallValid = true

                        if (routineName.isBlank()) {
                            routineNameError = "Routinename darf nicht leer sein."
                            isOverallValid = false
                        }

                        val finalActuatorActions = mutableListOf<ActuatorAction>()
                        var anyActionHasErrors = false

                        val updatedEditableActions = editableActuatorActions.map { editableAction ->
                            var currentActionCopy = editableAction.copy(actuatorError = null) // Fehler zurücksetzen
                            var actionIsValidForSaving = true

                            if (currentActionCopy.selectedActuatorId == null) {
                                currentActionCopy = currentActionCopy.copy(actuatorError = "Aktor auswählen")
                                anyActionHasErrors = true
                                actionIsValidForSaving = false
                            }

                            var finalRuleIdForThisAction: String? = currentActionCopy.assignedRuleId

                            currentActionCopy.currentEditableRuleForAction?.let { ruleDef ->
                                var mutableRuleDef = ruleDef.copy(nameError = null, generalConditionError = null) // Fehler zurücksetzen
                                var ruleDefIsValid = true

                                if (mutableRuleDef.name.isBlank()) {
                                    mutableRuleDef = mutableRuleDef.copy(nameError = "Name für Regel erforderlich.")
                                    anyActionHasErrors = true
                                    ruleDefIsValid = false
                                }
                                val validConditions = mutableRuleDef.conditions.filter { it.value.toFloatOrNull() != null }
                                if (validConditions.isEmpty()) {
                                    mutableRuleDef = mutableRuleDef.copy(generalConditionError = "Mindestens eine gültige Bedingung für Regel erforderlich.")
                                    anyActionHasErrors = true
                                    ruleDefIsValid = false
                                } else if (validConditions.size != mutableRuleDef.conditions.size) {
                                    mutableRuleDef = mutableRuleDef.copy(generalConditionError = "Einige Bedingungswerte sind ungültig.")
                                    anyActionHasErrors = true
                                    // Regel könnte trotzdem gespeichert werden, wenn mindestens eine Bedingung gültig ist
                                    // Aber für strikte Logik: ruleDefIsValid = false
                                }


                                if (ruleDefIsValid && actionIsValidForSaving) {
                                    convertEditableRuleToRule(mutableRuleDef)?.let { ruleToSave ->
                                        val savedRule = onSaveRuleAndGet(ruleToSave)
                                        finalRuleIdForThisAction = savedRule.id
                                        // Erfolgreich gespeichert, keine Fehler mehr für diese spezifische Regeldefinition
                                        currentActionCopy = currentActionCopy.copy(currentEditableRuleForAction = null, assignedRuleId = savedRule.id)
                                    } ?: run {
                                        mutableRuleDef = mutableRuleDef.copy(generalConditionError = (mutableRuleDef.generalConditionError ?: "") + " Fehler beim Konvertieren der Regel.")
                                        anyActionHasErrors = true
                                        actionIsValidForSaving = false // Mache die gesamte Aktion ungültig
                                        currentActionCopy = currentActionCopy.copy(currentEditableRuleForAction = mutableRuleDef)
                                    }
                                } else {
                                    actionIsValidForSaving = false // Mache die gesamte Aktion ungültig
                                    currentActionCopy = currentActionCopy.copy(currentEditableRuleForAction = mutableRuleDef)
                                }
                            } // Ende currentEditableRuleForAction Block

                            if (actionIsValidForSaving) {
                                finalActuatorActions.add(
                                    ActuatorAction(
                                        actuatorId = currentActionCopy.selectedActuatorId!!,
                                        actionValue = currentActionCopy.actionValue,
                                        ruleId = finalRuleIdForThisAction
                                    )
                                )
                            }
                            currentActionCopy // gibt die potenziell aktualisierte Kopie zurück
                        }

                        editableActuatorActions.clear()
                        editableActuatorActions.addAll(updatedEditableActions)


                        if (anyActionHasErrors) {
                            generalError = (generalError ?: "") + " Bitte korrigieren Sie die Fehler in den Aktionen/Regeln."
                            isOverallValid = false
                        }

                        if (editableActuatorActions.isEmpty() && isOverallValid) { // Sollte nicht passieren, wenn die erste Prüfung greift
                            generalError = (generalError ?: "") + " Fügen Sie mindestens eine Aktion hinzu."
                            isOverallValid = false
                        } else if (finalActuatorActions.isEmpty() && isOverallValid && editableActuatorActions.isNotEmpty()) {
                            // Wenn keine Fehler markiert wurden, aber trotzdem keine finale Aktion erstellt werden konnte
                            generalError = (generalError ?: "") + " Mindestens eine gültige Aktor-Aktion ist erforderlich."
                            isOverallValid = false
                        }


                        if (!isOverallValid) {
                            return@Button
                        }

                        val routineToSave = Routine(
                            id = routineId,
                            name = routineName.trim(),
                            actuatorActions = finalActuatorActions,
                            startTime = formattedStartTime,
                            endTime = formattedEndTime,
                            enabled = initialRoutine?.enabled ?: true
                        )
                        onSaveRoutine(routineToSave)
                    }) { Text("Speichern") }
                }
            }
        }
    }

    if (showRuleCreatorDialog && ruleDefinitionForCreatorDialog != null) {
        RuleCreatorDialog(
            editableRuleDefinitionState = ruleDefinitionForCreatorDialog!!,
            onSaveRule = { savedRuleDefFromCreator ->
                val actionIndex = editableActuatorActions.indexOfFirst { it.uniqueEditorId == editingRuleForActionEditorId }
                if (actionIndex != -1) {
                    // Übernehme die ID von der ursprünglichen Definition, falls es eine Bearbeitung war, sonst die von savedRuleDefFromCreator (die eine neue sein könnte)
                    val idToKeep = ruleDefinitionForCreatorDialog?.id?.takeIf { it != "new_rule_placeholder_id" } ?: savedRuleDefFromCreator.id

                    editableActuatorActions[actionIndex] = editableActuatorActions[actionIndex].copy(
                        currentEditableRuleForAction = savedRuleDefFromCreator.copy(
                            id= idToKeep, // Stelle sicher, dass die ID korrekt beibehalten/gesetzt wird
                            conditions = savedRuleDefFromCreator.conditions.toList().let { normalList ->
                                mutableStateListOf(*normalList.toTypedArray())
                            }
                        ),
                        assignedRuleId = null
                    )
                }
                showRuleCreatorDialog = false
                editingRuleForActionEditorId = null
                ruleDefinitionForCreatorDialog = null
                generalError = null
            },
            onDismiss = {
                showRuleCreatorDialog = false
                editingRuleForActionEditorId = null
                ruleDefinitionForCreatorDialog = null
            }
        )
    }

    if (showStartTimePicker) {
        val currentCalendar = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, startHour); set(Calendar.MINUTE, startMinute) }
        val timePickerState = rememberTimePickerState(
            initialHour = currentCalendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = currentCalendar.get(Calendar.MINUTE),
            is24Hour = true
        )
        CustomTimePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startHour = timePickerState.hour
                    startMinute = timePickerState.minute
                    showStartTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showStartTimePicker = false }) { Text("Abbrechen") } },
            title = "Startzeit auswählen"
        ) { TimePicker(state = timePickerState, modifier = Modifier.padding(16.dp)) }
    }

    if (showEndTimePicker) {
        val currentCalendar = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, endHour); set(Calendar.MINUTE, endMinute) }
        val timePickerState = rememberTimePickerState(
            initialHour = currentCalendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = currentCalendar.get(Calendar.MINUTE),
            is24Hour = true
        )
        CustomTimePickerDialog(
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endHour = timePickerState.hour
                    endMinute = timePickerState.minute
                    showEndTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showEndTimePicker = false }) { Text("Abbrechen") } },
            title = "Endzeit auswählen"
        ) { TimePicker(state = timePickerState, modifier = Modifier.padding(16.dp)) }
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
}

private fun parseTimeStringToCalendar(timeString: String, calendar: Calendar) {
    try {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.parse(timeString)?.let { date ->
            val parsedCalendar = Calendar.getInstance().apply { time = date }
            calendar.set(Calendar.HOUR_OF_DAY, parsedCalendar.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, parsedCalendar.get(Calendar.MINUTE))
        } ?: run {
            // Fallback, falls Parsen null ergibt
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
        }
    } catch (e: Exception) {
        // Fehler beim Parsen, ggf. Loggen oder Standardzeit verwenden
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
    }
}
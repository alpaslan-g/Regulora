package com.example.regulora.ui.dialogs

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.regulora.data.model.LogicalOperator
import com.example.regulora.ui.components.EditableSensorConditionCard
import com.example.regulora.ui.components.EditableSensorConditionState // Importiere den State von components
import com.example.regulora.ui.components.EditableRuleDefinitionState

// Die EditableRuleDefinitionState Datenklasse könnte auch hier sein oder in einem gemeinsamen State-File.
// Wir nehmen an, sie wird vom RoutineEditorDialog (dem Aufrufer) bereitgestellt.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuleCreatorDialog(
    editableRuleDefinitionState: EditableRuleDefinitionState,
    onSaveRule: (EditableRuleDefinitionState) -> Unit,
    onDismiss: () -> Unit
) {
    var localRuleDefState by remember(editableRuleDefinitionState.id) { // Keyed by ID
        mutableStateOf(
            editableRuleDefinitionState.copy(
                // Erstelle eine tiefe Kopie der conditions Liste, um Seiteneffekte zu vermeiden
                conditions = editableRuleDefinitionState.conditions.map { it.copy() }.toMutableStateList()
            )
        )
    }

    AlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(vertical = 16.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 450.dp)) {
                Text("Regel definieren", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = localRuleDefState.name,
                    onValueChange = { localRuleDefState = localRuleDefState.copy(name = it, nameError = null) },
                    label = { Text("Name der Regel") },
                    isError = localRuleDefState.nameError != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                if (localRuleDefState.nameError != null) {
                    Text(localRuleDefState.nameError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Bedingungen:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                if (localRuleDefState.generalConditionError != null) {
                    Text(localRuleDefState.generalConditionError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (localRuleDefState.conditions.size > 1) {
                    var operatorDropdownExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = operatorDropdownExpanded,
                        onExpandedChange = { operatorDropdownExpanded = !operatorDropdownExpanded },
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = localRuleDefState.operator.name,
                            onValueChange = {}, readOnly = true, label = { Text("Logischer Operator") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = operatorDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = operatorDropdownExpanded, onDismissRequest = { operatorDropdownExpanded = false }) {
                            LogicalOperator.values().forEach { op ->
                                DropdownMenuItem(text = { Text(op.name) }, onClick = {
                                    localRuleDefState = localRuleDefState.copy(operator = op)
                                    operatorDropdownExpanded = false
                                })
                            }
                        }
                    }
                }

                LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) {
                    itemsIndexed(localRuleDefState.conditions, key = { _, item -> item.id }) { index, conditionState ->
                        EditableSensorConditionCard(
                            conditionState = conditionState,
                            onConditionChange = { updatedCondition ->
                                val mutableConditions = localRuleDefState.conditions.toMutableList()
                                mutableConditions[index] = updatedCondition
                                localRuleDefState = localRuleDefState.copy(conditions = mutableConditions.toMutableStateList(), generalConditionError = null)
                            },
                            onDeleteCondition = {
                                if (localRuleDefState.conditions.size > 1) {
                                    val mutableConditions = localRuleDefState.conditions.toMutableList()
                                    mutableConditions.removeAt(index)
                                    localRuleDefState = localRuleDefState.copy(conditions = mutableConditions.toMutableStateList())
                                } else {
                                    val mutableConditions = localRuleDefState.conditions.toMutableList()
                                    mutableConditions[index] = mutableConditions[index].copy(valueError = "Mind. 1 Bedingung") // Set error on the condition
                                    localRuleDefState = localRuleDefState.copy(
                                        conditions = mutableConditions.toMutableStateList(),
                                        generalConditionError = "Mindestens eine Bedingung ist erforderlich."
                                    )
                                }
                            },
                            isLastCondition = localRuleDefState.conditions.size == 1
                        )
                        if (index < localRuleDefState.conditions.size - 1) {
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        val mutableConditions = localRuleDefState.conditions.toMutableList()
                        mutableConditions.add(EditableSensorConditionState()) // Verwende den State aus components
                        localRuleDefState = localRuleDefState.copy(conditions = mutableConditions.toMutableStateList(), generalConditionError = null)
                        if (localRuleDefState.conditions.size == 2 && localRuleDefState.conditions.first().valueError == "Mind. 1 Bedingung") {
                            val firstCond = localRuleDefState.conditions.first().copy(valueError = null)
                            val newConds = localRuleDefState.conditions.toMutableList()
                            newConds[0] = firstCond
                            localRuleDefState = localRuleDefState.copy(conditions = newConds.toMutableStateList())
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Weitere Bedingung hinzufügen")
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Abbrechen") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        var isRuleValid = true
                        if (localRuleDefState.name.isBlank()) {
                            localRuleDefState = localRuleDefState.copy(nameError = "Name darf nicht leer sein")
                            isRuleValid = false
                        } else {
                            localRuleDefState = localRuleDefState.copy(nameError = null)
                        }

                        if (localRuleDefState.conditions.isEmpty()){
                            localRuleDefState = localRuleDefState.copy(generalConditionError = "Mindestens eine Bedingung hinzufügen.")
                            isRuleValid = false
                        } else {
                            var allConditionsValidInRule = true
                            val updatedConditions = localRuleDefState.conditions.map { cond ->
                                if (cond.value.toFloatOrNull() == null) {
                                    allConditionsValidInRule = false
                                    cond.copy(valueError = "Ungültig")
                                } else {
                                    cond.copy(valueError = null)
                                }
                            }.toMutableStateList()
                            localRuleDefState = localRuleDefState.copy(conditions = updatedConditions)
                            if (!allConditionsValidInRule) {
                                localRuleDefState = localRuleDefState.copy(generalConditionError = "Bitte alle Bedingungswerte korrigieren.")
                                isRuleValid = false
                            } else {
                                localRuleDefState = localRuleDefState.copy(generalConditionError = null)
                            }
                        }

                        if (isRuleValid) {
                            onSaveRule(localRuleDefState.copy())
                        }
                    }) { Text("Regel speichern") }
                }
            }
        }
    }
}

// Optionale Extension Function, um die Konvertierung zu vereinfachen
fun <T> List<T>.toMutableStateList(): SnapshotStateList<T> {
    return androidx.compose.runtime.snapshots.SnapshotStateList<T>().also { it.addAll(this) }
}
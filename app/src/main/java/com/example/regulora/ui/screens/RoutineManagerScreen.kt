package com.example.regulora.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.regulora.data.model.*
import com.example.regulora.ui.components.RoutineDialog
import com.example.regulora.ui.viewmodel.RoutineViewModel

@Composable
fun RoutineManagerScreen(viewModel: RoutineViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Tagesroutine auswählen", style = MaterialTheme.typography.titleLarge)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            viewModel.routines.forEachIndexed { index, routine ->
                Button(
                    onClick = { viewModel.selectDailyRoutine(index) },
                    colors = if (index == viewModel.selectedDailyRoutineIndex)
                        ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    else ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                ) {
                    Text(routine.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Zeit-Routinen", style = MaterialTheme.typography.titleMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val selected = viewModel.getSelectedRoutine()
            items(selected.routines.size) { index ->
                val routine = selected.routines[index]
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Von ${routine.startTime} bis ${routine.endTime}")
                        routine.actuatorRules.forEach { rule ->
                            Text("→ ${rule.actuatorName}: ${rule.action} falls ${rule.conditionGroup.conditions.joinToString { "${it.sensorType} ${it.comparator} ${it.value}" }}")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showDialog = true }) {
            Text("Routine hinzufügen")
        }

        if (showDialog) {
            RoutineDialog(
                onDismiss = { showDialog = false },
                onSave = {
                    viewModel.addTimeRoutine(it)
                    showDialog = false
                },
                availableActuators = viewModel.getAvailableActuators()
            )
        }
    }
}

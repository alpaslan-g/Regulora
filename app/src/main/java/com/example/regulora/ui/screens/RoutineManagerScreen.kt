package com.example.regulora.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons // Import für Icons
import androidx.compose.material.icons.filled.Add // Import für das Add-Icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue // Diese Imports sind ok
import androidx.compose.runtime.setValue // Diese Imports sind ok
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.regulora.data.model.Routine // Wird direkt verwendet
import com.example.regulora.ui.components.RoutineCard
import com.example.regulora.ui.components.RoutineEditorDialog
import com.example.regulora.viewmodel.RoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineManagerScreen(viewModel: RoutineViewModel = viewModel()) {
    // Daten vom ViewModel als State sammeln
    val currentRoutineGroup by remember { mutableStateOf(viewModel.currentRoutineGroup) }
    val allRules by viewModel.allRules.collectAsState()
    val preconfiguredActuators by viewModel.preconfiguredActuators.collectAsState()

    // Wird verwendet, um das ViewModel zu beobachten und die UI neu zu zeichnen, wenn sich currentRoutineGroup ändert
    // Dieser LaunchedEffect ist hier nicht unbedingt für die Recomposition von currentRoutineGroup selbst nötig,
    // da Compose das Objekt selbst beobachten sollte. Er könnte nützlich sein, wenn interne
    // Änderungen in currentRoutineGroup nicht immer zu einer Neuzuweisung des Objekts führen,
    // aber in deinem Fall wird es durch das ViewModel wahrscheinlich als neues Objekt gesetzt.
    LaunchedEffect(viewModel.currentRoutineGroup) {
        // Hier könntest du reagieren, wenn das Objekt `currentRoutineGroup` vom ViewModel
        // explizit neu zugewiesen wird, falls nötig.
    }

    var showDialog by remember { mutableStateOf(false) }
    var routineToEdit by remember { mutableStateOf<Routine?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("Tagesroutine: ${currentRoutineGroup.name}")
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                routineToEdit = null // Neue Routine erstellen
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Neue Routine hinzufügen")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (currentRoutineGroup.routines.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Keine Routinen hinzugefügt. Klicke auf '+' um zu starten.")
                }
            } else {
                // Die Logik zum Finden von 'associatedRule' wurde entfernt,
                // da 'RoutineCard' jetzt 'allRules' erhält und dies intern handhabt.
                currentRoutineGroup.routines.forEach { routineItem ->
                    RoutineCard(
                        routine = routineItem,
                        allRules = allRules, // RoutineCard erhält jetzt die komplette Liste der Regeln
                        preconfiguredActuators = preconfiguredActuators,
                        onEdit = { selectedRoutine ->
                            routineToEdit = selectedRoutine
                            showDialog = true
                        },
                        onToggleEnabled = { routineForToggle, newStatus ->
                            viewModel.setRoutineEnabledState(routineForToggle, newStatus)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        if (showDialog) {
            RoutineEditorDialog(
                initialRoutine = routineToEdit,
                allAvailableRules = allRules,
                preconfiguredActuators = preconfiguredActuators,
                onSaveRoutine = { savedRoutine -> // Geänderter Parameter
                    viewModel.saveRoutine(savedRoutine) // Angepasster ViewModel-Aufruf
                    showDialog = false
                    routineToEdit = null
                },
                onSaveRuleAndGet = { ruleToSave -> // Neuer Parameter
                    // Diese Funktion im ViewModel muss die Regel speichern und die
                    // gespeicherte/aktualisierte Regel zurückgeben.
                    // Der Rückgabewert wird vom Dialog intern verwendet.
                    viewModel.saveRule(ruleToSave)
                },
                onDismiss = {
                    showDialog = false
                    routineToEdit = null
                }
            )
        }
    }
}
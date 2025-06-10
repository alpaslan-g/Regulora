package com.example.regulora.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.regulora.viewmodel.RoutineViewModel
import com.example.regulora.ui.components.RoutineCard
import com.example.regulora.ui.components.RoutineDialog
import com.example.regulora.data.model.Routine


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineManagerScreen(viewModel: RoutineViewModel = viewModel()) {
    val routine = viewModel.currentRoutine
    var showDialog by remember { mutableStateOf(false) }
    var editRoutine by remember { mutableStateOf<Routine?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tagesroutine: ${routine.name}") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editRoutine = null
                showDialog = true
            }) { Text("+") }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            routine.routines.forEach {
                RoutineCard(it, onEdit = { r ->
                    editRoutine = r
                    showDialog = true
                })
            }
        }

        if (showDialog) {
            RoutineDialog(
                initialRoutine = editRoutine,
                onSave = {
                    viewModel.addOrUpdateRoutine(it)
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}



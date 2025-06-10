package com.example.regulora.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.regulora.data.model.*

@Composable
fun RoutineCard(routine: Routine, onEdit: (Routine) -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("${routine.startTime}–${routine.endTime}")
                TextButton(onClick = { onEdit(routine) }) {
                    Text("Bearbeiten")
                }
            }
            routine.rules.forEach { rule ->
                val condition = rule.conditionGroup.conditions.first()
                Text("${rule.actuatorName} ${rule.action} wenn ${condition.sensorType} ${condition.comparator.symbol} ${condition.value}")
            }
        }
    }
}


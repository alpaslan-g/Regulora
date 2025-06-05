package com.example.regulora.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun StatsRow(label: String, values: List<Float>) {
    val min = values.minOrNull() ?: 0f
    val max = values.maxOrNull() ?: 0f
    val avg = if (values.isNotEmpty()) values.average() else 0.0

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text("$label:")
        Text("Min: ${min.roundToInt()}   Max: ${max.roundToInt()}   Ø: ${avg.roundToInt()}")
    }
}

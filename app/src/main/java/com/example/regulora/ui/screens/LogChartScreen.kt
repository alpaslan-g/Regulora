package com.example.regulora.ui.screens

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.regulora.data.SensorEntry
import com.example.regulora.ui.components.ChartView
import com.example.regulora.ui.components.StatsRow
import com.github.mikephil.charting.data.*


@Composable
fun LogChartScreen() {
    val context = LocalContext.current

    // Dummy Messdaten
    val entries = remember {
        List(100) { i ->
            SensorEntry(
                timestamp = System.currentTimeMillis() - (100 - i) * 60_000L,
                temperature = 20f + (0..10).random(),
                humidity = 50f + (0..5).random(),
                soil = 30f + (0..15).random()
            )
        }
    }

    val tempValues = entries.mapIndexed { idx, e -> Entry(idx.toFloat(), e.temperature) }
    val humidityValues = entries.mapIndexed { idx, e -> Entry(idx.toFloat(), e.humidity) }
    val soilValues = entries.mapIndexed { idx, e -> Entry(idx.toFloat(), e.soil) }

    Column(Modifier.padding(16.dp)) {

        // Temperatur (°C)
        Text("🌡 Temperatur (°C)", style = MaterialTheme.typography.titleMedium)
        StatsRow("Temperatur", entries.map { it.temperature })
        Spacer(Modifier.height(8.dp))
        ChartView(dataSets = listOf(
            LineDataSet(tempValues, "Temperatur").apply {
                color = Color.RED
                lineWidth = 2f
                valueTextColor = Color.TRANSPARENT
            }
        ))

        Spacer(Modifier.height(24.dp))

        // Luftfeuchte & Bodenfeuchte (%)
        Text("💧 Feuchtigkeitswerte (%)", style = MaterialTheme.typography.titleMedium)
        StatsRow("Luftfeuchtigkeit", entries.map { it.humidity })
        StatsRow("Bodenfeuchte", entries.map { it.soil })
        Spacer(Modifier.height(8.dp))
        ChartView(dataSets = listOf(
            LineDataSet(humidityValues, "Luftfeuchtigkeit").apply {
                color = Color.BLUE
                lineWidth = 2f
                valueTextColor = Color.TRANSPARENT
            },
            LineDataSet(soilValues, "Bodenfeuchte").apply {
                color = Color.GREEN
                lineWidth = 2f
                valueTextColor = Color.TRANSPARENT
            }
        ))
    }
}

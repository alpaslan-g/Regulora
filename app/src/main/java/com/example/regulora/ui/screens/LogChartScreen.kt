package com.example.regulora.ui.screens

import androidx.compose.foundation.layout.fillMaxSize // Optional: um den Chart den ganzen Platz einnehmen zu lassen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier // Optional: für Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*

@Composable
fun LogChartScreen() {
    AndroidView(
        factory = { context ->
            val chart = LineChart(context)
            // Beispielhafte Chart-Konfiguration
            val entries = listOf(
                Entry(0f, 21.0f),
                Entry(1f, 22.4f),
                Entry(2f, 23.7f)
            )
            val dataSet = LineDataSet(entries, "Temperatur").apply {
                // Hier können Sie das DataSet weiter konfigurieren (Farben, Linienstärke etc.)
                // z.B. color = android.graphics.Color.BLUE
                // valueTextColor = android.graphics.Color.BLACK
            }
            chart.data = LineData(dataSet)
            // Hier können Sie den Chart weiter konfigurieren (Achsen, Legende, etc.)
            // chart.description.isEnabled = false
            chart.invalidate() // Chart neu zeichnen lassen
            chart
        },
        modifier = Modifier.fillMaxSize() // Optional: Lässt den Chart den verfügbaren Platz ausfüllen
    )
}
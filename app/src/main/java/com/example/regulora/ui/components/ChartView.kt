package com.example.regulora.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun ChartView(
    modifier: Modifier = Modifier,
    dataSets: List<LineDataSet>
) {
    val context = LocalContext.current

    AndroidView(
        factory = {
            LineChart(context).apply {
                data = LineData(dataSets)
                description.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)
                setScaleEnabled(true)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisRight.isEnabled = false
                legend.form = Legend.LegendForm.LINE
                invalidate()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

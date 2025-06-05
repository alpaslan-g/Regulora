package com.example.regulora.data

data class SensorEntry(
    val timestamp: Long,  // z. B. epoch millis
    val temperature: Float,
    val humidity: Float,
    val soil: Float
)

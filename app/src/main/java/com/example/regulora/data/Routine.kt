package com.example.regulora.data

data class Routine(
    val id: String,
    val name: String,
    val startTime: String,
    val endTime: String,
    val rules: List<ActuatorRule>
)

package com.example.regulora.data

data class ActuatorRule(
    val actuatorId: Int,
    val label: String,
    val conditions: List<Condition>,
    val logic: LogicOperator = LogicOperator.AND
)

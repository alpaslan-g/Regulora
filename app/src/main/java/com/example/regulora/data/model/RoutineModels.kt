package com.example.regulora.data.model

enum class SensorType { TEMPERATURE, HUMIDITY, SOIL }
enum class Comparator { LT, GT, EQ }
enum class LogicalOperator { AND, OR }

data class SensorCondition(
    val sensorType: SensorType,
    val comparator: Comparator,
    val value: Float
)

data class ConditionGroup(
    val conditions: List<SensorCondition> = emptyList(),
    val operator: LogicalOperator = LogicalOperator.AND
)

data class ActuatorRule(
    val actuatorName: String,
    val conditionGroup: ConditionGroup? = null,
    val action: String // "ON" or "OFF"
)

data class TimeRoutine(
    val id: String = java.util.UUID.randomUUID().toString(),
    val startTime: String,
    val endTime: String,
    val actuatorRules: List<ActuatorRule>
)

data class DailyRoutine(
    val name: String,
    val routines: MutableList<TimeRoutine>
)

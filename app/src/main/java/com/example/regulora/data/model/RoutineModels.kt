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
    val conditions: List<SensorCondition>,
    val operator: LogicalOperator = LogicalOperator.AND
)

data class Rule(
    val actuatorName: String,
    val conditionGroup: ConditionGroup,
    val action: String // "ON", "OFF"
)

data class Routine(
    val id: String,
    val startTime: String,
    val endTime: String,
    val rules: List<Rule>
)

data class DailyRoutine(
    val name: String,
    val routines: List<Routine>
)

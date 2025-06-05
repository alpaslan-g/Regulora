package com.example.regulora.data

enum class Operator { GREATER, LESS }
enum class LogicOperator { AND, OR }

data class Condition(
    val sensor: DeviceType,
    val operator: Operator,
    val value: Float
)

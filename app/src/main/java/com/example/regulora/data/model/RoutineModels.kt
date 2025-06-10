package com.example.regulora.data.model

import java.util.UUID

// Enum für den logischen Operator zwischen Bedingungen
enum class LogicalOperator {
    AND, OR
}

// Enum für den Sensor-Vergleichsoperator
enum class Comparator(val symbol: String) {
    GT(">"), LT("<");

    companion object {
        fun fromSymbol(symbol: String): Comparator? {
            return values().find { it.symbol == symbol }
        }
    }
}

// Enum für Sensor-Typen
enum class SensorType {
    TEMPERATURE, HUMIDITY, LIGHT, SOIL_MOISTURE // Beispielhafte Typen
}

// Datenklasse für eine Sensorbedingung
data class SensorCondition(
    val id: String = UUID.randomUUID().toString(),
    val sensorType: SensorType,
    val comparator: Comparator,
    val value: Float // Numerischer Wert für den Vergleich
)

// Datenklasse für eine Bedingungsgruppe
data class ConditionGroup(
    val conditions: List<SensorCondition>,
    val operator: LogicalOperator // Wie die Bedingungen verknüpft sind (AND/OR)
)

// Datenklasse für eine Regel (die jetzt primär die Bedingungen enthält)
data class Rule(
    val id: String = UUID.randomUUID().toString(),
    val name: String, // Ein Name für diese Regel, z.B. "Temperatur über 25°C"
    val conditionGroup: ConditionGroup
)

// Datenklasse für eine Aktor-Aktion innerhalb einer Routine
// ***** ÄNDERUNG HIER *****
data class ActuatorAction(
    val id: String = UUID.randomUUID().toString(), // Eigene ID für die Aktion ist gut
    val actuatorId: String,
    val actionValue: String, // z.B. "ON", "OFF"
    val ruleId: String? = null // ID der Regel, die DIESE AKTION auslöst. Null = keine spezielle Bedingung / immer aktiv im Zeitfenster
)

// Datenklasse für eine Routine
// ***** ÄNDERUNG HIER *****
data class Routine(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    // val ruleId: String, // ***** DIESES FELD WIRD ENTFERNT *****
    val actuatorActions: List<ActuatorAction>, // Liste von Aktor-Aktionen
    val startTime: String, // Format "HH:mm"
    val endTime: String, // Format "HH:mm"
    val enabled: Boolean = true
)

// Datenklasse für eine tägliche Routine, die mehrere Routinen und Regeln enthalten kann
data class DailyRoutine(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "Mein Tag",
    val routines: List<Routine> = emptyList(),
    val rules: List<Rule> = emptyList() // Globale Liste der verfügbaren Regeln (bleibt nützlich)
)

// Datenklasse für vordefinierte Aktoren (zur Auswahl im UI)
data class PreconfiguredActuator(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String // z.B. "Lüfter", "Lampe", "Pumpe"
)
package com.example.regulora.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.regulora.data.model.* // Stellt sicher, dass alle deine Modelle importiert sind
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class RoutineViewModel : ViewModel() {

    var currentRoutineGroup by mutableStateOf(
        DailyRoutine(
            id = "daily_routine_1",
            name = "Blüte",
            routines = emptyList()
        )
    )
        private set

    private val _allRules = MutableStateFlow<List<Rule>>(
        listOf(
            Rule(
                id = "rule_1_vm_default", // Geänderte ID zur Unterscheidung
                name = "VM: Temperatur > 25°C",
                conditionGroup = ConditionGroup(
                    conditions = listOf(
                        SensorCondition(sensorType = SensorType.TEMPERATURE, comparator = Comparator.GT, value = 25f)
                    ),
                    operator = LogicalOperator.AND
                )
            ),
            Rule(
                id = "rule_2_vm_default", // Geänderte ID
                name = "VM: Luftfeuchtigkeit < 40%",
                conditionGroup = ConditionGroup(
                    conditions = listOf(
                        SensorCondition(sensorType = SensorType.HUMIDITY, comparator = Comparator.LT, value = 40f)
                    ),
                    operator = LogicalOperator.AND
                )
            )
        )
    )
    val allRules: StateFlow<List<Rule>> = _allRules.asStateFlow()

    private val _preconfiguredActuators = MutableStateFlow<List<PreconfiguredActuator>>(
        listOf(
            PreconfiguredActuator(id = "actuator_1_vm", name = "VM Lüfter", type = "Lüfter"),
            PreconfiguredActuator(id = "actuator_2_vm", name = "VM Lampe", type = "Lampe")
        )
    )
    val preconfiguredActuators: StateFlow<List<PreconfiguredActuator>> = _preconfiguredActuators.asStateFlow()

    /**
     * Fügt eine neue Regel zur globalen Liste hinzu oder aktualisiert eine bestehende.
     * Gibt die gespeicherte (oder aktualisierte) Regel zurück, damit ihre ID verwendet werden kann.
     */
    fun saveRule(ruleToSave: Rule): Rule {
        var savedRule = ruleToSave
        _allRules.update { currentRules ->
            val existingRuleIndex = currentRules.indexOfFirst { it.id == ruleToSave.id }
            if (existingRuleIndex != -1) {
                // Bestehende Regel aktualisieren
                currentRules.toMutableList().apply { this[existingRuleIndex] = ruleToSave }
            } else {
                // Neue Regel hinzufügen (ggf. ID sicherstellen, falls noch nicht gesetzt)
                if (ruleToSave.id.isBlank() || ruleToSave.id == "new_rule_placeholder_id") { // Überprüfe auf Platzhalter-ID
                    savedRule = ruleToSave.copy(id = UUID.randomUUID().toString())
                }
                currentRules + savedRule
            }
        }
        return savedRule // Gib die Regel mit der korrekten ID zurück
    }


    /**
     * Fügt eine neue Routine hinzu oder aktualisiert eine bestehende Routine
     * innerhalb der currentRoutineGroup.
     * Die `actuatorActions` in `routineToSave` sollten bereits ihre `ruleId`s korrekt gesetzt haben.
     */
    fun saveRoutine(routineToSave: Routine) {
        val routinesList = currentRoutineGroup.routines.toMutableList()
        val existingIndex = routinesList.indexOfFirst { it.id == routineToSave.id }

        val finalRoutine = if (routineToSave.id.isBlank() || routineToSave.id == "new_routine_placeholder_id") {
            routineToSave.copy(id = UUID.randomUUID().toString())
        } else {
            routineToSave
        }

        if (existingIndex != -1) {
            routinesList[existingIndex] = finalRoutine
        } else {
            routinesList.add(finalRoutine)
        }
        currentRoutineGroup = currentRoutineGroup.copy(routines = routinesList.toList())
    }

    /**
     * Setzt den 'enabled'-Status einer spezifischen Routine.
     */
    fun setRoutineEnabledState(routineToToggle: Routine, newEnabledState: Boolean) {
        currentRoutineGroup = currentRoutineGroup.copy(
            routines = currentRoutineGroup.routines.map {
                if (it.id == routineToToggle.id) {
                    it.copy(enabled = newEnabledState)
                } else {
                    it
                }
            }
        )
    }

    /**
     * Entfernt eine Routine aus der currentRoutineGroup.
     */
    fun deleteRoutine(routineToDelete: Routine) {
        currentRoutineGroup = currentRoutineGroup.copy(
            routines = currentRoutineGroup.routines.filterNot { it.id == routineToDelete.id }
        )
    }

    /**
     * Hilfsfunktion, um eine Regel anhand ihrer ID zu finden.
     */
    fun findRuleById(ruleId: String?): Rule? {
        if (ruleId == null) return null
        return _allRules.value.find { it.id == ruleId }
    }
}
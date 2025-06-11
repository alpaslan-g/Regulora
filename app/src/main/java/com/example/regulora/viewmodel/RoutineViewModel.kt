package com.example.regulora.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.regulora.data.model.*

class RoutineViewModel : ViewModel() {

    var routines = mutableListOf<DailyRoutine>(
        DailyRoutine("Blüte", mutableListOf()),
        DailyRoutine("Vegetation", mutableListOf())
    )
        private set

    var selectedDailyRoutineIndex: Int = 0
        private set

    fun selectDailyRoutine(index: Int) {
        selectedDailyRoutineIndex = index
    }

    fun addTimeRoutine(routine: TimeRoutine) {
        routines[selectedDailyRoutineIndex].routines.add(routine)
    }

    fun getSelectedRoutine(): DailyRoutine {
        return routines[selectedDailyRoutineIndex]
    }

    fun getAvailableActuators(): List<String> {
        return listOf("Lüfter", "Heizung", "Bewässerung", "Licht") // TODO: aus Storage laden
    }
}

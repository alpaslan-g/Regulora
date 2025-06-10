package com.example.regulora.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.regulora.data.model.*

class RoutineViewModel : ViewModel() {

    var currentRoutine by mutableStateOf(
        DailyRoutine(
            name = "Blüte",
            routines = emptyList()
        )
    )
        private set

    fun addOrUpdateRoutine(newRoutine: Routine) {
        currentRoutine = currentRoutine.copy(
            routines = currentRoutine.routines
                .filterNot { it.id == newRoutine.id } + newRoutine
        )
    }
}

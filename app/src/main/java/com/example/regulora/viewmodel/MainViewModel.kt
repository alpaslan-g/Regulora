package com.example.regulora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.regulora.data.*
import com.example.regulora.data.model.TimeRoutine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _routine = MutableStateFlow<TimeRoutine?>(null)
    val routine: StateFlow<TimeRoutine?> = _routine

    fun setRoutine(r: TimeRoutine) {
        _routine.value = r
    }
}

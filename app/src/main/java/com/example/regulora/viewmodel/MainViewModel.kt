package com.example.regulora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.regulora.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _routine = MutableStateFlow<Routine?>(null)
    val routine: StateFlow<Routine?> = _routine

    fun setRoutine(r: Routine) {
        _routine.value = r
    }
}

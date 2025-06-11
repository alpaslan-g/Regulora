package com.example.regulora.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.regulora.data.model.TimeRoutine
// Kein zusätzlicher Coroutine-Import hier nötig, da die Funktion selbst 'suspend' wird

val Context.routinePrefs by preferencesDataStore("routine_store")

class RoutineStore(private val context: Context) {
    private val gson = Gson()

    // ÄNDERUNG HIER: 'suspend' zur Funktion hinzufügen
    suspend fun save(routine: TimeRoutine) {
        val key = stringPreferencesKey("routine_${routine.id}")
        val json = gson.toJson(routine)
        // Dieser Aufruf ist jetzt gültig, da 'save' eine suspend-Funktion ist
        context.routinePrefs.edit { preferences -> // 'it' in 'preferences' umbenannt für Klarheit
            preferences[key] = json
        }
    }

    fun getAll(): Flow<List<TimeRoutine>> = context.routinePrefs.data.map { prefs ->
        prefs.asMap().values.mapNotNull { value -> // 'it' in 'value' umbenannt für Klarheit
            try { // Es ist gut, die JSON-Konvertierung in einen try-catch-Block zu packen
                gson.fromJson(value.toString(), TimeRoutine::class.java)
            } catch (e: Exception) {
                // Loggen Sie den Fehler oder behandeln Sie ihn entsprechend
                // z.B. Log.e("RoutineStore", "Fehler beim Parsen der Routine: $value", e)
                null // Gibt null zurück, wenn das Parsen fehlschlägt, was mapNotNull dann entfernt
            }
        }
    }
}
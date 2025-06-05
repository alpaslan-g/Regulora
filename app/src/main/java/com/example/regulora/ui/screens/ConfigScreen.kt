package com.example.regulora.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ConfigScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Controller konfigurieren", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // TODO: OTA Upload starten
        }) {
            Text("OTA-Update einspielen")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // TODO: Aktuatoren zu Steckdosen zuweisen
        }) {
            Text("Aktuatoren konfigurieren")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // TODO: Verbindung trennen
        }) {
            Text("Verbindung trennen")
        }
    }
}

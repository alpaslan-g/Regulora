package com.example.regulora.ui.theme // Passen Sie den Paketnamen an!

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Helles Farbschema basierend auf Ihren Farben in Color.kt
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    // Sie können hier weitere Standardfarben überschreiben:
    // background = Color.White,
    // surface = Color(0xFFFFFBFE),
    // onPrimary = Color.White,
    // onSecondary = Color.White,
    // onTertiary = Color.White,
    // onBackground = Color(0xFF1C1B1F),
    // onSurface = Color(0xFF1C1B1F),
)

// Dunkles Farbschema basierend auf Ihren Farben in Color.kt
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    // Sie können hier weitere Standardfarben überschreiben:
    // background = Color(0xFF1C1B1F),
    // surface = Color(0xFF1C1B1F),
    // onPrimary = PurpleGrey80, // Beispiel
    // onSecondary = PurpleGrey40, // Beispiel
    // onTertiary = Pink40, // Beispiel
    // onBackground = Color(0xFFFFFBFE),
    // onSurface = Color(0xFFFFFBFE),
)

@Composable
fun ReguloraTheme( // Benennen Sie diese Funktion entsprechend Ihrem App-Namen
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamische Farben sind ab Android 12+ verfügbar
    dynamicColor: Boolean = true, // Setzen Sie dies auf false, wenn Sie keine dynamischen Farben möchten
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Optional: Ändern der Systemleistenfarben (Statusleiste, Navigationsleiste)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Statusleistenfarbe (Sie können dies anpassen, z.B. colorScheme.surface)
            window.statusBarColor = colorScheme.primary.toArgb() // Beispiel
            // Navigationsleistenfarbe (falls sichtbar und gewünscht)
            // window.navigationBarColor = colorScheme.surface.toArgb() // Beispiel

            // Icons der Statusleiste (hell/dunkel)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            // Icons der Navigationsleiste (hell/dunkel, falls gewünscht)
            // WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Aus Ihrer Type.kt
        shapes = Shapes,         // Aus Ihrer Shape.kt (optional, aber gut für Konsistenz)
        content = content
    )
}
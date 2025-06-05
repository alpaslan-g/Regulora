package com.example.regulora.ui.theme // Passen Sie den Paketnamen an!

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Ersetzen Sie dies durch Ihre bevorzugten Schriftarten, falls Sie benutzerdefinierte verwenden.
// Standardmäßig wird die Systemschriftart verwendet, wenn keine FontFamily angegeben ist.

// Beispielhafte Typography Definition für Material 3
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    /* Andere Textstile nach Bedarf definieren:
    displayLarge, displayMedium, displaySmall,
    headlineLarge, headlineMedium,
    titleMedium, titleSmall,
    bodyMedium, bodySmall,
    labelLarge, labelMedium,
    */
)
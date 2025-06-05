package com.example.regulora.ui.theme // Passen Sie den Paketnamen an!

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp), // War vorher 4.dp in M2, oft auf 8.dp oder 12.dp in M3 angepasst
    large = RoundedCornerShape(16.dp)  // War vorher 0.dp oder 8.dp in M2
)
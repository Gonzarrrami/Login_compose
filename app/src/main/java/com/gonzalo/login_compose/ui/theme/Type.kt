package com.gonzalo.login_compose.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Tipografía de la app: la escala por defecto de Material 3 (fuente Roboto),
// con un único cambio: el título "LOGIN" muy grueso, como en el diseño de Figma.
val Typography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    ),
)

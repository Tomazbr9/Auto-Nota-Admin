package com.tomazbr9.autonotaadmin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Paleta fixa para preservar a identidade do Auto-Nota Admin.
// Nao utiliza cores dinamicas do papel de parede do Android.
private val AutoNotaColors = darkColorScheme(
    primary = TechPrimary,
    onPrimary = TechBackground,
    secondary = TechSecondary,
    onSecondary = TechBackground,
    background = TechBackground,
    onBackground = TechText,
    surface = TechSurface,
    onSurface = TechText,
    surfaceVariant = TechSurfaceElevated,
    onSurfaceVariant = TechMuted,
    outline = TechOutline,
    tertiary = TechSuccess,
    error = TechError
)

@Composable
fun AutoNotaAdminTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AutoNotaColors,
        typography = Typography,
        content = content
    )
}

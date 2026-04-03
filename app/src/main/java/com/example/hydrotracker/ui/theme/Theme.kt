package com.example.hydrotracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HydroColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = BlueSecondary,
    background = LightBackground,
    surface = WhiteSurface,
    onPrimary = OnPrimaryWhite,
    error = ErrorRed,
    onSurfaceVariant = BlueDark
)

@Composable
fun HydroTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HydroColorScheme,
        typography = AppTypography,
        content = content
    )
}
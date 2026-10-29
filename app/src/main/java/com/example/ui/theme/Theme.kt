package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PersonaLightColorScheme = lightColorScheme(
    primary = PersonaGreen,
    onPrimary = Color.White,
    primaryContainer = PersonaLightLime,
    onPrimaryContainer = PersonaDarkOlive,

    secondary = PersonaCoralPink,
    onSecondary = Color.White,
    secondaryContainer = PersonaBlushPink,
    onSecondaryContainer = PersonaDarkOlive,

    tertiary = PersonaButterCream,
    onTertiary = PersonaDarkOlive,
    tertiaryContainer = PersonaCreamSurface,
    onTertiaryContainer = PersonaDarkOlive,

    background = PersonaWarmOffWhite,
    onBackground = PersonaCharcoal,
    surface = PersonaWhite,
    onSurface = PersonaCharcoal,
    surfaceVariant = PersonaSurfaceVariant,
    onSurfaceVariant = PersonaMutedGray,
    outline = PersonaBorder
)

private val PersonaDarkColorScheme = darkColorScheme(
    primary = PersonaLightLime,
    onPrimary = PersonaDarkOlive,
    primaryContainer = PersonaGreen,
    onPrimaryContainer = Color.White,

    secondary = PersonaCoralPink,
    onSecondary = PersonaCharcoal,
    secondaryContainer = PersonaBlushPink,
    onSecondaryContainer = PersonaCharcoal,

    tertiary = PersonaButterCream,
    onTertiary = PersonaCharcoal,

    background = Color(0xFF1E211B),
    onBackground = Color(0xFFF1F1EB),
    surface = Color(0xFF272A23),
    onSurface = Color(0xFFF1F1EB),
    surfaceVariant = Color(0xFF33382D),
    onSurfaceVariant = Color(0xFFBCC2B3),
    outline = Color(0xFF454B3E)
)

@Composable
fun PersonaAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PersonaDarkColorScheme else PersonaLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

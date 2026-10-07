package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PixoraDarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = Charcoal900,
    primaryContainer = Charcoal700,
    onPrimaryContainer = GoldLight,
    secondary = GoldLight,
    onSecondary = Charcoal900,
    secondaryContainer = Charcoal700,
    onSecondaryContainer = WarmWhite,
    tertiary = WarmOffWhite,
    onTertiary = Charcoal900,
    background = Charcoal900,
    onBackground = WarmWhite,
    surface = Charcoal800,
    onSurface = WarmWhite,
    surfaceVariant = Charcoal700,
    onSurfaceVariant = Charcoal200,
    outline = Charcoal600,
    outlineVariant = Charcoal700,
    error = LuxuryRed,
    onError = Color.White
)

val PixoraLightColorScheme = lightColorScheme(
    primary = Charcoal900,
    onPrimary = WarmWhite,
    primaryContainer = Charcoal100,
    onPrimaryContainer = Charcoal900,
    secondary = GoldDark,
    onSecondary = WarmWhite,
    secondaryContainer = GoldSubtle,
    onSecondaryContainer = Charcoal900,
    tertiary = GoldAccent,
    onTertiary = Charcoal900,
    background = WarmWhite,
    onBackground = Charcoal900,
    surface = WarmSurfaceLight,
    onSurface = Charcoal900,
    surfaceVariant = WarmOffWhite,
    onSurfaceVariant = Charcoal600,
    outline = Charcoal200,
    outlineVariant = Charcoal100,
    error = LuxuryRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PixoraDarkColorScheme else PixoraLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PixoraTypography,
        content = content
    )
}

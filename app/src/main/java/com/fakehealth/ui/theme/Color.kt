package com.fakehealth.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Samsung Health inspired colors
// Primary: Samsung Blue #0066FF / #4D94FF
// Secondary: Health Green #00C853 / #69F0AE
// Background: Dark #121212 / Light #FAFAFA

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0066FF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    onPrimaryContainer = Color(0xFF001D4A),
    secondary = Color(0xFF006E1C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFA7F3A0),
    onSecondaryContainer = Color(0xFF002104),
    tertiary = Color(0xFF7C5200),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDC8E),
    onTertiaryContainer = Color(0xFF261800),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFAFAFA),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E2EC),
    onSurfaceVariant = Color(0xFF4A444E),
    outline = Color(0xFF7A757F),
    outlineVariant = Color(0xFFCAC4CF),
    shadow = Color(0xFF000000),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFFA0C4FF),
    surfaceTint = Color(0xFF0066FF)
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8AB4F8),
    onPrimary = Color(0xFF003380),
    primaryContainer = Color(0xFF004DB5),
    onPrimaryContainer = Color(0xFFD6E4FF),
    secondary = Color(0xFF8FD68A),
    onSecondary = Color(0xFF00380D),
    secondaryContainer = Color(0xFF005114),
    onSecondaryContainer = Color(0xFFA7F3A0),
    tertiary = Color(0xFFFFB872),
    onTertiary = Color(0xFF3F2A00),
    tertiaryContainer = Color(0xFF5C3F00),
    onTertiaryContainer = Color(0xFFFFDC8E),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF4A444E),
    onSurfaceVariant = Color(0xFFCAC4CF),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF4A444E),
    shadow = Color(0xFF000000),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = Color(0xFF0066FF),
    surfaceTint = Color(0xFF8AB4F8)
)
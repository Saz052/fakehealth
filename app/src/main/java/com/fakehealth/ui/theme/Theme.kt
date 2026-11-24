package com.fakehealth.ui.theme

import android.app.Activity
import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.setSystemBarsColor

@Composable
fun FakeHealthTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    val context = LocalContext.current
    SideEffect {
        (context as Activity).window.setStatusBarColor(
            colorScheme.background.toArgb()
        )
        (context as Activity).window.setNavigationBarColor(
            colorScheme.background.toArgb()
        )
        WindowCompat.setDecorFitsSystemWindows(view, false)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
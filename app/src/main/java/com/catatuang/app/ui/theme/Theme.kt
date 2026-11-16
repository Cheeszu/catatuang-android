package com.catatuang.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val HijauPemasukan = Color(0xFF1B8A5A)
val MerahPengeluaran = Color(0xFFD32F2F)

private val Terang = lightColorScheme(
    primary = Color(0xFF1B7F5B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA6F2CF),
    onPrimaryContainer = Color(0xFF002114),
    secondary = Color(0xFF4D6357),
    secondaryContainer = Color(0xFFCFE9DA),
    onSecondaryContainer = Color(0xFF0A1F15),
    surface = Color(0xFFF8FAF7),
    background = Color(0xFFF8FAF7),
    surfaceVariant = Color(0xFFDCE5DD),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF2F5F1),
    surfaceContainer = Color(0xFFECF0EB),
    surfaceContainerHigh = Color(0xFFE6EBE5),
    surfaceContainerHighest = Color(0xFFE0E6E0),
)

private val Gelap = darkColorScheme(
    primary = Color(0xFF8BD6B4),
    onPrimary = Color(0xFF003824),
    primaryContainer = Color(0xFF005236),
    onPrimaryContainer = Color(0xFFA6F2CF),
    secondary = Color(0xFFB4CCBE),
)

@Composable
fun CatatUangTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) Gelap else Terang,
        content = content,
    )
}

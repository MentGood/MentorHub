package com.example.aplicacionesmoviles.mentorhub


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Azul = Color(0xFF1E56B3)
private val AzulClaro = Color(0xFFE6EEFB)
private val Blanco = Color(0xFFFFFFFF)
private val FondoGris = Color(0xFFF6F8FB)
private val TextoOscuro = Color(0xFF1F2937)
private val TextoGris = Color(0xFF6B7280)
private val BordeGris = Color(0xFFE5E7EB)
private val Ambar = Color(0xFFF5A623)
private val Rojo = Color(0xFFD32F2F)

private val ColoresMentorHub = lightColorScheme(
    primary = Azul,
    onPrimary = Blanco,
    primaryContainer = AzulClaro,
    onPrimaryContainer = Azul,
    secondaryContainer = AzulClaro,
    onSecondaryContainer = Azul,
    tertiary = Ambar,
    onTertiary = Blanco,
    background = FondoGris,
    onBackground = TextoOscuro,
    surface = Blanco,
    onSurface = TextoOscuro,
    surfaceVariant = FondoGris,
    onSurfaceVariant = TextoGris,
    surfaceContainer = Blanco,
    surfaceContainerLow = Blanco,
    surfaceContainerHigh = Blanco,
    surfaceContainerHighest = FondoGris,
    outline = TextoGris,
    outlineVariant = BordeGris,
    error = Rojo
)

@Composable
fun MentorHubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColoresMentorHub,
        content = content
    )
}
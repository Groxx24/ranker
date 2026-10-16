package com.ranker.countries.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB547),
    onPrimary = Color(0xFF10131A),
    secondary = Color(0xFF3DDCFF),
    onSecondary = Color(0xFF10131A),
    tertiary = Color(0xFFFF6FB1),
    onTertiary = Color(0xFF10131A),
    background = Color(0xFF0B0D17),
    onBackground = Color(0xFFF2F4FF),
    surface = Color(0xFF151826),
    onSurface = Color(0xFFF2F4FF),
    surfaceVariant = Color(0xFF1E2235),
    onSurfaceVariant = Color(0xFF9AA0B8),
    surfaceContainerLowest = Color(0xFF080A12),
    surfaceContainerLow = Color(0xFF11131F),
    surfaceContainer = Color(0xFF151826),
    surfaceContainerHigh = Color(0xFF1B1F30),
    surfaceContainerHighest = Color(0xFF22263A),
    outline = Color(0xFF3A4060),
    outlineVariant = Color(0xFF2A2F45),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF10131A),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFFE08A00),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF0088C2),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFD6336C),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF6F7FB),
    onBackground = Color(0xFF12141F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF12141F),
    surfaceVariant = Color(0xFFECEEF6),
    onSurfaceVariant = Color(0xFF5D6378),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF9FAFD),
    surfaceContainer = Color(0xFFF2F3F9),
    surfaceContainerHigh = Color(0xFFECEEF6),
    surfaceContainerHighest = Color(0xFFE5E8F2),
    outline = Color(0xFFC3C7D6),
    outlineVariant = Color(0xFFD6D9E6),
    error = Color(0xFFD63031),
    onError = Color(0xFFFFFFFF),
)

private val RankerTypography = Typography().run {
    copy(
        headlineMedium = headlineMedium.copy(fontWeight = FontWeight.Black),
        headlineSmall = headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
        titleLarge = titleLarge.copy(fontWeight = FontWeight.Bold),
        titleMedium = titleMedium.copy(fontWeight = FontWeight.SemiBold),
        labelLarge = labelLarge.copy(fontWeight = FontWeight.Bold),
    )
}

/** Light and dark schemes following the phone's setting, with every role set in both. */
@Composable
fun RankerTheme(
    dark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = RankerTypography,
        content = content,
    )
}

/**
 * Each tab's own two-colour gradient. Bright enough to read [OnAccent] on and to stand out on both
 * backgrounds, so they are the same in light and dark.
 */
data class Accent(val start: Color, val end: Color)

private val Accents = listOf(
    Accent(Color(0xFFFFC857), Color(0xFFFF7A45)), // amber
    Accent(Color(0xFF4BE1FF), Color(0xFF3D7BFF)), // cyan
    Accent(Color(0xFFC08BFF), Color(0xFF7B5CFF)), // violet
    Accent(Color(0xFF5AF0B4), Color(0xFF14B88A)), // mint
    Accent(Color(0xFFFF8AC4), Color(0xFFFF3D6E)), // pink
)

fun accentFor(index: Int): Accent = Accents[index.mod(Accents.size)]

fun lerp(from: Accent, to: Accent, fraction: Float) =
    Accent(lerp(from.start, to.start, fraction), lerp(from.end, to.end, fraction))

/** Text and icons drawn on an accent gradient. */
val OnAccent = Color(0xFF10131A)

/** Rank badges of the podium; the same in both themes. */
val Gold = Color(0xFFFFD54A)
val Silver = Color(0xFFCFD8E3)
val Bronze = Color(0xFFE8A06A)

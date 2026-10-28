package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val AmberColorScheme = lightColorScheme(
    primary = CandyPink,
    onPrimary = Color.White,
    primaryContainer = CandyPinkLight,
    onPrimaryContainer = Color(0xFF4A0028),
    secondary = SkyBlue,
    onSecondary = Color.White,
    secondaryContainer = SkyBlueLight,
    onSecondaryContainer = Color(0xFF003650),
    tertiary = SunnyYellow,
    onTertiary = Color(0xFF3E2723),
    tertiaryContainer = SunnyYellowLight,
    onTertiaryContainer = Color(0xFF4A3400),
    background = SoftPinkBg,
    onBackground = TextDark,
    surface = Color.White,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFFFFAFD),
    onSurfaceVariant = TextSubtitle
)

val AmberShapes = Shapes(
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp),
    extraLarge = RoundedCornerShape(40.dp)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AmberColorScheme,
        shapes = AmberShapes,
        typography = Typography,
        content = content
    )
}

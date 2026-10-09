package com.example.room_database_discussion.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DirectoryColors = lightColorScheme(
    primary = Charcoal,
    onPrimary = Color.White,
    primaryContainer = PaleSage,
    onPrimaryContainer = Charcoal,
    secondary = Sage,
    onSecondary = Color.White,
    background = Paper,
    onBackground = Charcoal,
    surface = SurfaceWhite,
    onSurface = Charcoal,
    surfaceVariant = PaleSage,
    onSurfaceVariant = MutedInk,
    outline = SoftOutline,
    error = Color(0xFFB44848)
)

@Composable
fun Room_database_discussionTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DirectoryColors,
        typography = Typography,
        content = content
    )
}

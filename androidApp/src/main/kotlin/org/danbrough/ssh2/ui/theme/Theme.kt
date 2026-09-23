package org.danbrough.ssh2.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = Moonlight_Primary,
  secondary = Moonlight_Secondary,
  tertiary = Moonlight_Tertiary,
  background = Moonlight_Background,
  surface = Moonlight_Surface,
  onPrimary = Moonlight_OnPrimary,
  onBackground = Moonlight_OnBackground,
  onSurface = Moonlight_OnSurface,
  error = Moonlight_Error
)

private val LightColorScheme = lightColorScheme(
  primary = Moonlight_Primary,
  secondary = Moonlight_Secondary,
  tertiary = Moonlight_Tertiary,
  background = Color(0xFFF2F4F8), // Soft moonlit light grey
  surface = Color.White,
  onPrimary = Color.White,
  onBackground = Moonlight_Background,
  onSurface = Moonlight_Background
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is disabled by default to enforce Android Studio style colors
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }

    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
package com.vitalis.eps.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.vitalis.eps.resources.Res
import com.vitalis.eps.resources.plusjakartasans_400
import com.vitalis.eps.resources.plusjakartasans_500
import com.vitalis.eps.resources.plusjakartasans_600
import com.vitalis.eps.resources.plusjakartasans_700
import com.vitalis.eps.resources.sora_600
import com.vitalis.eps.resources.sora_700
import com.vitalis.eps.resources.sora_800
import org.jetbrains.compose.resources.Font

/** Paleta de la marca: azul noche de fondo, teal eléctrico como acento principal y violeta de apoyo. */
object Vitalis {
    val Noche = Color(0xFF0B1B3A)
    val Indigo = Color(0xFF1E2A78)
    val Violeta = Color(0xFF6C5CE7)
    val VioletaClaro = Color(0xFFECE9FF)
    val Teal = Color(0xFF12D6B0)
    val TealOscuro = Color(0xFF0A8F79)
    val TealClaro = Color(0xFFDDF8F2)
    val Coral = Color(0xFFF2545B)
    val CoralClaro = Color(0xFFFDE7E8)
    val Ambar = Color(0xFFF5A524)
    val AmbarClaro = Color(0xFFFEF3DC)
    val Fondo = Color(0xFFF3F6FB)
    val Tinta = Color(0xFF0F172A)
    val TintaSuave = Color(0xFF5B6478)
    val Linea = Color(0xFFE3E8F1)
}

private val coloresClaros = lightColorScheme(
    primary = Vitalis.Noche,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE6ECF8),
    onPrimaryContainer = Vitalis.Noche,
    secondary = Vitalis.Teal,
    onSecondary = Vitalis.Noche,
    secondaryContainer = Vitalis.TealClaro,
    onSecondaryContainer = Color(0xFF064E42),
    tertiary = Vitalis.Violeta,
    onTertiary = Color.White,
    tertiaryContainer = Vitalis.VioletaClaro,
    onTertiaryContainer = Color(0xFF2B2170),
    error = Vitalis.Coral,
    background = Vitalis.Fondo,
    onBackground = Vitalis.Tinta,
    surface = Color.White,
    onSurface = Vitalis.Tinta,
    surfaceVariant = Color(0xFFEAEFF7),
    onSurfaceVariant = Vitalis.TintaSuave,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF8FAFD),
    surfaceContainer = Color(0xFFF0F4FA),
    surfaceContainerHigh = Color(0xFFEAEFF7),
    outline = Color(0xFFB7C0D1),
    outlineVariant = Vitalis.Linea,
)

/** Modo oscuro: fondos azul noche profundo, tarjetas un tono más claras y el teal como color principal. */
private val coloresOscuros = darkColorScheme(
    primary = Vitalis.Teal,
    onPrimary = Vitalis.Noche,
    primaryContainer = Color(0xFF1B2744),
    onPrimaryContainer = Color(0xFFE6ECF8),
    secondary = Vitalis.Teal,
    onSecondary = Vitalis.Noche,
    secondaryContainer = Color(0xFF0E3A36),
    onSecondaryContainer = Color(0xFFB5F5E8),
    tertiary = Color(0xFFA99DFF),
    onTertiary = Color(0xFF1B1446),
    tertiaryContainer = Color(0xFF2A2360),
    onTertiaryContainer = Color(0xFFE2DDFF),
    error = Color(0xFFFF7A80),
    background = Color(0xFF070D1C),
    onBackground = Color(0xFFE8EDF7),
    surface = Color(0xFF0F172B),
    onSurface = Color(0xFFE8EDF7),
    surfaceVariant = Color(0xFF1A2440),
    onSurfaceVariant = Color(0xFF9AA6BD),
    surfaceContainerLowest = Color(0xFF111A30),
    surfaceContainerLow = Color(0xFF131D34),
    surfaceContainer = Color(0xFF17213A),
    surfaceContainerHigh = Color(0xFF1C2742),
    outline = Color(0xFF5A6782),
    outlineVariant = Color(0xFF223050),
)

/** Verdadero cuando el tema activo es oscuro. */
val esOscuro: Boolean
    @Composable @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.background.luminance() < 0.5f

/** Color de las tarjetas: blanco en modo claro, azul noche elevado en modo oscuro. */
val colorTarjeta: Color
    @Composable @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceContainerLowest

@Composable
private fun familiaTitulos() = FontFamily(
    Font(Res.font.sora_600, FontWeight.SemiBold),
    Font(Res.font.sora_700, FontWeight.Bold),
    Font(Res.font.sora_800, FontWeight.ExtraBold),
)

@Composable
private fun familiaTexto() = FontFamily(
    Font(Res.font.plusjakartasans_400, FontWeight.Normal),
    Font(Res.font.plusjakartasans_500, FontWeight.Medium),
    Font(Res.font.plusjakartasans_600, FontWeight.SemiBold),
    Font(Res.font.plusjakartasans_700, FontWeight.Bold),
)

/**
 * Tipografía: títulos grandes con interletrado negativo e interlineado ajustado;
 * texto pequeño con interletrado levemente positivo para que se lea mejor.
 */
@Composable
private fun tipografia(): Typography {
    val titulos = familiaTitulos()
    val texto = familiaTexto()
    return Typography(
        displayMedium = TextStyle(fontFamily = titulos, fontWeight = FontWeight.ExtraBold, fontSize = 44.sp, lineHeight = 46.sp, letterSpacing = (-0.035).em),
        displaySmall = TextStyle(fontFamily = titulos, fontWeight = FontWeight.ExtraBold, fontSize = 34.sp, lineHeight = 38.sp, letterSpacing = (-0.03).em),
        headlineMedium = TextStyle(fontFamily = titulos, fontWeight = FontWeight.Bold, fontSize = 27.sp, lineHeight = 32.sp, letterSpacing = (-0.025).em),
        headlineSmall = TextStyle(fontFamily = titulos, fontWeight = FontWeight.Bold, fontSize = 21.sp, lineHeight = 27.sp, letterSpacing = (-0.02).em),
        titleLarge = TextStyle(fontFamily = titulos, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = (-0.01).em),
        titleMedium = TextStyle(fontFamily = texto, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 22.sp),
        titleSmall = TextStyle(fontFamily = texto, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
        bodyLarge = TextStyle(fontFamily = texto, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 25.sp),
        bodyMedium = TextStyle(fontFamily = texto, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp),
        bodySmall = TextStyle(fontFamily = texto, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp, letterSpacing = 0.01.em),
        labelLarge = TextStyle(fontFamily = texto, fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 20.sp),
        labelMedium = TextStyle(fontFamily = texto, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.02.em),
        labelSmall = TextStyle(fontFamily = texto, fontWeight = FontWeight.Bold, fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.09.em),
    )
}

private val formas = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun VitalisTheme(oscuro: Boolean, content: @Composable () -> Unit) {
    val esquema = if (oscuro) coloresOscuros else coloresClaros
    MaterialTheme(colorScheme = esquema, typography = tipografia(), shapes = formas) {
        // Color de texto por defecto: sin esto, el texto sin color explícito sale negro también en modo oscuro.
        CompositionLocalProvider(LocalContentColor provides esquema.onBackground, content = content)
    }
}

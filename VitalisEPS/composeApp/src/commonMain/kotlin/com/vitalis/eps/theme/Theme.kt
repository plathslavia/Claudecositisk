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
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
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
import com.vitalis.eps.resources.fraunces_600
import com.vitalis.eps.resources.fraunces_700
import com.vitalis.eps.resources.spacegrotesk_600
import com.vitalis.eps.resources.spacegrotesk_700
import org.jetbrains.compose.resources.Font

private fun esquemaClaro(p: Paleta) = lightColorScheme(
    primary = p.Base,
    onPrimary = Color.White,
    primaryContainer = p.Base.copy(alpha = 0.09f).compositeOver(Color.White),
    onPrimaryContainer = p.Base,
    secondary = p.Acento,
    onSecondary = p.SobreAcento,
    secondaryContainer = p.AcentoClaro,
    onSecondaryContainer = p.AcentoOscuro,
    tertiary = p.Acento2,
    onTertiary = Color.White,
    tertiaryContainer = p.Acento2Claro,
    onTertiaryContainer = p.Base2,
    error = p.Peligro,
    background = p.Fondo,
    onBackground = p.Tinta,
    surface = Color.White,
    onSurface = p.Tinta,
    surfaceVariant = p.Linea.copy(alpha = 0.6f).compositeOver(p.Fondo),
    onSurfaceVariant = p.TintaSuave,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = p.Fondo.copy(alpha = 0.5f).compositeOver(Color.White),
    surfaceContainer = p.Fondo,
    surfaceContainerHigh = p.Linea.copy(alpha = 0.6f).compositeOver(p.Fondo),
    outline = p.TintaSuave.copy(alpha = 0.45f).compositeOver(Color.White),
    outlineVariant = p.Linea,
)

/** Modo oscuro: el fondo oscuro del estilo, tarjetas un tono más claras y el acento como color principal. */
private fun esquemaOscuro(p: Paleta) = darkColorScheme(
    primary = p.Acento,
    onPrimary = p.SobreAcento,
    primaryContainer = p.TarjetaOscura,
    onPrimaryContainer = Color(0xFFE8EDF7),
    secondary = p.Acento,
    onSecondary = p.SobreAcento,
    secondaryContainer = p.Acento.copy(alpha = 0.2f).compositeOver(p.FondoOscuro),
    onSecondaryContainer = p.AcentoClaro,
    tertiary = p.Acento2,
    onTertiary = Color.White,
    tertiaryContainer = p.Acento2.copy(alpha = 0.25f).compositeOver(p.FondoOscuro),
    onTertiaryContainer = p.Acento2Claro,
    error = p.Peligro,
    background = p.FondoOscuro,
    onBackground = Color(0xFFECEFF5),
    surface = p.TarjetaOscura,
    onSurface = Color(0xFFECEFF5),
    surfaceVariant = p.LineaOscura.copy(alpha = 0.7f).compositeOver(p.TarjetaOscura),
    onSurfaceVariant = Color(0xFFA0A8BA),
    surfaceContainerLowest = p.TarjetaOscura,
    surfaceContainerLow = p.TarjetaOscura,
    surfaceContainer = p.LineaOscura.copy(alpha = 0.4f).compositeOver(p.TarjetaOscura),
    surfaceContainerHigh = p.LineaOscura.copy(alpha = 0.7f).compositeOver(p.TarjetaOscura),
    outline = Color(0xFF5E6678),
    outlineVariant = p.LineaOscura,
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
private fun familiaTitulos(fuente: FuenteTitulos) = when (fuente) {
    FuenteTitulos.Sora -> FontFamily(
        Font(Res.font.sora_600, FontWeight.SemiBold),
        Font(Res.font.sora_700, FontWeight.Bold),
        Font(Res.font.sora_800, FontWeight.ExtraBold),
    )
    FuenteTitulos.PlusJakarta -> FontFamily(
        Font(Res.font.plusjakartasans_600, FontWeight.SemiBold),
        Font(Res.font.plusjakartasans_700, FontWeight.Bold),
        Font(Res.font.plusjakartasans_700, FontWeight.ExtraBold),
    )
    FuenteTitulos.Fraunces -> FontFamily(
        Font(Res.font.fraunces_600, FontWeight.SemiBold),
        Font(Res.font.fraunces_700, FontWeight.Bold),
        Font(Res.font.fraunces_700, FontWeight.ExtraBold),
    )
    FuenteTitulos.SpaceGrotesk -> FontFamily(
        Font(Res.font.spacegrotesk_600, FontWeight.SemiBold),
        Font(Res.font.spacegrotesk_700, FontWeight.Bold),
        Font(Res.font.spacegrotesk_700, FontWeight.ExtraBold),
    )
}

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
private fun tipografia(fuente: FuenteTitulos): Typography {
    val titulos = familiaTitulos(fuente)
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
fun VitalisTheme(oscuro: Boolean, estilo: Estilo = Estilo.Pulso, content: @Composable () -> Unit) {
    val esquema = remember(estilo, oscuro) { if (oscuro) esquemaOscuro(estilo.paleta) else esquemaClaro(estilo.paleta) }
    MaterialTheme(colorScheme = esquema, typography = tipografia(estilo.titulos), shapes = formas) {
        // Color de texto por defecto: sin esto, el texto sin color explícito sale negro también en modo oscuro.
        CompositionLocalProvider(
            LocalContentColor provides esquema.onBackground,
            LocalEstilo provides estilo,
            content = content,
        )
    }
}

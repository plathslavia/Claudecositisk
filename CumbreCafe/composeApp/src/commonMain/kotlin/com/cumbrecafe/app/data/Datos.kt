package com.cumbrecafe.app.data

import androidx.compose.ui.graphics.Color

/** Información institucional que se muestra en la pantalla de entrada (empresa ficticia). */
object Empresa {
    const val NOMBRE = "Cumbre Café Co."
    const val ESLOGAN = "Café de especialidad desde las montañas de Colombia"
    const val QUIENES_SOMOS =
        "Somos una tostadora de café de especialidad fundada en Bogotá en 2016. " +
            "Compramos directamente a familias caficultoras de ocho regiones del país, " +
            "pagamos por encima del precio de mercado y tostamos cada lote en pequeñas " +
            "cantidades para que en la taza se note de dónde viene."
    const val MISION =
        "Llevar a cada taza el trabajo de las familias caficultoras colombianas, " +
            "con comercio directo, trazabilidad completa y un tostado que respeta el origen."
    const val VISION =
        "Ser en 2030 la marca de café de especialidad colombiana más reconocida por su " +
            "transparencia, con presencia en toda Latinoamérica."

    val cifras = listOf(
        Cifra("2016", "Año de fundación"),
        Cifra("42", "Fincas aliadas"),
        Cifra("8", "Regiones de origen"),
    )

    val servicios = listOf(
        Servicio("Café de origen", "Grano entero o molido, en bolsas de 340 g con válvula."),
        Servicio("Suscripción mensual", "Un origen distinto cada mes, directo a tu puerta."),
        Servicio("Escuela de baristas", "Cursos de métodos filtrados, espresso y cata."),
        Servicio("Venta a cafeterías", "Perfiles de tostado a la medida para negocios."),
    )

    val contacto = listOf(
        DatoContacto(TipoContacto.Direccion, "Calle 70 # 10-22, Chapinero, Bogotá"),
        DatoContacto(TipoContacto.Telefono, "+57 601 555 0142"),
        DatoContacto(TipoContacto.Correo, "hola@cumbrecafe.co"),
        DatoContacto(TipoContacto.Horario, "Lun a sáb · 7:00 a. m. – 7:00 p. m."),
    )

    const val NIT = "NIT 901.456.789-0"

    val equipo = listOf("Andrés García", "Camilo Casallas", "Cristóbal Moncada")
}

data class Cifra(val valor: String, val etiqueta: String)
data class Servicio(val titulo: String, val descripcion: String)
data class DatoContacto(val tipo: TipoContacto, val valor: String)
enum class TipoContacto { Direccion, Telefono, Correo, Horario }

enum class Proceso(val etiqueta: String) {
    Lavado("Lavado"),
    Honey("Honey"),
    Natural("Natural"),
}

/** Elemento del catálogo: la vista Maestro los lista y la vista Detalle muestra uno. */
data class Cafe(
    val id: String,
    val nombre: String,
    val region: String,
    val finca: String,
    val productor: String,
    val altitud: Int,
    val variedad: String,
    val proceso: Proceso,
    /** 0 = tueste muy claro, 1 = tueste oscuro. */
    val tueste: Float,
    val notas: List<String>,
    val puntaje: Double,
    val precio: Int,
    val descripcion: String,
    val metodos: List<String>,
    val colorA: Color,
    val colorB: Color,
)

object Catalogo {
    val cafes = listOf(
        Cafe(
            id = "huila-mirador",
            nombre = "Huila El Mirador",
            region = "Pitalito, Huila",
            finca = "Finca El Mirador",
            productor = "Familia Cuéllar",
            altitud = 1750,
            variedad = "Caturra",
            proceso = Proceso.Lavado,
            tueste = 0.5f,
            notas = listOf("Panela", "Mandarina", "Cacao"),
            puntaje = 86.0,
            precio = 42000,
            descripcion = "Un café dulce y balanceado, cosechado a mano en las laderas del sur del Huila. " +
                "Fermentado 36 horas y secado en marquesina, deja una acidez cítrica suave y un final largo a cacao.",
            metodos = listOf("V60", "Chemex", "Prensa francesa"),
            colorA = Color(0xFFB5652F), colorB = Color(0xFF5A2B16),
        ),
        Cafe(
            id = "narino-esperanza",
            nombre = "Nariño La Esperanza",
            region = "Buesaco, Nariño",
            finca = "Finca La Esperanza",
            productor = "Doña Rosa Benavides",
            altitud = 2100,
            variedad = "Castillo",
            proceso = Proceso.Honey,
            tueste = 0.3f,
            notas = listOf("Frutos rojos", "Miel", "Jazmín"),
            puntaje = 87.5,
            precio = 52000,
            descripcion = "Cultivado a más de 2.000 metros, donde el grano madura despacio. El proceso honey conserva " +
                "parte del mucílago durante el secado y el resultado es una taza jugosa, floral y muy dulce.",
            metodos = listOf("V60", "AeroPress", "Kalita"),
            colorA = Color(0xFFC0485A), colorB = Color(0xFF5E1D2B),
        ),
        Cafe(
            id = "sierra-nubes",
            nombre = "Sierra Nevada Las Nubes",
            region = "Pueblo Bello, Cesar",
            finca = "Finca Las Nubes",
            productor = "Asociación Las Nubes",
            altitud = 1450,
            variedad = "Típica",
            proceso = Proceso.Natural,
            tueste = 0.55f,
            notas = listOf("Chocolate", "Uva pasa", "Vainilla"),
            puntaje = 85.5,
            precio = 46000,
            descripcion = "Secado al sol con la cereza entera durante tres semanas. Tiene cuerpo alto, dulzor a fruta " +
                "deshidratada y un chocolate que funciona muy bien con leche.",
            metodos = listOf("Espresso", "Moka", "Prensa francesa"),
            colorA = Color(0xFF7B5A8C), colorB = Color(0xFF33223F),
        ),
        Cafe(
            id = "planadas-geisha",
            nombre = "Planadas Geisha",
            region = "Planadas, Tolima",
            finca = "Finca Buenavista",
            productor = "Familia Rojas",
            altitud = 1900,
            variedad = "Geisha",
            proceso = Proceso.Lavado,
            tueste = 0.22f,
            notas = listOf("Bergamota", "Durazno", "Té negro"),
            puntaje = 89.0,
            precio = 78000,
            descripcion = "Nuestra microlote más especial. La variedad Geisha, cultivada en el sur del Tolima, " +
                "ofrece una taza delicada y aromática, con notas florales y cítricas muy limpias.",
            metodos = listOf("V60", "Chemex", "Sifón"),
            colorA = Color(0xFFD29A45), colorB = Color(0xFF6E4A17),
        ),
        Cafe(
            id = "inza-bourbon",
            nombre = "Inzá Bourbon Rosado",
            region = "Inzá, Cauca",
            finca = "Finca El Paraíso",
            productor = "Familia Yule",
            altitud = 1850,
            variedad = "Bourbon rosado",
            proceso = Proceso.Lavado,
            tueste = 0.35f,
            notas = listOf("Lima", "Caramelo", "Flor de naranjo"),
            puntaje = 88.0,
            precio = 64000,
            descripcion = "El bourbon rosado es una variedad escasa y muy apreciada. Este lote del oriente caucano " +
                "tiene acidez brillante, dulzor a caramelo y un aroma floral que se siente desde la molienda.",
            metodos = listOf("V60", "Kalita", "AeroPress"),
            colorA = Color(0xFFD27A86), colorB = Color(0xFF6B2E3A),
        ),
        Cafe(
            id = "jardin-clasico",
            nombre = "Jardín Clásico",
            region = "Jardín, Antioquia",
            finca = "Finca La Cascada",
            productor = "Familia Restrepo",
            altitud = 1650,
            variedad = "Castillo",
            proceso = Proceso.Lavado,
            tueste = 0.72f,
            notas = listOf("Nuez", "Chocolate amargo", "Caramelo"),
            puntaje = 84.0,
            precio = 38000,
            descripcion = "El café de todos los días: redondo, de cuerpo medio y baja acidez. Con un tueste un poco " +
                "más desarrollado, es ideal para espresso, capuchino y greca.",
            metodos = listOf("Espresso", "Greca", "Moka"),
            colorA = Color(0xFF8A5A3C), colorB = Color(0xFF2E1B11),
        ),
        Cafe(
            id = "filandia-anaerobico",
            nombre = "Filandia Anaeróbico",
            region = "Filandia, Quindío",
            finca = "Finca Santa Mónica",
            productor = "Familia Arango",
            altitud = 1700,
            variedad = "Colombia",
            proceso = Proceso.Natural,
            tueste = 0.45f,
            notas = listOf("Fresa", "Ron", "Nibs de cacao"),
            puntaje = 87.0,
            precio = 58000,
            descripcion = "Fermentado 72 horas en tanques sellados, sin oxígeno, antes de secarse como natural. " +
                "Es un café intenso y licoroso, con fruta roja madura y un final que recuerda al ron.",
            metodos = listOf("AeroPress", "V60", "Cold brew"),
            colorA = Color(0xFFB0413E), colorB = Color(0xFF461716),
        ),
        Cafe(
            id = "barichara-descafeinado",
            nombre = "Barichara Descafeinado",
            region = "San Gil, Santander",
            finca = "Finca Los Guayacanes",
            productor = "Cooperativa del Fonce",
            altitud = 1600,
            variedad = "Caturra",
            proceso = Proceso.Honey,
            tueste = 0.6f,
            notas = listOf("Panela", "Almendra", "Manzana roja"),
            puntaje = 84.5,
            precio = 44000,
            descripcion = "Descafeinado de forma natural con acetato de etilo obtenido de la caña de azúcar. " +
                "Conserva el dulzor y el cuerpo del café, para disfrutarlo también en la noche.",
            metodos = listOf("Prensa francesa", "Chemex", "Espresso"),
            colorA = Color(0xFF6F8A5B), colorB = Color(0xFF26361D),
        ),
    )

    fun porId(id: String): Cafe? = cafes.firstOrNull { it.id == id }
}

/** Formatea pesos colombianos con punto de miles: 42000 -> "$ 42.000". */
fun precioCop(valor: Int): String {
    val digitos = valor.toString()
    val conPuntos = digitos.reversed().chunked(3).joinToString(".").reversed()
    return "$ $conPuntos"
}

/** 1750 -> "1.750" */
fun miles(valor: Int): String = valor.toString().reversed().chunked(3).joinToString(".").reversed()

fun etiquetaTueste(tueste: Float): String = when {
    tueste < 0.3f -> "Claro"
    tueste < 0.5f -> "Medio claro"
    tueste < 0.65f -> "Medio"
    else -> "Medio oscuro"
}

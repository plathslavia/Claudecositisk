package com.vitalis.eps.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Dirección de la API PHP que corre en XAMPP (carpeta htdocs/vitalis_api).
 * En el emulador de Android, 10.0.2.2 es el "localhost" del computador.
 */
expect val urlServidorPorDefecto: String

@Serializable
private data class CitaDto(
    val id: Int,
    val especialidad: String,
    val profesional: String,
    val registro: String,
    val fecha: String,
    val hora: String,
    @SerialName("duracion_min") val duracionMin: Int,
    val modalidad: String,
    val sede: String,
    val lugar: String,
    val estado: String,
    val motivo: String,
    val autorizacion: String,
    @SerialName("cuota_moderadora") val cuotaModeradora: Int,
    val preparacion: List<String> = emptyList(),
)

@Serializable
private data class RespuestaCitas(val ok: Boolean, val citas: List<CitaDto> = emptyList(), val error: String? = null)

@Serializable
private data class CambioEstado(val id: Int, val estado: String)

@Serializable
private data class RespuestaSimple(val ok: Boolean, val error: String? = null)

/** Cliente de la API REST de la EPS: lee las citas de MySQL y guarda los cambios de estado. */
class ClienteApi(urlBase: String) : AutoCloseable {
    private val base = urlBase.trimEnd('/')

    private val http = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(HttpTimeout) {
            connectTimeoutMillis = 3_000
            requestTimeoutMillis = 6_000
        }
    }

    suspend fun citas(idAfiliado: Int): List<Cita> {
        val respuesta: RespuestaCitas = http.get("$base/citas.php") { parameter("afiliado", idAfiliado) }.body()
        if (!respuesta.ok) error(respuesta.error ?: "El servidor no devolvió las citas")
        return respuesta.citas.map { it.aCita() }
    }

    suspend fun cambiarEstado(idCita: String, estado: Estado) {
        val respuesta: RespuestaSimple = http.post("$base/cambiar_estado.php") {
            contentType(ContentType.Application.Json)
            setBody(CambioEstado(idCita.toInt(), estado.name))
        }.body()
        if (!respuesta.ok) error(respuesta.error ?: "El servidor no guardó el cambio")
    }

    override fun close() = http.close()
}

private fun CitaDto.aCita() = Cita(
    id = id.toString(),
    especialidad = Especialidad.entries.firstOrNull { it.name == especialidad } ?: Especialidad.MedicinaGeneral,
    profesional = profesional,
    registro = registro,
    fecha = fechaDesdeIso(fecha),
    hora = horaLegible(hora),
    duracionMin = duracionMin,
    modalidad = Modalidad.entries.firstOrNull { it.name == modalidad } ?: Modalidad.Presencial,
    sede = sede,
    lugar = lugar,
    estado = Estado.entries.firstOrNull { it.name == estado } ?: Estado.PorConfirmar,
    motivo = motivo,
    preparacion = preparacion,
    autorizacion = autorizacion,
    cuotaModeradora = cuotaModeradora,
)

private val DIAS = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")

/** "2026-10-01" -> Fecha("Jue", 1, 10, 2026). El día de la semana se calcula con el algoritmo de Sakamoto. */
internal fun fechaDesdeIso(iso: String): Fecha {
    val (a, m, d) = iso.take(10).split("-").map { it.toInt() }
    val t = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
    val y = if (m < 3) a - 1 else a
    val dia = (y + y / 4 - y / 100 + y / 400 + t[m - 1] + d) % 7
    return Fecha(DIAS[dia], d, m, a)
}

/** "14:15:00" -> "2:15 p. m." */
internal fun horaLegible(hora: String): String {
    val (h, min) = hora.split(":").map { it.toInt() }
    val h12 = if (h % 12 == 0) 12 else h % 12
    return "$h12:${min.toString().padStart(2, '0')} ${if (h < 12) "a. m." else "p. m."}"
}

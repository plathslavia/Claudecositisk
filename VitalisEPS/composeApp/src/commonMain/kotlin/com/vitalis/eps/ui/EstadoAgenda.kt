package com.vitalis.eps.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.vitalis.eps.data.Agenda
import com.vitalis.eps.data.Cita
import com.vitalis.eps.data.ClienteApi
import com.vitalis.eps.data.Empresa
import com.vitalis.eps.data.Estado
import com.vitalis.eps.data.urlServidorPorDefecto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class Filtro(val etiqueta: String) {
    Proximas("Próximas"),
    PorConfirmar("Por confirmar"),
    Historial("Historial"),
    Todas("Todas"),
}

/** De dónde salen las citas que se ven en pantalla. */
enum class Origen { Conectando, Servidor, Ejemplo }

/**
 * Estado de la agenda: las citas (del servidor XAMPP o, si no hay conexión, las de ejemplo),
 * la búsqueda, el filtro, la posición de la lista y los cambios que hace el afiliado.
 * Vive fuera de las pantallas para que el Maestro y el Detalle vean siempre los mismos datos.
 */
class EstadoAgenda {
    var busqueda by mutableStateOf("")
    var filtro by mutableStateOf(Filtro.Proximas)
    val lista = LazyListState()

    var urlServidor by mutableStateOf(urlServidorPorDefecto)
    var origen by mutableStateOf(Origen.Conectando)
        private set
    var mensaje by mutableStateOf<String?>(null)
        private set

    private var base by mutableStateOf(Agenda.citas)
    private val cambios = mutableStateMapOf<String, Estado>()

    val citas: List<Cita> get() = base.map { c -> cambios[c.id]?.let { c.copy(estado = it) } ?: c }

    fun cita(id: String): Cita? = citas.firstOrNull { it.id == id }

    val proxima: Cita? get() = citas.filter { it.estado.esProxima }.minByOrNull { it.fecha.orden }

    /** Carga las citas desde la API de XAMPP; si no responde, deja las de ejemplo. */
    suspend fun sincronizar() {
        origen = Origen.Conectando
        try {
            val delServidor = ClienteApi(urlServidor).use { it.citas(Empresa.ID_AFILIADO) }
            base = delServidor
            cambios.clear()
            origen = Origen.Servidor
            mensaje = null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            base = Agenda.citas
            cambios.clear()
            origen = Origen.Ejemplo
            mensaje = "No hubo respuesta de $urlServidor. Se muestran datos de ejemplo."
        }
    }

    /** Cambia el estado en pantalla de inmediato y, si hay servidor, lo guarda en MySQL. */
    fun cambiarEstado(id: String, estado: Estado, alcance: CoroutineScope) {
        val anterior = cita(id)?.estado ?: return
        cambios[id] = estado
        if (origen != Origen.Servidor) return
        alcance.launch {
            try {
                ClienteApi(urlServidor).use { it.cambiarEstado(id, estado) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                cambios[id] = anterior
                mensaje = "No se pudo guardar el cambio en el servidor."
            }
        }
    }

    val resultados: List<Cita>
        get() {
            val texto = busqueda.trim()
            return citas
                .filter { c ->
                    when (filtro) {
                        Filtro.Proximas -> c.estado.esProxima
                        Filtro.PorConfirmar -> c.estado == Estado.PorConfirmar
                        Filtro.Historial -> !c.estado.esProxima
                        Filtro.Todas -> true
                    }
                }
                .filter { c ->
                    texto.isEmpty() || listOf(c.especialidad.nombre, c.profesional, c.sede, c.modalidad.etiqueta)
                        .any { it.contains(texto, ignoreCase = true) }
                }
                .sortedWith(compareBy<Cita> { !it.estado.esProxima }.thenBy { if (it.estado.esProxima) it.fecha.orden else -it.fecha.orden })
        }
}

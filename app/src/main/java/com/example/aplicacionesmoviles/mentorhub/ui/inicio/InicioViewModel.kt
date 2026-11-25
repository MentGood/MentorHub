package com.example.aplicacionesmoviles.mentorhub.ui.inicio

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionesmoviles.R
import com.example.aplicacionesmoviles.mentorhub.data.local.InscripcionDetalle
import com.example.aplicacionesmoviles.mentorhub.data.local.UsuarioEntity
import com.example.aplicacionesmoviles.mentorhub.data.repository.InscripcionRepository
import com.example.aplicacionesmoviles.mentorhub.data.repository.SesionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ---------------------------------------------------------------
//  ViewModel de la pantalla de inicio del estudiante
//  SELECT: inscripciones del usuario (Flow observado)
//  UPDATE: marcar asistencia      DELETE: cancelar inscripción
// ---------------------------------------------------------------
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InicioViewModel @Inject constructor(
    private val inscripcionRepository: InscripcionRepository,
    sesionRepository: SesionRepository
) : ViewModel() {

    val usuario: StateFlow<UsuarioEntity?> = sesionRepository.usuarioActual

    // Cada vez que cambia el usuario o sus inscripciones, la lista se actualiza sola
    val inscripciones: StateFlow<List<InscripcionDetalle>> = usuario
        .flatMapLatest { u -> if (u == null) flowOf(emptyList()) else inscripcionRepository.inscripcionesDe(u.idUsuario) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _mensaje = MutableStateFlow<Int?>(null)
    val mensaje: StateFlow<Int?> = _mensaje.asStateFlow()

    fun marcarAsistencia(idInscripcion: Int) = ejecutar(R.string.mh_msg_asistencia) {
        inscripcionRepository.marcarAsistencia(idInscripcion)
    }

    fun cancelar(idInscripcion: Int) = ejecutar(R.string.mh_msg_inscripcion_cancelada) {
        inscripcionRepository.cancelar(idInscripcion)
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    /** Ejecuta una operación de la base de datos y muestra éxito o error */
    private fun ejecutar(@StringRes mensajeExito: Int, accion: suspend () -> Unit) {
        viewModelScope.launch {
            _mensaje.value = try {
                accion()
                mensajeExito
            } catch (e: Exception) {
                R.string.mh_error_general
            }
        }
    }
}
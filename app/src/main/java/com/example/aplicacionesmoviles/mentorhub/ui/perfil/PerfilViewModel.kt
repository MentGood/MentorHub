package com.example.aplicacionesmoviles.mentorhub.ui.perfil

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionesmoviles.R
import com.example.aplicacionesmoviles.mentorhub.data.local.UsuarioEntity
import com.example.aplicacionesmoviles.mentorhub.data.repository.SesionRepository
import com.example.aplicacionesmoviles.mentorhub.data.repository.UsuarioRepository
import com.example.aplicacionesmoviles.mentorhub.util.esCorreoValido
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PerfilUiState(
    val nombre: String = "",
    val correo: String = "",
    @StringRes val error: Int? = null,
    @StringRes val mensaje: Int? = null,
    val sesionCerrada: Boolean = false
)

// ---------------------------------------------------------------
//  ViewModel del Perfil: UPDATE y DELETE de la tabla usuarios
// ---------------------------------------------------------------
@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val sesionRepository: SesionRepository
) : ViewModel() {

    private val usuario: UsuarioEntity? get() = sesionRepository.usuarioActual.value

    private val _uiState = MutableStateFlow(
        PerfilUiState(nombre = usuario?.nombre ?: "", correo = usuario?.correo ?: "")
    )
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    val matricula: String get() = usuario?.matricula ?: ""

    fun onNombreChange(valor: String) {
        _uiState.value = _uiState.value.copy(nombre = valor, error = null)
    }

    fun onCorreoChange(valor: String) {
        _uiState.value = _uiState.value.copy(correo = valor, error = null)
    }

    /** UPDATE: guarda nombre y correo nuevos */
    fun guardarCambios() {
        val actual = usuario ?: return
        val estado = _uiState.value
        val error = when {
            estado.nombre.isBlank() || estado.correo.isBlank() -> R.string.mh_error_campos_vacios
            !estado.correo.esCorreoValido() -> R.string.mh_error_correo_invalido
            else -> null
        }
        if (error != null) {
            _uiState.value = estado.copy(error = error)
            return
        }
        viewModelScope.launch {
            try {
                val actualizado = actual.copy(nombre = estado.nombre.trim(), correo = estado.correo.trim())
                usuarioRepository.actualizar(actualizado)
                sesionRepository.iniciar(actualizado)   // actualiza el usuario de la sesión
                _uiState.value = _uiState.value.copy(mensaje = R.string.mh_msg_perfil_actualizado)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = R.string.mh_error_general)
            }
        }
    }

    /** DELETE: borra la cuenta (y sus inscripciones por CASCADE) */
    fun eliminarCuenta() {
        val actual = usuario ?: return
        viewModelScope.launch {
            try {
                usuarioRepository.eliminar(actual)
                sesionRepository.cerrar()
                _uiState.value = _uiState.value.copy(sesionCerrada = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = R.string.mh_error_general)
            }
        }
    }

    fun cerrarSesion() {
        sesionRepository.cerrar()
        _uiState.value = _uiState.value.copy(sesionCerrada = true)
    }

    fun limpiarMensaje() {
        _uiState.value = _uiState.value.copy(mensaje = null)
    }
}
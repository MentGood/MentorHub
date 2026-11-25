package com.example.aplicacionesmoviles.mentorhub.ui.auth

import android.database.sqlite.SQLiteConstraintException
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionesmoviles.R
import com.example.aplicacionesmoviles.mentorhub.data.local.UsuarioEntity
import com.example.aplicacionesmoviles.mentorhub.data.repository.SesionRepository
import com.example.aplicacionesmoviles.mentorhub.data.repository.UsuarioRepository
import com.example.aplicacionesmoviles.mentorhub.util.esContrasenaSegura
import com.example.aplicacionesmoviles.mentorhub.util.esCorreoValido
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ---------------------------------------------------------------
//  PATRÓN UDF (Unidirectional Data Flow)
//  - El ESTADO baja del ViewModel a la pantalla (uiState).
//  - Los EVENTOS suben de la pantalla al ViewModel (onMatriculaChange,
//    iniciarSesion, registrar...).
//  La pantalla nunca cambia los datos directamente.
// ---------------------------------------------------------------

/** Todo lo que la pantalla de login/registro necesita mostrar */
data class AuthUiState(
    val matricula: String = "",
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val confirmar: String = "",
    @StringRes val error: Int? = null,
    val cargando: Boolean = false,
    val exito: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,   // inyectado por Hilt
    private val sesionRepository: SesionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ----- Eventos que llegan desde la pantalla -----
    fun onMatriculaChange(valor: String) = _uiState.update { it.copy(matricula = valor, error = null) }
    fun onNombreChange(valor: String) = _uiState.update { it.copy(nombre = valor, error = null) }
    fun onCorreoChange(valor: String) = _uiState.update { it.copy(correo = valor, error = null) }
    fun onContrasenaChange(valor: String) = _uiState.update { it.copy(contrasena = valor, error = null) }
    fun onConfirmarChange(valor: String) = _uiState.update { it.copy(confirmar = valor, error = null) }

    /** SELECT: busca el usuario y revisa la contraseña */
    fun iniciarSesion() {
        val estado = _uiState.value
        if (estado.matricula.isBlank() || estado.contrasena.isBlank()) {
            _uiState.update { it.copy(error = R.string.mh_error_campos_vacios) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                val usuario = usuarioRepository.buscarPorMatricula(estado.matricula.trim())
                when {
                    usuario == null ->
                        _uiState.update { it.copy(error = R.string.mh_error_usuario_no_existe, cargando = false) }
                    usuario.contrasena != estado.contrasena ->
                        _uiState.update { it.copy(error = R.string.mh_error_contrasena_incorrecta, cargando = false) }
                    else -> {
                        sesionRepository.iniciar(usuario)
                        _uiState.update { AuthUiState(exito = true) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = R.string.mh_error_general, cargando = false) }
            }
        }
    }

    /** INSERT: valida los datos y crea el usuario */
    fun registrar() {
        val estado = _uiState.value
        val error = when {
            estado.matricula.isBlank() || estado.nombre.isBlank() || estado.correo.isBlank() ||
                    estado.contrasena.isBlank() || estado.confirmar.isBlank() -> R.string.mh_error_campos_vacios
            !estado.correo.esCorreoValido() -> R.string.mh_error_correo_invalido
            !estado.contrasena.esContrasenaSegura() -> R.string.mh_error_contrasena_corta
            estado.contrasena != estado.confirmar -> R.string.mh_error_contrasenas_distintas
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(error = error) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                // SELECT: primero revisa si la matrícula ya está registrada
                if (usuarioRepository.buscarPorMatricula(estado.matricula.trim()) != null) {
                    _uiState.update { it.copy(error = R.string.mh_error_matricula_repetida, cargando = false) }
                    return@launch
                }
                usuarioRepository.registrar(
                    UsuarioEntity(
                        matricula = estado.matricula.trim(),
                        nombre = estado.nombre.trim(),
                        correo = estado.correo.trim(),
                        contrasena = estado.contrasena
                    )
                )
                _uiState.update { AuthUiState(exito = true) }
            } catch (e: SQLiteConstraintException) {
                // La matrícula es UNIQUE: si ya existe, Room lanza esta excepción
                _uiState.update { it.copy(error = R.string.mh_error_matricula_repetida, cargando = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = R.string.mh_error_general, cargando = false) }
            }
        }
    }

    fun limpiarExito() = _uiState.update { it.copy(exito = false) }
}
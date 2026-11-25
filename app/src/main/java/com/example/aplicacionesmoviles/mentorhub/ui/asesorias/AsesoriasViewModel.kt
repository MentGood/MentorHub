package com.example.aplicacionesmoviles.mentorhub.ui.asesorias

import android.database.sqlite.SQLiteConstraintException
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionesmoviles.R
import com.example.aplicacionesmoviles.mentorhub.data.local.AsesoriaConMateria
import com.example.aplicacionesmoviles.mentorhub.data.local.AsesoriaEntity
import com.example.aplicacionesmoviles.mentorhub.data.local.MateriaEntity
import com.example.aplicacionesmoviles.mentorhub.data.repository.AsesoriaRepository
import com.example.aplicacionesmoviles.mentorhub.data.repository.InscripcionRepository
import com.example.aplicacionesmoviles.mentorhub.data.repository.MateriaRepository
import com.example.aplicacionesmoviles.mentorhub.data.repository.SesionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Datos del formulario para crear o editar una asesoría */
data class FormAsesoria(
    val idAsesoria: Int = 0,          // 0 = nueva, otro número = editar
    val idMateria: Int? = null,
    val docente: String = "",
    val dia: String = "",
    val hora: String = "",
    val aula: String = "",
    val cupo: String = "",
    @StringRes val error: Int? = null
)

// ---------------------------------------------------------------
//  ViewModel del Centro de asesorías
//  SELECT asesorías y materias | INSERT/UPDATE/DELETE asesorías
//  INSERT inscripción (reservar)
// ---------------------------------------------------------------
@HiltViewModel
class AsesoriasViewModel @Inject constructor(
    private val asesoriaRepository: AsesoriaRepository,
    private val inscripcionRepository: InscripcionRepository,
    private val sesionRepository: SesionRepository,
    materiaRepository: MateriaRepository
) : ViewModel() {

    val asesorias: StateFlow<List<AsesoriaConMateria>> = asesoriaRepository.asesorias
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val materias: StateFlow<List<MateriaEntity>> = materiaRepository.materias
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // null = formulario cerrado
    private val _formulario = MutableStateFlow<FormAsesoria?>(null)
    val formulario: StateFlow<FormAsesoria?> = _formulario.asStateFlow()

    private val _mensaje = MutableStateFlow<Int?>(null)
    val mensaje: StateFlow<Int?> = _mensaje.asStateFlow()

    // ----- Formulario -----
    fun abrirNueva() {
        _formulario.value = FormAsesoria()
    }

    fun abrirEditar(item: AsesoriaConMateria) {
        val a = item.asesoria
        _formulario.value = FormAsesoria(a.idAsesoria, a.idMateria, a.docente, a.dia, a.hora, a.aula, a.cupo.toString())
    }

    fun cerrarFormulario() {
        _formulario.value = null
    }

    fun onFormChange(nuevo: FormAsesoria) {
        _formulario.value = nuevo.copy(error = null)
    }

    /** INSERT (si es nueva) o UPDATE (si se está editando) */
    fun guardar() {
        val f = _formulario.value ?: return
        val cupo = f.cupo.trim().toIntOrNull()
        val error = when {
            f.idMateria == null -> R.string.mh_error_elige_materia
            f.docente.isBlank() || f.dia.isBlank() || f.hora.isBlank() || f.aula.isBlank() || f.cupo.isBlank() ->
                R.string.mh_error_campos_vacios
            cupo == null || cupo <= 0 -> R.string.mh_error_cupo_invalido
            else -> null
        }
        if (error != null) {
            _formulario.update { it?.copy(error = error) }
            return
        }
        val asesoria = AsesoriaEntity(
            idAsesoria = f.idAsesoria,
            idMateria = f.idMateria!!,
            docente = f.docente.trim(),
            dia = f.dia.trim(),
            hora = f.hora.trim(),
            aula = f.aula.trim(),
            cupo = cupo!!
        )
        viewModelScope.launch {
            try {
                if (f.idAsesoria == 0) {
                    asesoriaRepository.insertar(asesoria)
                    _mensaje.value = R.string.mh_msg_asesoria_creada
                } else {
                    asesoriaRepository.actualizar(asesoria)
                    _mensaje.value = R.string.mh_msg_asesoria_actualizada
                }
                _formulario.value = null
            } catch (e: Exception) {
                _formulario.update { it?.copy(error = R.string.mh_error_general) }
            }
        }
    }

    /** DELETE (también borra sus inscripciones por el CASCADE) */
    fun eliminar(item: AsesoriaConMateria) {
        viewModelScope.launch {
            _mensaje.value = try {
                asesoriaRepository.eliminar(item.asesoria)
                R.string.mh_msg_asesoria_eliminada
            } catch (e: Exception) {
                R.string.mh_error_general
            }
        }
    }

    /** INSERT en inscripciones, revisando sesión, duplicados y cupo */
    fun reservar(item: AsesoriaConMateria) {
        val usuario = sesionRepository.usuarioActual.value
        if (usuario == null) {
            _mensaje.value = R.string.mh_error_sin_sesion
            return
        }
        viewModelScope.launch {
            _mensaje.value = try {
                when {
                    inscripcionRepository.yaInscrito(usuario.idUsuario, item.asesoria.idAsesoria) ->
                        R.string.mh_error_ya_inscrito
                    inscripcionRepository.inscritosEn(item.asesoria.idAsesoria) >= item.asesoria.cupo ->
                        R.string.mh_error_cupo_lleno
                    else -> {
                        inscripcionRepository.inscribir(usuario.idUsuario, item.asesoria.idAsesoria)
                        R.string.mh_msg_reservada
                    }
                }
            } catch (e: SQLiteConstraintException) {
                R.string.mh_error_ya_inscrito
            } catch (e: Exception) {
                R.string.mh_error_general
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
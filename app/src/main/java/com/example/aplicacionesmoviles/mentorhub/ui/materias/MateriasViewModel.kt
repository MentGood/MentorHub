package com.example.aplicacionesmoviles.mentorhub.ui.materias

import android.database.sqlite.SQLiteConstraintException
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionesmoviles.R
import com.example.aplicacionesmoviles.mentorhub.data.local.MateriaEntity
import com.example.aplicacionesmoviles.mentorhub.data.repository.MateriaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Formulario para crear o editar una materia */
data class FormMateria(
    val idMateria: Int = 0,           // 0 = nueva
    val nombre: String = "",
    val clave: String = "",
    @StringRes val error: Int? = null
)

// ---------------------------------------------------------------
//  ViewModel de Materias: CRUD completo
//  SELECT (Flow) | INSERT | UPDATE | DELETE
// ---------------------------------------------------------------
@HiltViewModel
class MateriasViewModel @Inject constructor(
    private val materiaRepository: MateriaRepository
) : ViewModel() {

    val materias: StateFlow<List<MateriaEntity>> = materiaRepository.materias
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _formulario = MutableStateFlow<FormMateria?>(null)
    val formulario: StateFlow<FormMateria?> = _formulario.asStateFlow()

    private val _mensaje = MutableStateFlow<Int?>(null)
    val mensaje: StateFlow<Int?> = _mensaje.asStateFlow()

    fun abrirNueva() {
        _formulario.value = FormMateria()
    }

    fun abrirEditar(materia: MateriaEntity) {
        _formulario.value = FormMateria(materia.idMateria, materia.nombre, materia.clave)
    }

    fun cerrarFormulario() {
        _formulario.value = null
    }

    fun onFormChange(nuevo: FormMateria) {
        _formulario.value = nuevo.copy(error = null)
    }

    fun guardar() {
        val f = _formulario.value ?: return
        if (f.nombre.isBlank() || f.clave.isBlank()) {
            _formulario.update { it?.copy(error = R.string.mh_error_campos_vacios) }
            return
        }
        val materia = MateriaEntity(idMateria = f.idMateria, nombre = f.nombre.trim(), clave = f.clave.trim().uppercase())
        viewModelScope.launch {
            try {
                if (f.idMateria == 0) {
                    materiaRepository.insertar(materia)
                    _mensaje.value = R.string.mh_msg_materia_creada
                } else {
                    materiaRepository.actualizar(materia)
                    _mensaje.value = R.string.mh_msg_materia_actualizada
                }
                _formulario.value = null
            } catch (e: SQLiteConstraintException) {
                // El nombre de la materia es UNIQUE
                _formulario.update { it?.copy(error = R.string.mh_error_materia_repetida) }
            } catch (e: Exception) {
                _formulario.update { it?.copy(error = R.string.mh_error_general) }
            }
        }
    }

    fun eliminar(materia: MateriaEntity) {
        viewModelScope.launch {
            _mensaje.value = try {
                if (materiaRepository.tieneAsesorias(materia.idMateria)) {
                    R.string.mh_error_materia_con_asesorias
                } else {
                    materiaRepository.eliminar(materia)
                    R.string.mh_msg_materia_eliminada
                }
            } catch (e: SQLiteConstraintException) {
                R.string.mh_error_materia_con_asesorias
            } catch (e: Exception) {
                R.string.mh_error_general
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
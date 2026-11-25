package com.example.aplicacionesmoviles.mentorhub.data.repository

import com.example.aplicacionesmoviles.mentorhub.data.local.AsesoriaConMateria
import com.example.aplicacionesmoviles.mentorhub.data.local.AsesoriaDao
import com.example.aplicacionesmoviles.mentorhub.data.local.AsesoriaEntity
import com.example.aplicacionesmoviles.mentorhub.data.local.InscripcionDao
import com.example.aplicacionesmoviles.mentorhub.data.local.InscripcionDetalle
import com.example.aplicacionesmoviles.mentorhub.data.local.InscripcionEntity
import com.example.aplicacionesmoviles.mentorhub.data.local.MateriaDao
import com.example.aplicacionesmoviles.mentorhub.data.local.MateriaEntity
import com.example.aplicacionesmoviles.mentorhub.data.local.UsuarioDao
import com.example.aplicacionesmoviles.mentorhub.data.local.UsuarioEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// ---------------------------------------------------------------
//  PATRÓN REPOSITORY
//  Los ViewModels NO hablan directo con Room: le piden los datos al
//  repositorio, y el repositorio decide de dónde sacarlos (aquí, de los DAOs).
//  Si mañana los datos vinieran de internet, solo cambia el repositorio.
//
//  @Inject constructor -> Hilt sabe crear la clase y le pasa el DAO
//                         (INYECCIÓN DE DEPENDENCIAS).
//  @Singleton          -> Hilt crea una sola instancia para toda la app.
// ---------------------------------------------------------------

@Singleton
class UsuarioRepository @Inject constructor(
    private val usuarioDao: UsuarioDao
) {
    suspend fun registrar(usuario: UsuarioEntity): Long = usuarioDao.insertar(usuario)
    suspend fun buscarPorMatricula(matricula: String): UsuarioEntity? = usuarioDao.buscarPorMatricula(matricula)
    suspend fun actualizar(usuario: UsuarioEntity) = usuarioDao.actualizar(usuario)
    suspend fun eliminar(usuario: UsuarioEntity) = usuarioDao.eliminar(usuario)
}

@Singleton
class MateriaRepository @Inject constructor(
    private val materiaDao: MateriaDao
) {
    val materias: Flow<List<MateriaEntity>> = materiaDao.obtenerTodas()
    suspend fun insertar(materia: MateriaEntity): Long = materiaDao.insertar(materia)
    suspend fun actualizar(materia: MateriaEntity) = materiaDao.actualizar(materia)
    suspend fun eliminar(materia: MateriaEntity) = materiaDao.eliminar(materia)
    suspend fun tieneAsesorias(idMateria: Int): Boolean = materiaDao.contarAsesorias(idMateria) > 0
}

@Singleton
class AsesoriaRepository @Inject constructor(
    private val asesoriaDao: AsesoriaDao
) {
    val asesorias: Flow<List<AsesoriaConMateria>> = asesoriaDao.obtenerTodas()
    suspend fun insertar(asesoria: AsesoriaEntity): Long = asesoriaDao.insertar(asesoria)
    suspend fun actualizar(asesoria: AsesoriaEntity) = asesoriaDao.actualizar(asesoria)
    suspend fun eliminar(asesoria: AsesoriaEntity) = asesoriaDao.eliminar(asesoria)
}

@Singleton
class InscripcionRepository @Inject constructor(
    private val inscripcionDao: InscripcionDao
) {
    fun inscripcionesDe(idUsuario: Int): Flow<List<InscripcionDetalle>> =
        inscripcionDao.obtenerDeUsuario(idUsuario)

    suspend fun inscribir(idUsuario: Int, idAsesoria: Int): Long =
        inscripcionDao.insertar(InscripcionEntity(idUsuario = idUsuario, idAsesoria = idAsesoria))

    suspend fun marcarAsistencia(idInscripcion: Int) =
        inscripcionDao.actualizarEstado(idInscripcion, InscripcionEntity.ESTADO_ASISTIO)

    suspend fun cancelar(idInscripcion: Int) = inscripcionDao.eliminar(idInscripcion)

    suspend fun yaInscrito(idUsuario: Int, idAsesoria: Int): Boolean =
        inscripcionDao.contarInscripcion(idUsuario, idAsesoria) > 0

    suspend fun inscritosEn(idAsesoria: Int): Int = inscripcionDao.contarInscritos(idAsesoria)
}

/**
 * Guarda qué usuario inició sesión. Es @Singleton, así todas las
 * pantallas comparten el mismo usuario. Usa StateFlow (PATRÓN OBSERVER):
 * las pantallas que lo observan se enteran cuando cambia.
 */
@Singleton
class SesionRepository @Inject constructor() {
    private val _usuarioActual = MutableStateFlow<UsuarioEntity?>(null)
    val usuarioActual: StateFlow<UsuarioEntity?> = _usuarioActual.asStateFlow()

    fun iniciar(usuario: UsuarioEntity) {
        _usuarioActual.value = usuario
    }

    fun cerrar() {
        _usuarioActual.value = null
    }
}
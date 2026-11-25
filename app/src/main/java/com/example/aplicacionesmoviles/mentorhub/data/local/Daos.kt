package com.example.aplicacionesmoviles.mentorhub.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// ---------------------------------------------------------------
//  DAOs (Data Access Object): aquí están las operaciones CRUD
//  @Insert -> INSERT   @Query -> SELECT / UPDATE / DELETE con SQL
//  @Update -> UPDATE   @Delete -> DELETE
//  suspend -> se ejecuta en segundo plano (corrutina) sin congelar la app
//  Flow    -> la consulta se "observa": si cambian los datos, la UI se actualiza sola
// ---------------------------------------------------------------

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE matricula = :matricula LIMIT 1")
    suspend fun buscarPorMatricula(matricula: String): UsuarioEntity?

    @Update
    suspend fun actualizar(usuario: UsuarioEntity)

    @Delete
    suspend fun eliminar(usuario: UsuarioEntity)
}

@Dao
interface MateriaDao {
    @Query("SELECT * FROM materias ORDER BY nombre")
    fun obtenerTodas(): Flow<List<MateriaEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(materia: MateriaEntity): Long

    @Update
    suspend fun actualizar(materia: MateriaEntity)

    @Delete
    suspend fun eliminar(materia: MateriaEntity)

    @Query("SELECT COUNT(*) FROM asesorias WHERE idMateria = :idMateria")
    suspend fun contarAsesorias(idMateria: Int): Int
}

@Dao
interface AsesoriaDao {
    @Query(
        "SELECT a.*, m.nombre AS nombreMateria, " +
                "(SELECT COUNT(*) FROM inscripciones i WHERE i.idAsesoria = a.idAsesoria) AS inscritos " +
                "FROM asesorias a INNER JOIN materias m ON a.idMateria = m.idMateria " +
                "ORDER BY m.nombre"
    )
    fun obtenerTodas(): Flow<List<AsesoriaConMateria>>

    @Insert
    suspend fun insertar(asesoria: AsesoriaEntity): Long

    @Update
    suspend fun actualizar(asesoria: AsesoriaEntity)

    @Delete
    suspend fun eliminar(asesoria: AsesoriaEntity)
}

@Dao
interface InscripcionDao {
    @Query(
        "SELECT i.idInscripcion, i.idAsesoria, i.estado, m.nombre AS nombreMateria, " +
                "a.docente, a.dia, a.hora, a.aula " +
                "FROM inscripciones i " +
                "INNER JOIN asesorias a ON i.idAsesoria = a.idAsesoria " +
                "INNER JOIN materias m ON a.idMateria = m.idMateria " +
                "WHERE i.idUsuario = :idUsuario ORDER BY i.idInscripcion DESC"
    )
    fun obtenerDeUsuario(idUsuario: Int): Flow<List<InscripcionDetalle>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(inscripcion: InscripcionEntity): Long

    @Query("UPDATE inscripciones SET estado = :estado WHERE idInscripcion = :idInscripcion")
    suspend fun actualizarEstado(idInscripcion: Int, estado: String)

    @Query("DELETE FROM inscripciones WHERE idInscripcion = :idInscripcion")
    suspend fun eliminar(idInscripcion: Int)

    @Query("SELECT COUNT(*) FROM inscripciones WHERE idUsuario = :idUsuario AND idAsesoria = :idAsesoria")
    suspend fun contarInscripcion(idUsuario: Int, idAsesoria: Int): Int

    @Query("SELECT COUNT(*) FROM inscripciones WHERE idAsesoria = :idAsesoria")
    suspend fun contarInscritos(idAsesoria: Int): Int
}
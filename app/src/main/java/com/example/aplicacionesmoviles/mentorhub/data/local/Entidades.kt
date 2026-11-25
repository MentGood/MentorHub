package com.example.aplicacionesmoviles.mentorhub.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ---------------------------------------------------------------
//  ENTIDADES DE ROOM (cada una es una TABLA de la base de datos)
//  @Entity      -> indica que la clase es una tabla
//  @PrimaryKey  -> llave primaria (autoGenerate = autoincrementable)
//  ForeignKey   -> llave foránea que relaciona dos tablas
// ---------------------------------------------------------------

/** Tabla 1: usuarios (alumnos registrados) */
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["matricula"], unique = true)]   // no se repite la matrícula
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val idUsuario: Int = 0,
    val matricula: String,
    val nombre: String,
    val correo: String,
    val contrasena: String
)

/** Tabla 2: materias */
@Entity(
    tableName = "materias",
    indices = [Index(value = ["nombre"], unique = true)]      // no se repite el nombre
)
data class MateriaEntity(
    @PrimaryKey(autoGenerate = true) val idMateria: Int = 0,
    val nombre: String,
    val clave: String
)

/** Tabla 3: asesorías (cada asesoría pertenece a una materia) */
@Entity(
    tableName = "asesorias",
    foreignKeys = [
        ForeignKey(
            entity = MateriaEntity::class,
            parentColumns = ["idMateria"],
            childColumns = ["idMateria"],
            onDelete = ForeignKey.RESTRICT          // no deja borrar una materia con asesorías
        )
    ],
    indices = [Index("idMateria")]
)
data class AsesoriaEntity(
    @PrimaryKey(autoGenerate = true) val idAsesoria: Int = 0,
    val idMateria: Int,
    val docente: String,
    val dia: String,
    val hora: String,
    val aula: String,
    val cupo: Int
)

/** Tabla 4: inscripciones (une a un usuario con una asesoría) */
@Entity(
    tableName = "inscripciones",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["idUsuario"],
            childColumns = ["idUsuario"],
            onDelete = ForeignKey.CASCADE           // si se borra el usuario, se borran sus inscripciones
        ),
        ForeignKey(
            entity = AsesoriaEntity::class,
            parentColumns = ["idAsesoria"],
            childColumns = ["idAsesoria"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["idUsuario", "idAsesoria"], unique = true),  // un alumno no se inscribe dos veces
        Index("idAsesoria")
    ]
)
data class InscripcionEntity(
    @PrimaryKey(autoGenerate = true) val idInscripcion: Int = 0,
    val idUsuario: Int,
    val idAsesoria: Int,
    val estado: String = ESTADO_INSCRITO
) {
    companion object {
        const val ESTADO_INSCRITO = "Inscrito"
        const val ESTADO_ASISTIO = "Asistió"
    }
}

// ---------------------------------------------------------------
//  CLASES PARA RESULTADOS DE CONSULTAS CON JOIN (no son tablas)
// ---------------------------------------------------------------

/** Asesoría + nombre de su materia + número de alumnos inscritos */
data class AsesoriaConMateria(
    @Embedded val asesoria: AsesoriaEntity,
    val nombreMateria: String,
    val inscritos: Int
)

/** Inscripción con los datos de la asesoría y la materia */
data class InscripcionDetalle(
    val idInscripcion: Int,
    val idAsesoria: Int,
    val estado: String,
    val nombreMateria: String,
    val docente: String,
    val dia: String,
    val hora: String,
    val aula: String
)
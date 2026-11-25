package com.example.aplicacionesmoviles.mentorhub.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

// ---------------------------------------------------------------
//  BASE DE DATOS ROOM (SQLite local): mentorhub_room.db
//  @Database -> lista las 4 entidades (tablas) y la versión
//
//  PATRÓN SINGLETON: el "companion object" guarda UNA sola instancia
//  en INSTANCE. Si ya existe, la regresa; si no, la crea dentro de
//  synchronized para que dos hilos no creen dos bases al mismo tiempo.
// ---------------------------------------------------------------
@Database(
    entities = [
        UsuarioEntity::class,
        MateriaEntity::class,
        AsesoriaEntity::class,
        InscripcionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MentorHubDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun materiaDao(): MateriaDao
    abstract fun asesoriaDao(): AsesoriaDao
    abstract fun inscripcionDao(): InscripcionDao

    companion object {
        @Volatile
        private var INSTANCE: MentorHubDatabase? = null

        fun getInstance(context: Context): MentorHubDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MentorHubDatabase::class.java,
                    "mentorhub_room.db"
                )
                    .addCallback(datosIniciales)
                    .build()
                    .also { INSTANCE = it }
            }
        }

        // Datos de ejemplo que se insertan SOLO la primera vez que se crea la base
        private val datosIniciales = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL(
                    "INSERT INTO materias (nombre, clave) VALUES " +
                            "('Cálculo Diferencial', 'MAT101'), " +
                            "('Álgebra Lineal', 'MAT102'), " +
                            "('Física General', 'FIS101'), " +
                            "('Programación I', 'PRO101')"
                )
                db.execSQL(
                    "INSERT INTO asesorias (idMateria, docente, dia, hora, aula, cupo) VALUES " +
                            "(1, 'Dra. Laura Méndez', 'Lunes', '10:00', 'B-204', 8), " +
                            "(2, 'Mtro. Carlos Ruiz', 'Miércoles', '12:00', 'A-101', 3), " +
                            "(3, 'Ing. Sofía Ramírez', 'Jueves', '09:00', 'C-110', 5), " +
                            "(4, 'Mtro. Jorge Hernández', 'Viernes', '11:00', 'Lab 3', 10)"
                )
            }
        }
    }
}
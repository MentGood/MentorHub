package com.example.aplicacionesmoviles.mentorhub.util

import android.util.Patterns
import com.example.aplicacionesmoviles.mentorhub.data.local.AsesoriaConMateria

// ---------------------------------------------------------------
//  FUNCIONES DE EXTENSIÓN
//  Agregan funciones nuevas a clases que ya existen (String,
//  AsesoriaConMateria) sin modificarlas ni heredar de ellas.
// ---------------------------------------------------------------

/** "alumno@uttt.edu.mx".esCorreoValido() -> true */
fun String.esCorreoValido(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this.trim()).matches()

/** La contraseña debe tener al menos 6 caracteres */
fun String.esContrasenaSegura(): Boolean = this.length >= 6

/** Cupos que quedan libres en una asesoría */
fun AsesoriaConMateria.lugaresDisponibles(): Int = asesoria.cupo - inscritos

/** true si la asesoría ya no tiene lugares */
fun AsesoriaConMateria.estaLlena(): Boolean = lugaresDisponibles() <= 0
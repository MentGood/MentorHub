package com.example.aplicacionesmoviles.mentorhub.di

import android.content.Context
import com.example.aplicacionesmoviles.mentorhub.data.local.AsesoriaDao
import com.example.aplicacionesmoviles.mentorhub.data.local.InscripcionDao
import com.example.aplicacionesmoviles.mentorhub.data.local.MateriaDao
import com.example.aplicacionesmoviles.mentorhub.data.local.MentorHubDatabase
import com.example.aplicacionesmoviles.mentorhub.data.local.UsuarioDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// ---------------------------------------------------------------
//  INYECCIÓN DE DEPENDENCIAS CON HILT
//  @Module + @InstallIn(SingletonComponent) -> le enseña a Hilt cómo
//  crear la base de datos y los DAOs, y los comparte en toda la app.
//  Así ninguna clase hace "new" de la base: Hilt se la entrega.
// ---------------------------------------------------------------
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MentorHubDatabase =
        MentorHubDatabase.getInstance(context)

    @Provides
    fun provideUsuarioDao(db: MentorHubDatabase): UsuarioDao = db.usuarioDao()

    @Provides
    fun provideMateriaDao(db: MentorHubDatabase): MateriaDao = db.materiaDao()

    @Provides
    fun provideAsesoriaDao(db: MentorHubDatabase): AsesoriaDao = db.asesoriaDao()

    @Provides
    fun provideInscripcionDao(db: MentorHubDatabase): InscripcionDao = db.inscripcionDao()
}
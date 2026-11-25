package com.example.aplicacionesmoviles.mentorhub

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// ---------------------------------------------------------------
//  @HiltAndroidApp -> arranca Hilt cuando arranca la app.
//  Crea el "contenedor" donde viven las dependencias (base de datos,
//  DAOs, repositorios). Se registra en AndroidManifest.xml con
//  android:name=".mentorhub.MentorHubApplication"
// ---------------------------------------------------------------
@HiltAndroidApp
class MentorHubApplication : Application()